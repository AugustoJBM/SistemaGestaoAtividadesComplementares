package br.edu.ufape.backend.solicitacao.unidade.controller;

import br.edu.ufape.backend.comum.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.solicitacao.controller.SolicitacaoAvaliacaoController;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoAvaliadorDetalheResponseDTO;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoAvaliadorResumoResponseDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAvaliacaoControllerTest {

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

	@Mock
	private SolicitacaoFacade facade;

	@InjectMocks
	private SolicitacaoAvaliacaoController controller;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	private String toJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	private SolicitacaoDetalheResponseDTO responseAprovada() {
		return new SolicitacaoDetalheResponseDTO(1L, StatusSolicitacao.APROVADA, LocalDateTime.now(),
				LocalDateTime.now(), null, List.of(), 0);
	}

	// ---- 200 PATCH avaliacao ----

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

	// ---- 200 GET avaliacao ----

	@Test
	@DisplayName("GET /avaliacao sem status retorna 200 com lista completa de solicitacoes")
	void deveRetornar200ListaCompletaParaAvaliacao() throws Exception {
		SolicitacaoAvaliadorResumoResponseDTO r1 = new SolicitacaoAvaliadorResumoResponseDTO(
				1L, "Lucas Silva", LocalDateTime.now().minusDays(1), StatusSolicitacao.SUBMETIDA, null, 2L, 40);
		SolicitacaoAvaliadorResumoResponseDTO r2 = new SolicitacaoAvaliadorResumoResponseDTO(
				2L, "Maria Santos", LocalDateTime.now().minusDays(2), StatusSolicitacao.APROVADA, LocalDateTime.now(), 3L, 60);

		when(facade.listarParaAvaliacao(isNull())).thenReturn(List.of(r1, r2));

		mockMvc.perform(get("/api/v1/solicitacoes/avaliacao"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].estudanteNome").value("Lucas Silva"))
				.andExpect(jsonPath("$[0].status").value("SUBMETIDA"))
				.andExpect(jsonPath("$[0].totalAtividades").value(2))
				.andExpect(jsonPath("$[0].cargaHorariaTotal").value(40))
				.andExpect(jsonPath("$[1].id").value(2L))
				.andExpect(jsonPath("$[1].estudanteNome").value("Maria Santos"))
				.andExpect(jsonPath("$[1].status").value("APROVADA"));
	}

	@Test
	@DisplayName("GET /avaliacao com filtro ?status=APROVADA retorna 200 com lista filtrada")
	void deveRetornar200ListaFiltradaPorStatus() throws Exception {
		SolicitacaoAvaliadorResumoResponseDTO r2 = new SolicitacaoAvaliadorResumoResponseDTO(
				2L, "Maria Santos", LocalDateTime.now().minusDays(2), StatusSolicitacao.APROVADA, LocalDateTime.now(), 3L, 60);

		when(facade.listarParaAvaliacao(StatusSolicitacao.APROVADA)).thenReturn(List.of(r2));

		mockMvc.perform(get("/api/v1/solicitacoes/avaliacao").param("status", "APROVADA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(2L))
				.andExpect(jsonPath("$[0].status").value("APROVADA"));
	}

	@Test
	@DisplayName("GET /avaliacao com resultado vazio retorna 200 com array vazio []")
	void deveRetornar200ArrayVazio() throws Exception {
		when(facade.listarParaAvaliacao(any())).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/solicitacoes/avaliacao").param("status", "COM_PENDENCIAS"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0))
				.andExpect(content().json("[]"));
	}

	@Test
	@DisplayName("GET /avaliacao com status invalido retorna 400 em ErroResponse")
	void deveRetornar400ParaStatusInvalido() throws Exception {
		mockMvc.perform(get("/api/v1/solicitacoes/avaliacao").param("status", "INEXISTENTE"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.status").value(400));
	}

	// ---- 400 PATCH ----

	@Test
	@DisplayName("Rejeicao sem justificativa retorna 400 com ErroResponse")
	void deveRetornar400ParaRejeicaoSemJustificativa() throws Exception {
		when(facade.avaliar(eq(1L), any(), eq(DecisaoAvaliacao.REJEITADA), isNull()))
				.thenThrow(new IllegalArgumentException(
						"Justificativa e obrigatoria para decisao REJEITADA"));

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
		mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao").contentType(MediaType.APPLICATION_JSON)
				.content("{\"decisao\": null}")
				.principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
				.andExpect(status().isBadRequest());
	}

	// ---- 401 ----

	@Test
	@DisplayName("Requisicao sem autenticacao retorna 401")
	void deveRetornar401SemAutenticacao() throws Exception {
		mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao").contentType(MediaType.APPLICATION_JSON)
				.content(toJson(Map.of("decisao", "APROVADA")))).andExpect(status().isUnauthorized());
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
				.thenThrow(new TransicaoEstadoInvalidaException(StatusSolicitacao.APROVADA,
						StatusSolicitacao.REJEITADA));

		mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
				.contentType(MediaType.APPLICATION_JSON)
				.content(toJson(Map.of("decisao", "REJEITADA", "justificativa", "Motivo")))
				.principal(new UsernamePasswordAuthenticationToken("avaliador@ufape.edu.br", "pwd")))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").exists());
	}
	// ---- 200 GET /{id}/avaliacao ----

	@Test
	@DisplayName("GET /{id}/avaliacao retorna 200 com detalhe completo para o avaliador")
	void deveRetornar200DetalheParaAvaliacao() throws Exception {
		SolicitacaoAvaliadorDetalheResponseDTO detalhe = new SolicitacaoAvaliadorDetalheResponseDTO(
				10L, "Lucas Silva", "lucas@ufape.edu.br", LocalDateTime.now().minusDays(1),
				StatusSolicitacao.SUBMETIDA, null, null, 20, List.of());

		when(facade.detalharParaAvaliacao(10L)).thenReturn(detalhe);

		mockMvc.perform(get("/api/v1/solicitacoes/10/avaliacao"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(10L))
				.andExpect(jsonPath("$.estudanteNome").value("Lucas Silva"))
				.andExpect(jsonPath("$.estudanteEmail").value("lucas@ufape.edu.br"))
				.andExpect(jsonPath("$.status").value("SUBMETIDA"))
				.andExpect(jsonPath("$.cargaHorariaTotal").value(20));
	}

	@Test
	@DisplayName("GET /{id}/avaliacao com id inexistente retorna 404 em ErroResponse")
	void deveRetornar404DetalheParaAvaliacaoInexistente() throws Exception {
		when(facade.detalharParaAvaliacao(999L))
				.thenThrow(new SolicitacaoNaoEncontradaException(999L));

		mockMvc.perform(get("/api/v1/solicitacoes/999/avaliacao"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());
	}
}