package br.edu.ufape.backend.ia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import tools.jackson.databind.ObjectMapper;

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

    @BeforeEach
    void setUp() {
        lenient().when(embeddingService.gerarEmbedding(any())).thenReturn(new float[384]);
    }

    @Test
    @DisplayName("Deve retornar ERRO ao submeter arquivo vazio")
    void deveRetornarErroQuandoArquivoVazio() {
        MockMultipartFile arquivoVazio = new MockMultipartFile(
                "arquivo", "vazio.txt", "text/plain", new byte[0]);

        IngestaoNormativaResponseDTO resultado = service.ingerirDocumentoNormativo(arquivoVazio, false);

        assertNotNull(resultado);
        assertEquals("ERRO", resultado.status());
        assertEquals(0, resultado.totalChunksExtraidos());
        verify(regulamentoRepository, never()).deleteAll();
    }

    @Test
    @DisplayName("Deve extrair Resoluções, Seções e Quadro de Horas com sucesso via fallback determinístico")
    void deveIngerirDocumentoComSecoesEResolucoes() {
        String texto = """
                7.10 ATIVIDADES COMPLEMENTARES
                As atividades complementares possuem limite semestral de 40 horas para monitoria e 60 horas para pesquisa.
                
                Resolução Nº 08/2024 CONSEPE
                Aprova as diretrizes curriculares de extensão para cursos de bacharelado.
                
                Quadro 5 - Síntese da Carga Horária
                Carga Horária Obrigatória: ACC = 90 horas e ACEX = 320 horas.
                """;

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "ppc_bcc.txt", "text/plain", texto.getBytes());

        IngestaoNormativaResponseDTO resultado = service.ingerirDocumentoNormativo(arquivo, true);

        assertNotNull(resultado);
        assertEquals("SUCESSO", resultado.status());
        verify(regulamentoRepository, times(1)).deleteAll();
        verify(regulamentoRepository, times(resultado.totalChunksExtraidos())).save(any(RegulamentoChunk.class));
    }

    @Test
    @DisplayName("Deve listar todos os chunks persistidos")
    void deveListarChunks() {
        RegulamentoChunk chunk = new RegulamentoChunk("Art. 12", "Monitoria até 40h", "[0.1, 0.2]");
        when(regulamentoRepository.findAll()).thenReturn(List.of(chunk));

        List<RegulamentoChunkResponseDTO> lista = service.listarChunks();

        assertEquals(1, lista.size());
        assertEquals("Art. 12", lista.get(0).artigo());
        assertEquals("Monitoria até 40h", lista.get(0).conteudoTexto());
    }
}