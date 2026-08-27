package br.edu.ufape.backend.solicitacaoTest.unidade.controller;

import br.edu.ufape.backend.comum.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.solicitacao.controller.SolicitacaoAvaliacaoController;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoDetalheResponseDTO;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.exception.TransicaoEstadoInvalidaException;
import br.edu.ufape.backend.solicitacao.facade.SolicitacaoFacade;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAvaliacaoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @Mock
    private SolicitacaoFacade facade;

    @InjectMocks
    private SolicitacaoAvaliacaoController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private SolicitacaoDetalheResponseDTO responseAprovada() {
        return new SolicitacaoDetalheResponseDTO(
                1L, StatusSolicitacao.APROVADA,
                LocalDateTime.now(), LocalDateTime.now(),
                null, List.of(), 0);
    }

    // ---- 200 ----

    @Test
    @DisplayName("AVALIADOR aprova solicitacao SUBMETIDA e recebe 200 com status APROVADA")
    void deveRetornar200AoAprovar() throws Exception {
        when(facade.avaliar(eq(1L), eq("avaliador@ufape.edu.br"), eq(DecisaoAvaliacao.APROVADA), isNull()))
                .thenReturn(responseAprovada());

        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("decisao", "APROVADA")))
                        .principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    // ---- 400 ----

    @Test
    @DisplayName("Rejeicao sem justificativa retorna 400 com ErroResponse")
    void deveRetornar400ParaRejeicaoSemJustificativa() throws Exception {
        when(facade.avaliar(eq(1L), any(), eq(DecisaoAvaliacao.REJEITADA), isNull()))
                .thenThrow(new IllegalArgumentException("Justificativa e obrigatoria para decisao REJEITADA"));

        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("decisao", "REJEITADA")))
                        .principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("decisao nula retorna 400 por validacao de Bean Validation")
    void deveRetornar400ParaDecisaoNula() throws Exception {
        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decisao\": null}")
                        .principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
                .andExpect(status().isBadRequest());
    }

    // ---- 401 ----

    @Test
    @DisplayName("Requisicao sem autenticacao retorna 401")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("decisao", "APROVADA"))))
                .andExpect(status().isUnauthorized());
    }

    // ---- 404 ----

    @Test
    @DisplayName("Id inexistente retorna 404 com ErroResponse")
    void deveRetornar404ParaSolicitacaoInexistente() throws Exception {
        when(facade.avaliar(eq(999L), any(), any(), any()))
                .thenThrow(new SolicitacaoNaoEncontradaException(999L));

        mockMvc.perform(patch("/api/v1/solicitacoes/999/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("decisao", "APROVADA")))
                        .principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---- 409 ----

    @Test
    @DisplayName("Avaliar solicitacao ja finalizada retorna 409 com ErroResponse")
    void deveRetornar409ParaSolicitacaoJaFinalizada() throws Exception {
        when(facade.avaliar(eq(1L), any(), eq(DecisaoAvaliacao.REJEITADA), any()))
                .thenThrow(new TransicaoEstadoInvalidaException(StatusSolicitacao.APROVADA, StatusSolicitacao.REJEITADA));

        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("decisao", "REJEITADA", "justificativa", "Motivo")))
                        .principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

}
