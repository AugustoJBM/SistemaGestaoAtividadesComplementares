package br.edu.ufape.backend.ia.controller;

import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MetricasPesquisaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IaCertificadoFacade iaCertificadoFacade;

    @InjectMocks
    private MetricasPesquisaController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve retornar métricas empíricas de concordância e tempo médio")
    void deveRetornarMetricasEmpiricas() throws Exception {
        when(iaCertificadoFacade.contarAvaliadas()).thenReturn(100L);
        when(iaCertificadoFacade.contarConcordancias()).thenReturn(90L);
        when(iaCertificadoFacade.calcularTempoMedioMs()).thenReturn(250.0);

        mockMvc.perform(get("/api/v1/metricas-pesquisa/concordancia-kappa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmostrasAvaliadas").value(100))
                .andExpect(jsonPath("$.totalConcordancias").value(90))
                .andExpect(jsonPath("$.acuraciaObservada").value(0.9))
                .andExpect(jsonPath("$.tempoMedioInferenciaMs").value(250.0));
    }
}