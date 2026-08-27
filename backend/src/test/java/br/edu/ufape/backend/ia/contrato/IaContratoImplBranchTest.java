package br.edu.ufape.backend.ia.contrato;

import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import br.edu.ufape.backend.ia.service.GroqRagService;
import br.edu.ufape.backend.ia.service.HuggingFaceEmbeddingService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaContratoImplBranchTest {

	@Mock
	private GroqRagService groqRagService;
	@Mock
	private RegulamentoChunkRepository regulamentoRepository;
	@Mock
	private HuggingFaceEmbeddingService embeddingService;

	private IaContratoImpl contrato;

	@BeforeEach
	void setUp() {
		contrato = new IaContratoImpl(groqRagService, regulamentoRepository, embeddingService);
	}

	@Test
	@DisplayName("Branch: converterStringParaFloatArray com nulos, inválidos e valores formatados")
	void deveConverterStringParaFloatArray() {
		float[] vNull = ReflectionTestUtils.invokeMethod(contrato, "converterStringParaFloatArray", (String) null);
		assertEquals(384, vNull.length);

		float[] vSemColchete = ReflectionTestUtils.invokeMethod(contrato, "converterStringParaFloatArray", "0.1, 0.2");
		assertEquals(384, vSemColchete.length);

		float[] vValido = ReflectionTestUtils.invokeMethod(contrato, "converterStringParaFloatArray",
				"[0.5, 0.25, invalido]");
		assertEquals(3, vValido.length);
		assertEquals(0.5f, vValido[0]);
		assertEquals(0.25f, vValido[1]);
		assertEquals(0.0f, vValido[2]);
	}

	@Test
    @DisplayName("Branch: recuperarArtigosMaisRelevantes com base vazia")
    void deveRetornarFallbackBaseVazia() {
        when(embeddingService.gerarEmbedding(anyString())).thenReturn(new float[384]);
        when(regulamentoRepository.findAll()).thenReturn(List.of());

        String resVazia = ReflectionTestUtils.invokeMethod(contrato, "recuperarArtigosMaisRelevantes", "consulta");
        assertEquals("Regulamento Geral de ACC e ACEX da UFAPE.", resVazia);
    }

	@Test
    @DisplayName("Branch: recuperarArtigosMaisRelevantes com múltiplos chunks e ordenação")
    void deveRecuperarEOrdenarArtigos() {
        when(embeddingService.gerarEmbedding(anyString())).thenReturn(new float[384]);
        RegulamentoChunk chunk1 = new RegulamentoChunk("Art. 1", "Texto do artigo 1", "[0.1]");
        RegulamentoChunk chunk2 = new RegulamentoChunk("Art. 2", "Texto do artigo 2", "[0.2]");
        when(regulamentoRepository.findAll()).thenReturn(List.of(chunk1, chunk2));
        when(embeddingService.calcularSimilaridadeCosseno(any(), any())).thenReturn(0.8);

        String resComDados = ReflectionTestUtils.invokeMethod(contrato, "recuperarArtigosMaisRelevantes", "consulta");
        assertTrue(resComDados.contains("Art. 1"));
        assertTrue(resComDados.contains("Art. 2"));
    }

	@Test
    @DisplayName("Branch: recuperarArtigosMaisRelevantes tratando exceção no embedding")
    void deveTratarExcecaoNaRecuperacao() {
        when(embeddingService.gerarEmbedding(anyString())).thenThrow(new RuntimeException("Falha de rede"));

        String resFalha = ReflectionTestUtils.invokeMethod(contrato, "recuperarArtigosMaisRelevantes", "consulta");
        assertEquals("Normas Institucionais Gerais da UFAPE.", resFalha);
    }

	@Test
	@DisplayName("Branch: extrairDadosArquivo com imagem e texto genérico")
	void deveExtrairDadosDeDiferentesTiposDeArquivo() {
		MockMultipartFile imagem = new MockMultipartFile("arquivo", "cert.png", "image/png", new byte[]{1, 2, 3});
		contrato.extrairDadosArquivo(imagem);
		verify(groqRagService).extrairDadosDeImagem(any(), eq("image/png"));

		MockMultipartFile outro = new MockMultipartFile("arquivo", "cert.outro", "application/octet-stream",
				new byte[]{1, 2});
		contrato.extrairDadosArquivo(outro);
		verify(groqRagService).extrairDadosDeTexto(contains("Certificado: cert.outro"));
	}

	@Test
	@DisplayName("Branch: extrairDadosArquivo de PDF escaneado (renderiza página)")
	void deveRenderizarPaginaDePdfEscaneado() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PDDocument doc = new PDDocument()) {
			doc.addPage(new PDPage());
			doc.save(baos);
		}

		MockMultipartFile pdfEscaneado = new MockMultipartFile("arquivo", "scan.pdf", "application/pdf",
				baos.toByteArray());
		contrato.extrairDadosArquivo(pdfEscaneado);
		verify(groqRagService).extrairDadosDeImagem(any(), eq("image/jpeg"));
	}
}
