package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class IngestaoDocumentoNormativoServiceTest {

    @Mock
    private RegulamentoChunkRepository regulamentoRepository;

    @Mock
    private HuggingFaceEmbeddingService embeddingService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IngestaoDocumentoNormativoService service;

    @Test
    @DisplayName("Deve retornar mensagem de erro quando o arquivo for vazio")
    void deveRetornarErroQuandoArquivoVazio() {
        MockMultipartFile arquivoVazio = new MockMultipartFile(
                "arquivo", "vazio.txt", "text/plain", new byte[0]);

        IngestaoNormativaResponseDTO resultado = service.ingerirDocumentoNormativo(arquivoVazio, false);

        assertNotNull(resultado);
        assertEquals("ERRO", resultado.status());
        assertEquals(0, resultado.totalChunksExtraidos());
    }

    @Test
    @DisplayName("Deve ingerir e segmentar texto normativo com sucesso usando fallback determinístico")
    void deveIngerirDocumentoComTexto() {
        // O texto precisa ser longo o suficiente (> 50 chars) e respeitar o padrão do
        // título para virar um chunk válido
        String texto = "7.10 ATIVIDADES COMPLEMENTARES\n" +
                "Este é um texto longo o suficiente para passar na validação de tamanho mínimo de 50 caracteres para ser considerado um chunk normativo da base de conhecimento da IA.\n";

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "norma.txt", "text/plain", texto.getBytes());

        // Usamos lenient() para evitar UnnecessaryStubbingException caso ocorra alguma
        // variação de regex do sistema operacional
        lenient().when(embeddingService.gerarEmbedding(any())).thenReturn(new float[384]);

        IngestaoNormativaResponseDTO resultado = service.ingerirDocumentoNormativo(arquivo, false);

        assertNotNull(resultado);
        assertEquals("SUCESSO", resultado.status());
    }
}