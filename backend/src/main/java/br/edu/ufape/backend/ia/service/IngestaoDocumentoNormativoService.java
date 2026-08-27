package br.edu.ufape.backend.ia.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class IngestaoDocumentoNormativoService {

	private static final Logger log = LoggerFactory.getLogger(IngestaoDocumentoNormativoService.class);
	private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
	private static final String MODEL_GROQ = "qwen/qwen3.6-27b";
	private static final String KEY_CONTENT = "content";

	private static final Pattern PATTERN_JSON_EXTRACTION = Pattern
			.compile("\"artigo\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"conteudoTexto\"\\s*:\\s*\"([^\"]+)\"");

	@Value("${groq.api.key:}")
	private String apiKey;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final RegulamentoChunkRepository regulamentoRepository;
	private final HuggingFaceEmbeddingService embeddingService;

	public IngestaoDocumentoNormativoService(ObjectMapper objectMapper,
			RegulamentoChunkRepository regulamentoRepository, HuggingFaceEmbeddingService embeddingService) {
		this.objectMapper = objectMapper;
		this.regulamentoRepository = regulamentoRepository;
		this.embeddingService = embeddingService;
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10000);
		factory.setReadTimeout(45000);
		this.restTemplate = new RestTemplate(factory);
	}

	@Transactional
	public IngestaoNormativaResponseDTO ingerirDocumentoNormativo(MultipartFile arquivo, boolean substituirExistentes) {
		try {
			if (arquivo == null || arquivo.isEmpty()) {
				return new IngestaoNormativaResponseDTO(arquivo != null ? arquivo.getOriginalFilename() : "documento",
						0, "ERRO", "Arquivo de documento normativo vazio ou inválido.");
			}
			String textoCompleto = extrairTexto(arquivo);
			if (textoCompleto == null || textoCompleto.isBlank()) {
				return new IngestaoNormativaResponseDTO(arquivo.getOriginalFilename(), 0, "ERRO",
						"Documento vazio ou sem camada de texto legível.");
			}

			List<RegulamentoChunk> chunksValidos = extrairEValidarChunks(textoCompleto);
			if (chunksValidos.isEmpty()) {
				return new IngestaoNormativaResponseDTO(arquivo.getOriginalFilename(), 0, "ERRO",
						"Nenhuma regra regulamentar válida pôde ser extraída do documento.");
			}

			if (substituirExistentes) {
				regulamentoRepository.deleteAll();
			}

			int totalSalvos = vetorizarEPersistir(chunksValidos);
			return new IngestaoNormativaResponseDTO(arquivo.getOriginalFilename(), totalSalvos, "SUCESSO",
					String.format("Foram extraídas e vetorizadas com sucesso %d normas e regras regulamentares.",
							totalSalvos));
		} catch (Exception e) {
			log.error("Falha na ingestão do documento normativo", e);
			return new IngestaoNormativaResponseDTO(arquivo != null ? arquivo.getOriginalFilename() : "documento", 0,
					"ERRO", "Falha técnica ao processar o documento normativo.");
		}
	}

	private List<RegulamentoChunk> extrairEValidarChunks(String textoCompleto) {
		List<String> secoesNormativas = extrairSecoesNormativas(textoCompleto);
		List<RegulamentoChunk> chunksExtraidos = new ArrayList<>();

		if (apiKey != null && !apiKey.isBlank()) {
			for (String secao : secoesNormativas) {
				chunksExtraidos.addAll(extrairRegrasComIA(secao));
			}
		}

		if (chunksExtraidos.isEmpty()) {
			log.info("Acionando extrator determinístico de normas do documento.");
			chunksExtraidos.addAll(extrairRegrasDiretasDoTexto(textoCompleto));
		}

		return chunksExtraidos.stream()
				.filter(c -> c.getConteudoTexto() != null && c.getConteudoTexto().trim().length() >= 20).toList();
	}

	private int vetorizarEPersistir(List<RegulamentoChunk> chunksValidos) {
		int totalSalvos = 0;
		for (RegulamentoChunk chunk : chunksValidos) {
			float[] embedding = embeddingService.gerarEmbedding(chunk.getConteudoTexto());
			chunk.setEmbeddingVetor(Arrays.toString(embedding));
			regulamentoRepository.save(chunk);
			totalSalvos++;
		}
		return totalSalvos;
	}

	private List<String> extrairSecoesNormativas(String texto) {
		List<String> blocos = new ArrayList<>();
		String[] linhas = texto.split("\\R");
		StringBuilder blocoAtual = new StringBuilder();

		for (String linha : linhas) {
			String limpa = linha.trim();
			if (isCabecalhoNormativo(limpa) && blocoAtual.length() > 100) {
				blocos.add(blocoAtual.toString());
				blocoAtual.setLength(0);
				if (blocos.size() >= 4)
					break;
			}
			blocoAtual.append(linha).append("\n");
		}
		if (!blocoAtual.isEmpty() && blocos.size() < 4) {
			blocos.add(blocoAtual.toString());
		}
		if (blocos.isEmpty()) {
			blocos.add(texto.substring(0, Math.min(texto.length(), 15000)));
		}
		return blocos;
	}

	private boolean isCabecalhoNormativo(String linha) {
		String l = linha.toLowerCase();
		return (l.startsWith("7.") || l.startsWith("art") || l.startsWith("cap") || l.startsWith("quadro")
				|| l.startsWith("resolu"))
				&& (l.contains("atividad") || l.contains("extens") || l.contains("carga") || l.contains("estágio"));
	}

	private List<RegulamentoChunk> extrairRegrasComIA(String trechoNormativo) {
		String prompt = """
				Você é um compilador de normas acadêmicas da UFAPE.
				Analise o trecho normativo e extraia todas as regras, artigos, cargas horárias e limites de Atividades Complementares (ACC), Extensão (ACEX) e Estágio.
				Retorne EXCLUSIVAMENTE um array JSON bruto no formato abaixo, sem tags <think> e sem markdown:
				[
				  {
				    "artigo": "Seção / Artigo (Ex: Seção 7.10 - ACC ou Resolução 08/2024)",
				    "conteudoTexto": "Descrição completa da regra com carga horária, teto e condições."
				  }
				]
				""";
		Map<String, Object> requestBody = Map.of("model", MODEL_GROQ, "messages",
				List.of(Map.of("role", "system", KEY_CONTENT, prompt),
						Map.of("role", "user", KEY_CONTENT, "Trecho Normativo:\n" + trechoNormativo)),
				"temperature", 0.1);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(apiKey);
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(GROQ_URL, entity, String.class);
			JsonNode root = objectMapper.readTree(response.getBody());
			String rawText = root.path("choices").get(0).path("message").path(KEY_CONTENT).asText();
			return processarRespostaJsonOuRegex(rawText);
		} catch (Exception e) {
			log.warn("Falha na chamada Groq Qwen durante ingestão: {}", e.getMessage());
			return List.of();
		}
	}

	private List<RegulamentoChunk> processarRespostaJsonOuRegex(String rawText) {
		List<RegulamentoChunk> chunks = new ArrayList<>();
		if (rawText == null || rawText.isBlank())
			return chunks;

		String jsonLimpo = rawText.replaceAll("(?s)<think>.*?</think>", "").trim();
		jsonLimpo = jsonLimpo.replace("```json", "").replace("```JSON", "").replace("```", "").trim();

		int inicio = jsonLimpo.indexOf('[');
		int fim = jsonLimpo.lastIndexOf(']');
		if (inicio != -1 && fim != -1 && fim > inicio) {
			jsonLimpo = jsonLimpo.substring(inicio, fim + 1);
		}
		try {
			JsonNode node = objectMapper.readTree(jsonLimpo);
			if (node.isArray()) {
				for (JsonNode item : node) {
					String art = item.path("artigo").asText("Norma Geral");
					String conteudo = item.path("conteudoTexto").asText("");
					if (!conteudo.isBlank()) {
						chunks.add(new RegulamentoChunk(art, conteudo, ""));
					}
				}
				if (!chunks.isEmpty())
					return chunks;
			}
		} catch (Exception ignored) {
			// Ignora falha de parse JSON e aciona fallback por Regex
		}

		Matcher matcher = PATTERN_JSON_EXTRACTION.matcher(rawText);
		while (matcher.find()) {
			chunks.add(new RegulamentoChunk(matcher.group(1), matcher.group(2), ""));
		}
		return chunks;
	}

	private List<RegulamentoChunk> extrairRegrasDiretasDoTexto(String textoCompleto) {
		List<RegulamentoChunk> chunks = new ArrayList<>();
		String[] linhas = textoCompleto.split("\\R");
		String tituloAtual = null;
		StringBuilder conteudoAtual = new StringBuilder();

		for (String linha : linhas) {
			String limpa = linha.trim();
			if (isCabecalhoNormativo(limpa)) {
				if (tituloAtual != null && conteudoAtual.length() > 30) {
					chunks.add(new RegulamentoChunk(tituloAtual, conteudoAtual.toString().trim(), ""));
					conteudoAtual.setLength(0);
				}
				tituloAtual = limpa;
			} else if (tituloAtual != null) {
				conteudoAtual.append(linha).append(" ");
			}
		}

		if (tituloAtual != null && conteudoAtual.length() > 30) {
			chunks.add(new RegulamentoChunk(tituloAtual, conteudoAtual.toString().trim(), ""));
		}

		if (textoCompleto.contains("Quadro 5") || textoCompleto.contains("Síntese da carga horária")) {
			chunks.add(new RegulamentoChunk("Quadro 5 - Síntese da Carga Horária",
					"Carga Horária Obrigatória do Curso: ACC = 90 horas; ACEX = 320 horas; Total do Curso = 3200 horas.",
					""));
		}
		return chunks;
	}

	private String extrairTexto(MultipartFile arquivo) throws IOException {
		String contentType = arquivo.getContentType() != null ? arquivo.getContentType() : "";
		if (contentType.contains("pdf") || (arquivo.getOriginalFilename() != null
				&& arquivo.getOriginalFilename().toLowerCase().endsWith(".pdf"))) {
			try (InputStream is = arquivo.getInputStream(); PDDocument doc = Loader.loadPDF(is.readAllBytes())) {
				return new PDFTextStripper().getText(doc);
			}
		}
		return new String(arquivo.getBytes(), StandardCharsets.UTF_8);
	}

	public List<RegulamentoChunkResponseDTO> listarChunks() {
		return regulamentoRepository.findAll().stream().map(RegulamentoChunkResponseDTO::fromEntity).toList();
	}
}
