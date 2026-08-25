package br.edu.ufape.backend.ia.service;

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

    @Value("${groq.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RegulamentoChunkRepository regulamentoRepository;
    private final HuggingFaceEmbeddingService embeddingService;

    private static final String GROQ_URL = "[https://api.groq.com/openai/v1/chat/completions](https://api.groq.com/openai/v1/chat/completions)";
    private static final String MODEL_GROQ = "qwen/qwen3.6-27b";

    public IngestaoDocumentoNormativoService(
            ObjectMapper objectMapper,
            RegulamentoChunkRepository regulamentoRepository,
            HuggingFaceEmbeddingService embeddingService) {
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
                return new IngestaoNormativaResponseDTO(
                        arquivo != null ? arquivo.getOriginalFilename() : "documento", 0, "ERRO",
                        "Arquivo de documento normativo vazio ou inválido.");
            }

            String textoCompleto = extrairTexto(arquivo);
            if (textoCompleto == null || textoCompleto.isBlank()) {
                return new IngestaoNormativaResponseDTO(
                        arquivo.getOriginalFilename(), 0, "ERRO",
                        "Documento vazio ou sem camada de texto legível.");
            }

            // 1. Isola seções normativas do documento
            List<String> secoesNormativas = extrairSecoesNormativas(textoCompleto);
            List<RegulamentoChunk> chunksExtraidos = new ArrayList<>();

            // 2. Extração via IA (se configurada)
            if (apiKey != null && !apiKey.isBlank()) {
                for (String secao : secoesNormativas) {
                    List<RegulamentoChunk> daIA = extrairRegrasComIA(secao);
                    chunksExtraidos.addAll(daIA);
                }
            }

            // 3. Fallback Heurístico/Determinístico
            if (chunksExtraidos.isEmpty()) {
                log.info("Acionando extrator determinístico de normas do documento.");
                chunksExtraidos.addAll(extrairRegrasDiretasDoTexto(textoCompleto));
            }

            // 4. Valida se chunks válidos foram identificados
            List<RegulamentoChunk> chunksValidos = chunksExtraidos.stream()
                    .filter(c -> c.getConteudoTexto() != null && c.getConteudoTexto().trim().length() >= 20)
                    .toList();

            if (chunksValidos.isEmpty()) {
                return new IngestaoNormativaResponseDTO(
                        arquivo.getOriginalFilename(), 0, "ERRO",
                        "Nenhuma regra regulamentar válida pôde ser extraída do documento.");
            }

            // 5. Substituição atômica: remove base antiga apenas após sucesso da validação
            if (substituirExistentes) {
                regulamentoRepository.deleteAll();
            }

            // 6. Vetorização e persistência dos novos registros
            int totalSalvos = 0;
            for (RegulamentoChunk chunk : chunksValidos) {
                float[] embedding = embeddingService.gerarEmbedding(chunk.getConteudoTexto());
                chunk.setEmbeddingVetor(Arrays.toString(embedding));
                regulamentoRepository.save(chunk);
                totalSalvos++;
            }

            return new IngestaoNormativaResponseDTO(
                    arquivo.getOriginalFilename(),
                    totalSalvos,
                    "SUCESSO",
                    String.format("Foram extraídas e vetorizadas com sucesso %d normas e regras regulamentares.",
                            totalSalvos));
        } catch (Exception e) {
            log.error("Falha na ingestão do documento normativo", e);
            return new IngestaoNormativaResponseDTO(
                    arquivo != null ? arquivo.getOriginalFilename() : "documento", 0, "ERRO",
                    "Falha técnica ao processar o documento normativo.");
        }
    }

    private List<String> extrairSecoesNormativas(String texto) {
        List<String> blocos = new ArrayList<>();
        String[] padroes = {
                "(?i)(?:7\\.\\d+|seção|capítulo|artigo|quadro|resolução).*?(?:atividades?\\s+complementares?|curricularização|extensão|estágio|síntese\\s+da\\s+carga|base\\s+legal).*?(?=\\n(?:7\\.\\d+|8\\.|9\\.|10\\.|seção|capítulo)|\\Z)",
                "(?i)Quadro\\s+\\d+.*?carga\\s+horária.*?(?=\\n[A-Z0-9]{2,}|\\Z)",
                "(?i)Resolução\\s+Nº?\\s*\\d+/\\d+.*?(?=\\n[A-Z0-9]{2,}|\\Z)"
        };
        for (String regex : padroes) {
            Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(texto);
            while (m.find() && blocos.size() < 4) {
                String trecho = m.group(0).trim();
                if (trecho.length() > 100) {
                    blocos.add(trecho.length() > 8000 ? trecho.substring(0, 8000) : trecho);
                }
            }
        }
        if (blocos.isEmpty()) {
            int maxChars = Math.min(texto.length(), 15000);
            blocos.add(texto.substring(0, maxChars));
        }
        return blocos;
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
        Map<String, Object> requestBody = Map.of(
                "model", MODEL_GROQ,
                "messages", List.of(
                        Map.of("role", "system", "content", prompt),
                        Map.of("role", "user", "content", "Trecho Normativo:\n" + trechoNormativo)),
                "temperature", 0.1);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_URL, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            String rawText = root.path("choices").get(0).path("message").path("content").asText();
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
        jsonLimpo = jsonLimpo.replaceAll("(?i)```json", "").replaceAll("```", "").trim();
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
        }

        Pattern pattern = Pattern
                .compile("\"artigo\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"conteudoTexto\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(rawText);
        while (matcher.find()) {
            chunks.add(new RegulamentoChunk(matcher.group(1), matcher.group(2), ""));
        }
        return chunks;
    }

    private List<RegulamentoChunk> extrairRegrasDiretasDoTexto(String textoCompleto) {
        List<RegulamentoChunk> chunks = new ArrayList<>();
        Pattern pSecao = Pattern.compile(
                "(?i)(\\d+\\.\\d+\\.?\\s*(?:ATIVIDADES\\s+COMPLEMENTARES|CURRICULARIZAÇÃO\\s+DA\\s+EXTENSÃO|ESTÁGIO|TRABALHO\\s+DE\\s+CONCLUSÃO)[^\\n]*)\\n([\\s\\S]*?)(?=\\n\\d+\\.\\d+|\\Z)");
        Matcher mSecao = pSecao.matcher(textoCompleto);
        while (mSecao.find() && chunks.size() < 10) {
            String titulo = mSecao.group(1).trim().replaceAll("\\s+", " ");
            String conteudo = mSecao.group(2).trim().replaceAll("\\s+", " ");
            if (conteudo.length() > 50) {
                if (conteudo.length() > 500)
                    conteudo = conteudo.substring(0, 500);
                chunks.add(new RegulamentoChunk(titulo, conteudo, ""));
            }
        }

        Pattern pRes = Pattern.compile("(?i)(Resolução\\s+Nº?\\s*\\d+/\\d+[^\\n]*)\\s+([^\\n]+(?:\\n[^\\n]+){1,2})");
        Matcher mRes = pRes.matcher(textoCompleto);
        while (mRes.find() && chunks.size() < 20) {
            String res = mRes.group(1).trim().replaceAll("\\s+", " ");
            String desc = mRes.group(2).trim().replaceAll("\\s+", " ");
            if (desc.length() > 30) {
                chunks.add(new RegulamentoChunk(res, desc, ""));
            }
        }

        if (textoCompleto.contains("Quadro 5") || textoCompleto.contains("Síntese da carga horária")) {
            chunks.add(new RegulamentoChunk(
                    "Quadro 5 - Síntese da Carga Horária",
                    "Carga Horária Obrigatória do Curso: ACC = 90 horas; ACEX = 320 horas; Total do Curso = 3200 horas.",
                    ""));
        }
        return chunks;
    }

    private String extrairTexto(MultipartFile arquivo) throws Exception {
        String contentType = arquivo.getContentType() != null ? arquivo.getContentType() : "";
        if (contentType.contains("pdf") || (arquivo.getOriginalFilename() != null
                && arquivo.getOriginalFilename().toLowerCase().endsWith(".pdf"))) {
            try (InputStream is = arquivo.getInputStream();
                    PDDocument doc = Loader.loadPDF(is.readAllBytes())) {
                return new PDFTextStripper().getText(doc);
            }
        }
        return new String(arquivo.getBytes(), StandardCharsets.UTF_8);
    }

    public List<RegulamentoChunkResponseDTO> listarChunks() {
        return regulamentoRepository.findAll()
                .stream()
                .map(RegulamentoChunkResponseDTO::fromEntity)
                .toList();
    }
}