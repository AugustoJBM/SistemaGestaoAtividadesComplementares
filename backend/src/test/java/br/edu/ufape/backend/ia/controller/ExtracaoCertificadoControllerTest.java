package br.edu.ufape.backend.ia.controller;

import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExtracaoCertificadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IaCertificadoFacade iaCertificadoFacade;

    @InjectMocks
    private ExtracaoCertificadoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve extrair dados de certificado com sucesso e retornar 200 OK")
    void deveExtrairDadosDeCertificadoComSucesso() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "certificado.pdf", MediaType.APPLICATION_PDF_VALUE, "PDF-DUMMY".getBytes());

        ExtracaoCertificadoResponseDTO dtoEsperado = new ExtracaoCertificadoResponseDTO(
                "Curso Spring Boot", "UFAPE", LocalDate.of(2026, 5, 10), 20, "ACC", "ENSINO");

        when(iaCertificadoFacade.extrairDadosCertificado(any())).thenReturn(dtoEsperado);

        mockMvc.perform(multipart("/api/v1/atividades/extrair-certificado")
                        .file(arquivo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Curso Spring Boot"))
                .andExpect(jsonPath("$.instituicaoResponsavel").value("UFAPE"))
                .andExpect(jsonPath("$.cargaHoraria").value(20))
                .andExpect(jsonPath("$.natureza").value("ACC"))
                .andExpect(jsonPath("$.categoria").value("ENSINO"));
    }
}