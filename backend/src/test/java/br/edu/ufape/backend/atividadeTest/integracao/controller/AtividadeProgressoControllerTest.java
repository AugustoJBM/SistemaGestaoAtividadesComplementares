package br.edu.ufape.backend.atividadeTest.integracao.controller;

import br.edu.ufape.backend.atividade.controller.AtividadeProgressoController;
import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AtividadeProgressoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AtividadeFacade atividadeFacade;

    @InjectMocks
    private AtividadeProgressoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve retornar progresso com 200 OK para estudante autenticado")
    void deveRetornarProgressoComSucesso() throws Exception {
        ProgressoResponseDTO dto = new ProgressoResponseDTO(
                new ProgressoModalidadeResponseDTO(60, 0, 90),
                new ProgressoModalidadeResponseDTO(160, 0, 320));

        when(atividadeFacade.obterProgresso("estudante@ufape.edu.br")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/atividades/progresso")
                .principal(new UsernamePasswordAuthenticationToken("estudante@ufape.edu.br", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc.horasAcumuladas").value(60))
                .andExpect(jsonPath("$.acc.horasExigidas").value(90))
                .andExpect(jsonPath("$.acex.horasAcumuladas").value(160))
                .andExpect(jsonPath("$.acex.horasExigidas").value(320));
    }

    @Test
    @DisplayName("Deve retornar 401 quando não autenticado")
    void deveRetornarUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/atividades/progresso"))
                .andExpect(status().isUnauthorized());
    }
}