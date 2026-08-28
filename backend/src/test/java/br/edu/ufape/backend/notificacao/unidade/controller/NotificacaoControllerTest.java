package br.edu.ufape.backend.notificacao.unidade.controller;

import br.edu.ufape.backend.comum.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.notificacao.controller.NotificacaoController;
import br.edu.ufape.backend.notificacao.dto.ContagemNaoLidasResponseDTO;
import br.edu.ufape.backend.notificacao.dto.NotificacaoResponseDTO;
import br.edu.ufape.backend.notificacao.exception.NotificacaoNaoEncontradaException;
import br.edu.ufape.backend.notificacao.facade.NotificacaoFacade;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificacaoControllerTest {

	private MockMvc mockMvc;

	@Mock
	private NotificacaoFacade facade;

	@InjectMocks
	private NotificacaoController controller;

	private static final String EMAIL = "estudante@ufape.edu.br";

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
	}

	private NotificacaoResponseDTO notificacao(Long id, boolean lida) {
		return new NotificacaoResponseDTO(id, "SOLICITACAO_APROVADA", "Solicitação aprovada",
				"Sua solicitação de validação foi aprovada.", 10L, lida, LocalDateTime.now());
	}

	// ---- GET / ----

	@Test
	@DisplayName("Retorna 200 com a lista de notificacoes do usuario autenticado")
	void deveRetornar200ComLista() throws Exception {
		when(facade.listar(eq(EMAIL), isNull())).thenReturn(List.of(notificacao(1L, false)));

		mockMvc.perform(get("/api/v1/notificacoes").principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd")))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].solicitacaoId").value(10));
	}

	@Test
	@DisplayName("Retorna 200 com lista vazia quando nao ha notificacoes")
	void deveRetornar200ComListaVazia() throws Exception {
		when(facade.listar(eq(EMAIL), isNull())).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/notificacoes").principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd")))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@DisplayName("Filtra apenas nao lidas quando apenasNaoLidas=true")
	void deveFiltrarApenasNaoLidas() throws Exception {
		when(facade.listar(eq(EMAIL), eq(true))).thenReturn(List.of(notificacao(2L, false)));

		mockMvc.perform(get("/api/v1/notificacoes").param("apenasNaoLidas", "true")
				.principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd"))).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].lida").value(false));
	}

	// ---- GET /contagem-nao-lidas ----

	@Test
	@DisplayName("Retorna 200 com a contagem de nao lidas")
	void deveRetornar200ComContagem() throws Exception {
		when(facade.contarNaoLidas(EMAIL)).thenReturn(new ContagemNaoLidasResponseDTO(3L));

		mockMvc.perform(get("/api/v1/notificacoes/contagem-nao-lidas")
				.principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.naoLidas").value(3));
	}

	@Test
	@DisplayName("Retorna 200 com contagem zero")
	void deveRetornar200ComContagemZero() throws Exception {
		when(facade.contarNaoLidas(EMAIL)).thenReturn(new ContagemNaoLidasResponseDTO(0L));

		mockMvc.perform(get("/api/v1/notificacoes/contagem-nao-lidas")
				.principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.naoLidas").value(0));
	}

	// ---- PATCH /{id}/leitura ----

	@Test
	@DisplayName("Marca notificacao como lida e retorna 200")
	void deveRetornar200AoMarcarComoLida() throws Exception {
		when(facade.marcarComoLida(EMAIL, 1L)).thenReturn(notificacao(1L, true));

		mockMvc.perform(patch("/api/v1/notificacoes/1/leitura")
				.principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.lida").value(true));
	}

	@Test
	@DisplayName("Retorna 404 ao marcar como lida notificacao de outro usuario")
	void deveRetornar404AoMarcarComoLidaDeOutroUsuario() throws Exception {
		when(facade.marcarComoLida(EMAIL, 99L)).thenThrow(new NotificacaoNaoEncontradaException(99L));

		mockMvc.perform(patch("/api/v1/notificacoes/99/leitura")
				.principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd"))).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());
	}

	// ---- PATCH /leitura ----

	@Test
	@DisplayName("Marca todas como lidas e retorna 204")
	void deveRetornar204AoMarcarTodasComoLidas() throws Exception {
		mockMvc.perform(
				patch("/api/v1/notificacoes/leitura").principal(new UsernamePasswordAuthenticationToken(EMAIL, "pwd")))
				.andExpect(status().isNoContent());
	}

	// ---- 401 ----

	@Test
	@DisplayName("Retorna 401 sem autenticacao")
	void deveRetornar401SemAutenticacao() throws Exception {
		mockMvc.perform(get("/api/v1/notificacoes")).andExpect(status().isUnauthorized());
	}
}
