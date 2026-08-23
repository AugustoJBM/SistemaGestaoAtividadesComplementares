package br.edu.ufape.backend.ia.controller;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.facade.RegulamentoFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegulamentoIngestaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegulamentoFacade regulamentoFacade;

    @InjectMocks
    private RegulamentoIngestaoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve ingerir documento normativo com sucesso e retornar DTO consolidado")
    void deveIngerirDocumentoNormativo() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "ppc.pdf", MediaType.APPLICATION_PDF_VALUE, "PPC-TEXT".getBytes());

        IngestaoNormativaResponseDTO responseDTO = new IngestaoNormativaResponseDTO(
                "ppc.pdf", 5, "SUCESSO", "Vetorizado com sucesso.");

        when(regulamentoFacade.ingerirDocumentoNormativo(any(), eq(false))).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/v1/regulamentos/ingerir")
                        .file(arquivo)
                        .param("substituirExistentes", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDocumento").value("ppc.pdf"))
                .andExpect(jsonPath("$.totalChunksExtraidos").value(5))
                .andExpect(jsonPath("$.status").value("SUCESSO"));
    }

    @Test
    @DisplayName("Deve listar todos os chunks regulatórios salvos no banco")
    void deveListarChunksRegulamento() throws Exception {
        RegulamentoChunkResponseDTO chunkDTO = new RegulamentoChunkResponseDTO(
                1L, "Art. 12", "Atividades de Ensino valem 40h");

        when(regulamentoFacade.listarChunks()).thenReturn(List.of(chunkDTO));

        mockMvc.perform(get("/api/v1/regulamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].artigo").value("Art. 12"))
                .andExpect(jsonPath("$[0].conteudoTexto").value("Atividades de Ensino valem 40h"));
    }
}