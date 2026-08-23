package br.edu.ufape.backend.ia.contrato;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import br.edu.ufape.backend.ia.service.GroqRagService;
import br.edu.ufape.backend.ia.service.HuggingFaceEmbeddingService;

@Component
public class IaContratoImpl implements IaContrato {

    private static final Logger log = LoggerFactory.getLogger(IaContratoImpl.class);

    private final GroqRagService groqRagService;
    private final RegulamentoChunkRepository regulamentoChunkRepository;
    private final HuggingFaceEmbeddingService embeddingService;

    public IaContratoImpl(
            GroqRagService groqRagService,
            RegulamentoChunkRepository regulamentoChunkRepository,
            HuggingFaceEmbeddingService embeddingService) {
        this.groqRagService = groqRagService;
        this.regulamentoChunkRepository = regulamentoChunkRepository;
        this.embeddingService = embeddingService;
    }

    @Override
    public ExtracaoCertificadoResponseDTO extrairDadosArquivo(MultipartFile arquivo) {
        try {
            String contentType = arquivo.getContentType() != null ? arquivo.getContentType() : "";

            if (contentType.contains("pdf")) {
                try (InputStream is = arquivo.getInputStream();
                        PDDocument document = Loader.loadPDF(is.readAllBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String textoExtraido = stripper.getText(document).trim();

                    if (textoExtraido.length() >= 30) {
                        return groqRagService.extrairDadosDeTexto(textoExtraido);
                    }

                    // PDF escaneado (renderiza página 1 em imagem)
                    PDFRenderer renderer = new PDFRenderer(document);
                    BufferedImage imagemPagina = renderer.renderImageWithDPI(0, 150);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(imagemPagina, "jpeg", baos);
                    return groqRagService.extrairDadosDeImagem(baos.toByteArray(), "image/jpeg");
                }
            } else if (contentType.startsWith("image/")) {
                return groqRagService.extrairDadosDeImagem(arquivo.getBytes(), contentType);
            }

            return groqRagService.extrairDadosDeTexto("Certificado: " + arquivo.getOriginalFilename());
        } catch (Exception e) {
            log.error("Falha ao extrair dados do arquivo com IA: {}", e.getMessage());
            return new ExtracaoCertificadoResponseDTO(
                    "Atividade Complementar", "Instituição Emissora", LocalDate.now(), 20, "ACC", "EVENTOS");
        }
    }

    @Override
    public ParecerResponseDTO gerarParecerConformidade(String titulo, String instituicao, String natureza,
            String categoria, int cargaHoraria) {
        String contextoRecuperado = recuperarArtigosMaisRelevantes(titulo + " " + categoria + " " + natureza);
        return groqRagService.gerarParecerComContextoRAG(
                titulo, instituicao, natureza, categoria, cargaHoraria, contextoRecuperado);
    }

    private String recuperarArtigosMaisRelevantes(String consulta) {
        try {
            float[] queryEmbedding = embeddingService.gerarEmbedding(consulta);
            List<RegulamentoChunk> todos = regulamentoChunkRepository.findAll();

            if (todos.isEmpty()) {
                return "Regulamento Geral de ACC e ACEX da UFAPE.";
            }

            return todos.stream()
                    .sorted((c1, c2) -> Double.compare(
                            embeddingService.calcularSimilaridadeCosseno(queryEmbedding,
                                    converterStringParaFloatArray(c2.getEmbeddingVetor())),
                            embeddingService.calcularSimilaridadeCosseno(queryEmbedding,
                                    converterStringParaFloatArray(c1.getEmbeddingVetor()))))
                    .limit(2)
                    .map(c -> c.getArtigo() + ": " + c.getConteudoTexto())
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            log.warn("Erro ao recuperar contexto RAG para IA: {}", e.getMessage());
            return "Normas Institucionais Gerais da UFAPE.";
        }
    }

    private float[] converterStringParaFloatArray(String str) {
        if (str == null || !str.startsWith("[")) {
            return new float[384];
        }
        String[] partes = str.substring(1, str.length() - 1).split(",");
        float[] vet = new float[partes.length];
        for (int i = 0; i < partes.length; i++) {
            try {
                vet[i] = Float.parseFloat(partes[i].trim());
            } catch (Exception ignored) {
            }
        }
        return vet;
    }
}