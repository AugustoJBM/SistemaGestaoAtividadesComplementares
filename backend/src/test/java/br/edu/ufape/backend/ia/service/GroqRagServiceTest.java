package br.edu.ufape.backend.ia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.exception.IaProcessamentoException;
import tools.jackson.databind.ObjectMapper;

class GroqRagServiceTest {

    private GroqRagService service;
    private RestTemplate restTemplateMock;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new GroqRagService(objectMapper);
        restTemplateMock = mock(RestTemplate.class);
        ReflectionTestUtils.setField(service, "restTemplate", restTemplateMock);
        ReflectionTestUtils.setField(service, "apiKey", "gsk_test_token_12345");
    }

    @Test
    @DisplayName("Deve retornar parecer AMBIGUO quando a chave Groq não estiver configurada")
    void deveRetornarParecerInconclusivoSemChave() {
        ReflectionTestUtils.setField(service, "apiKey", "");
        ParecerResponseDTO parecer = service.gerarParecerComContextoRAG(
                "Monitoria", "UFAPE", "ACC", "ENSINO", 30, "Art. 12: Monitoria até 40h");

        assertNotNull(parecer);
        assertEquals("ACC", parecer.naturezaSugerida());
        assertEquals("ENSINO", parecer.categoriaSugerida());
        assertEquals("AMBIGUO", parecer.decisaoIA());
        assertEquals(0.0, parecer.scoreConfianca());
    }

    @Test
    @DisplayName("Deve processar resposta de parecer estruturada em JSON")
    void deveGerarParecerComRespostaJsonValida() {
        String jsonRetorno = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\"naturezaSugerida\\": \\"ACEX\\", \\"categoriaSugerida\\": \\"EXTENSAO\\", \\"cargaHorariaAproveitavel\\": 40, \\"artigoRegulamento\\": \\"Art. 14\\", \\"justificativaTecnica\\": \\"Projeto comunitário válido\\", \\"scoreConfianca\\": 0.98, \\"decisaoIA\\": \\"DEFERIDO\\"}"
                    }
                  }]
                }
                """;

        when(restTemplateMock.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonRetorno, HttpStatus.OK));

        ParecerResponseDTO parecer = service.gerarParecerComContextoRAG(
                "Projeto Social", "UFAPE", "ACEX", "EXTENSAO", 40, "Art. 14 Extensao");

        assertNotNull(parecer);
        assertEquals("ACEX", parecer.naturezaSugerida());
        assertEquals("EXTENSAO", parecer.categoriaSugerida());
        assertEquals(40, parecer.cargaHorariaAproveitavel());
        assertEquals("Art. 14", parecer.artigoRegulamento());
        assertEquals("DEFERIDO", parecer.decisaoIA());
        assertEquals(0.98, parecer.scoreConfianca());
    }

    @Test
    @DisplayName("Deve acionar o fallback por regex quando a IA retornar JSON com tags markdown e formatação quebrada")
    void deveProcessarParecerComFallbackRegex() {
        String respostaComTags = """
                {
                  "choices": [{
                    "message": {
                      "content": "<think>Pensando...</think>```json \\n \\"naturezaSugerida\\": \\"ACC\\", \\"categoriaSugerida\\": \\"PESQUISA\\", \\"cargaHorariaAproveitavel\\": 20, \\"artigoRegulamento\\": \\"Art. 13\\", \\"justificativaTecnica\\": \\"Iniciação científica\\", \\"decisaoIA\\": \\"DEFERIDO\\" ```"
                    }
                  }]
                }
                """;

        when(restTemplateMock.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(respostaComTags, HttpStatus.OK));

        ParecerResponseDTO parecer = service.gerarParecerComContextoRAG(
                "PIBIC", "UFAPE", "ACC", "PESQUISA", 20, "Art. 13 PIBIC");

        assertNotNull(parecer);
        assertEquals("ACC", parecer.naturezaSugerida());
        assertEquals("PESQUISA", parecer.categoriaSugerida());
        assertEquals("Art. 13", parecer.artigoRegulamento());
        assertEquals("DEFERIDO", parecer.decisaoIA());
    }

    @Test
    @DisplayName("Deve extrair dados de texto de certificado com sucesso")
    void deveExtrairDadosDeTexto() {
        String respostaExtracao = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\"titulo\\": \\"Minicurso Spring Boot\\", \\"instituicaoResponsavel\\": \\"UFAPE\\", \\"dataRealizacao\\": \\"2026-05-10\\", \\"cargaHoraria\\": 20, \\"natureza\\": \\"ACC\\", \\"categoria\\": \\"ENSINO\\"}"
                    }
                  }]
                }
                """;

        when(restTemplateMock.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(respostaExtracao, HttpStatus.OK));

        ExtracaoCertificadoResponseDTO extracao = service.extrairDadosDeTexto("Certificamos que concluiu o curso...");

        assertNotNull(extracao);
        assertEquals("Minicurso Spring Boot", extracao.titulo());
        assertEquals("UFAPE", extracao.instituicaoResponsavel());
        assertEquals(20, extracao.cargaHoraria());
        assertEquals("ACC", extracao.natureza());
    }

    @Test
    @DisplayName("Deve extrair dados de imagem de certificado com sucesso")
    void deveExtrairDadosDeImagem() {
        String respostaExtracao = """
                {
                  "choices": [{
                    "message": {
                      "content": "{\\"titulo\\": \\"Congresso Brasileiro de Software\\", \\"instituicaoResponsavel\\": \\"SBC\\", \\"dataRealizacao\\": \\"2026-06-15\\", \\"cargaHoraria\\": 30, \\"natureza\\": \\"ACC\\", \\"categoria\\": \\"EVENTOS\\"}"
                    }
                  }]
                }
                """;

        when(restTemplateMock.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(respostaExtracao, HttpStatus.OK));

        byte[] imagemBytes = new byte[] { 1, 2, 3, 4 };
        ExtracaoCertificadoResponseDTO extracao = service.extrairDadosDeImagem(imagemBytes, "image/jpeg");

        assertNotNull(extracao);
        assertEquals("Congresso Brasileiro de Software", extracao.titulo());
        assertEquals("SBC", extracao.instituicaoResponsavel());
        assertEquals(30, extracao.cargaHoraria());
    }

    @Test
    @DisplayName("Deve lançar IaProcessamentoException ao tentar extrair dados sem chave configurada")
    void deveLancarExcecaoAoExtrairSemChave() {
        ReflectionTestUtils.setField(service, "apiKey", "");

        assertThrows(IaProcessamentoException.class, () -> service.extrairDadosDeTexto("Texto"));
        assertThrows(IaProcessamentoException.class, () -> service.extrairDadosDeImagem(new byte[] { 1 }, "image/png"));
    }
}