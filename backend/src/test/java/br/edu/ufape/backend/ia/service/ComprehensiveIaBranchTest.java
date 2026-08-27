package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.ia.contrato.IaContratoImpl;
import br.edu.ufape.backend.ia.controller.MetricasPesquisaController;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.exception.IaProcessamentoException;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprehensiveIaBranchTest {

	@Mock
	private RegulamentoChunkRepository regulamentoRepository;
	@Mock
	private HuggingFaceEmbeddingService embeddingService;
	@Mock
	private RestTemplate restTemplate;

	private GroqRagService groqService;
	private IngestaoDocumentoNormativoService ingestaoService;
	private IaContratoImpl iaContrato;

	@BeforeEach
	void setUp() {
		groqService = new GroqRagService(new ObjectMapper());
		ReflectionTestUtils.setField(groqService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(groqService, "apiKey", "dummy-api-key");

		ingestaoService = new IngestaoDocumentoNormativoService(new ObjectMapper(), regulamentoRepository,
				embeddingService);
		ReflectionTestUtils.setField(ingestaoService, "restTemplate", restTemplate);
		ReflectionTestUtils.setField(ingestaoService, "apiKey", "dummy-api-key");

		iaContrato = new IaContratoImpl(groqService, regulamentoRepository, embeddingService);
	}

	@Test
    @DisplayName("GroqRagService: Falha na chamada HTTP de parecer gera DTO de falha tecnica")
    void deveGerarParecerComFalhaTecnicaHttp() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Timeout na Groq"));

        ParecerResponseDTO dto = groqService.gerarParecerComContextoRAG(
                "Curso", "UFAPE", "ACC", "ENSINO", 20, "Art 12");
        assertEquals("AMBIGUO", dto.decisaoIA());
        assertEquals("Falha Técnica", dto.artigoRegulamento());
    }

	@Test
	@DisplayName("GroqRagService: Extração de texto com sucesso e com data de realização")
	void deveExtrairTextoComData() {
		String jsonRetorno = """
				{
				  "choices": [{
				    "message": {
				      "content": "{\\"titulo\\": \\"Minicurso\\", \\"instituicaoResponsavel\\": \\"UFAPE\\", \\"dataRealizacao\\": \\"2026-05-10\\", \\"cargaHoraria\\": 20, \\"natureza\\": \\"ACC\\", \\"categoria\\": \\"ENSINO\\"}"
				    }
				  }]
				}
				""";
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(jsonRetorno, HttpStatus.OK));

		ExtracaoCertificadoResponseDTO dto = groqService.extrairDadosDeTexto("Certificado de conclusao...");
		assertEquals("Minicurso", dto.titulo());
		assertEquals("2026-05-10", dto.dataRealizacao().toString());
	}

	@Test
    @DisplayName("GroqRagService: Extração de imagem com falha HTTP lança IaProcessamentoException")
    void deveLancarIaProcessamentoExceptionEmFalhaExtracao() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Erro 500 Groq"));

        assertThrows(IaProcessamentoException.class,
                () -> groqService.extrairDadosDeImagem(new byte[]{1, 2}, "image/png"));
    }

	@Test
    @DisplayName("IngestaoService: Extração com IA com sucesso via Groq")
    void deveIngerirComSucessoViaIa() {
        when(embeddingService.gerarEmbedding(any())).thenReturn(new float[384]);
        String jsonGroq = """
                {
                  "choices": [{
                    "message": {
                      "content": "[{\\"artigo\\": \\"Art. 12\\", \\"conteudoTexto\\": \\"Atividades de Ensino valem 40h\\"}]"
                    }
                  }]
                }
                """;
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonGroq, HttpStatus.OK));

        String texto = "7.1 ATIVIDADES COMPLEMENTARES " + "A".repeat(120);
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "norma.txt", "text/plain", texto.getBytes());

        IngestaoNormativaResponseDTO res = ingestaoService.ingerirDocumentoNormativo(arquivo, false);
        assertEquals("SUCESSO", res.status());
        verify(regulamentoRepository, atLeastOnce()).save(any());
    }

	@Test
	@DisplayName("IngestaoService: PDF sem camada de texto legivel retorna status ERRO")
	void deveRetornarErroParaPdfSemTexto() throws Exception {
		ReflectionTestUtils.setField(ingestaoService, "apiKey", "");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PDDocument doc = new PDDocument()) {
			doc.addPage(new PDPage());
			doc.save(baos);
		}

		MockMultipartFile pdfFile = new MockMultipartFile("arquivo", "regulamento.pdf", "application/pdf",
				baos.toByteArray());
		IngestaoNormativaResponseDTO res = ingestaoService.ingerirDocumentoNormativo(pdfFile, false);
		assertEquals("ERRO", res.status());
		assertEquals(0, res.totalChunksExtraidos());
	}

	@Test
	@DisplayName("IngestaoService: Documento com normas validas via fallback deterministico retorna SUCESSO")
	void deveIngerirComSucessoDocumentoValido() {
		ReflectionTestUtils.setField(ingestaoService, "apiKey", "");
		when(embeddingService.gerarEmbedding(any())).thenReturn(new float[384]);
		String texto = "Quadro 5 - Sintese da Carga Horaria: ACC = 90 horas e ACEX = 320 horas.";
		MockMultipartFile txtFile = new MockMultipartFile("arquivo", "regulamento.txt", "text/plain", texto.getBytes());

		IngestaoNormativaResponseDTO res = ingestaoService.ingerirDocumentoNormativo(txtFile, false);
		assertEquals("SUCESSO", res.status());
		assertTrue(res.totalChunksExtraidos() > 0);
	}

	@Test
	@DisplayName("IaContratoImpl: PDF com texto curto aciona renderização de página (OCR visual)")
	void deveRenderizarPaginaQuandoTextoCurto() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PDDocument doc = new PDDocument()) {
			doc.addPage(new PDPage());
			doc.save(baos);
		}

		String jsonGroq = """
				{
				  "choices": [{
				    "message": {
				      "content": "{\\"titulo\\": \\"Certificado Renderizado\\", \\"instituicaoResponsavel\\": \\"UFAPE\\", \\"cargaHoraria\\": 10, \\"natureza\\": \\"ACC\\", \\"categoria\\": \\"EVENTOS\\"}"
				    }
				  }]
				}
				""";
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
				.thenReturn(new ResponseEntity<>(jsonGroq, HttpStatus.OK));

		MockMultipartFile pdfEscaneado = new MockMultipartFile("arquivo", "scan.pdf", "application/pdf",
				baos.toByteArray());
		ExtracaoCertificadoResponseDTO res = iaContrato.extrairDadosArquivo(pdfEscaneado);
		assertEquals("Certificado Renderizado", res.titulo());
	}

	@Test
	@DisplayName("MetricasPesquisaController: Total avaliadas zerado retorna acurácia 0.0")
	void deveCalcularMetricasSemAvaliadas() {
		IaCertificadoFacade facade = mock(IaCertificadoFacade.class);
		when(facade.contarAvaliadas()).thenReturn(0L);
		when(facade.contarConcordancias()).thenReturn(0L);
		when(facade.calcularTempoMedioMs()).thenReturn(0.0);

		MetricasPesquisaController controller = new MetricasPesquisaController(facade);
		ResponseEntity<Map<String, Object>> response = controller.obterMetricasEmpiricas();
		assertEquals(0.0, response.getBody().get("acuraciaObservada"));
	}
}
