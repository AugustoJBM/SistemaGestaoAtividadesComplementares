package br.edu.ufape.backend.notificacao.unidade.facade;

import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.notificacao.dto.ContagemNaoLidasResponseDTO;
import br.edu.ufape.backend.notificacao.dto.NotificacaoResponseDTO;
import br.edu.ufape.backend.notificacao.facade.NotificacaoFacade;
import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.service.NotificacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoFacadeTest {

	private static final String EMAIL = "estudante@ufape.edu.br";

	@Mock
	private NotificacaoService notificacaoService;

	@Mock
	private UsuarioContrato usuarioContrato;

	@InjectMocks
	private NotificacaoFacade facade;

	private Estudante usuario;

	@BeforeEach
	void setUp() {
		usuario = new Estudante();
		usuario.setId(1L);
	}

	private Notificacao notificacao() {
		Notificacao notificacao = new Notificacao(1L, TipoNotificacao.SOLICITACAO_APROVADA, "Solicitação aprovada",
				"Sua solicitação de validação foi aprovada.", 10L);
		notificacao.setId(5L);
		return notificacao;
	}

	@Test
	@DisplayName("Resolve o destinatario pelo email e converte a lista de notificacoes para DTO")
	void deveListarNotificacoesDoUsuarioAutenticado() {
		when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(usuario));
		when(notificacaoService.listar(1L, null)).thenReturn(List.of(notificacao()));

		List<NotificacaoResponseDTO> resultado = facade.listar(EMAIL, null);

		assertEquals(1, resultado.size());
		assertEquals(5L, resultado.get(0).id());
		assertEquals(10L, resultado.get(0).solicitacaoId());
		assertEquals("SOLICITACAO_APROVADA", resultado.get(0).tipo());
	}

	@Test
	@DisplayName("Converte a contagem de nao lidas do service para o DTO")
	void deveContarNaoLidas() {
		when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(usuario));
		when(notificacaoService.contarNaoLidas(1L)).thenReturn(3L);

		ContagemNaoLidasResponseDTO resultado = facade.contarNaoLidas(EMAIL);

		assertEquals(3L, resultado.naoLidas());
	}

	@Test
	@DisplayName("Marca notificacao como lida delegando para o service com o destinatario resolvido")
	void deveMarcarComoLida() {
		when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(usuario));
		Notificacao lida = notificacao();
		lida.setLida(true);
		when(notificacaoService.marcarComoLida(5L, 1L)).thenReturn(lida);

		NotificacaoResponseDTO resultado = facade.marcarComoLida(EMAIL, 5L);

		assertTrue(resultado.lida());
	}

	@Test
	@DisplayName("Lanca UnauthorizedException quando o email nao corresponde a nenhum usuario")
	void deveLancarExcecaoParaEmailInexistente() {
		when(usuarioContrato.buscarPorEmail("desconhecido@ufape.edu.br")).thenReturn(Optional.empty());

		assertThrows(UnauthorizedException.class, () -> facade.listar("desconhecido@ufape.edu.br", null));
	}
}
