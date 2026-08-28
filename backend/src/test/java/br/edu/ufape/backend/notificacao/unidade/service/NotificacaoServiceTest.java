package br.edu.ufape.backend.notificacao.unidade.service;

import br.edu.ufape.backend.notificacao.exception.NotificacaoNaoEncontradaException;
import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.repository.NotificacaoRepository;
import br.edu.ufape.backend.notificacao.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

	@Mock
	private NotificacaoRepository notificacaoRepository;

	private NotificacaoService service;

	@BeforeEach
	void setUp() {
		service = new NotificacaoService(notificacaoRepository);
	}

	private Notificacao notificacao(Long id, boolean lida) {
		Notificacao notificacao = new Notificacao(1L, TipoNotificacao.SOLICITACAO_APROVADA, "Solicitação aprovada",
				"Sua solicitação de validação foi aprovada.", 10L);
		notificacao.setId(id);
		notificacao.setLida(lida);
		return notificacao;
	}

	@Test
	@DisplayName("registrar salva uma nova notificacao para o destinatario")
	void deveRegistrarNotificacao() {
		when(notificacaoRepository.save(any(Notificacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Notificacao resultado = service.registrar(1L, TipoNotificacao.SOLICITACAO_APROVADA, "Solicitação aprovada",
				"Sua solicitação de validação foi aprovada.", 10L);

		assertEquals(1L, resultado.getDestinatarioId());
		assertEquals(TipoNotificacao.SOLICITACAO_APROVADA, resultado.getTipo());
		assertEquals(10L, resultado.getSolicitacaoId());
		ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
		verify(notificacaoRepository).save(captor.capture());
		assertEquals("Solicitação aprovada", captor.getValue().getTitulo());
	}

	@Test
	@DisplayName("listar sem filtro retorna todas as notificacoes do destinatario")
	void deveListarTodasAsNotificacoes() {
		when(notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(1L))
				.thenReturn(List.of(notificacao(1L, false), notificacao(2L, true)));

		List<Notificacao> resultado = service.listar(1L, null);

		assertEquals(2, resultado.size());
	}

	@Test
	@DisplayName("listar com apenasNaoLidas=true retorna apenas as nao lidas")
	void deveListarApenasNaoLidas() {
		when(notificacaoRepository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(1L, false))
				.thenReturn(List.of(notificacao(1L, false)));

		List<Notificacao> resultado = service.listar(1L, true);

		assertEquals(1, resultado.size());
		assertTrue(!resultado.get(0).isLida());
	}

	@Test
	@DisplayName("listar com apenasNaoLidas=false retorna apenas as lidas")
	void deveListarApenasLidas() {
		when(notificacaoRepository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(1L, true))
				.thenReturn(List.of(notificacao(2L, true)));

		List<Notificacao> resultado = service.listar(1L, false);

		assertEquals(1, resultado.size());
		assertTrue(resultado.get(0).isLida());
	}

	@Test
	@DisplayName("contarNaoLidas delega para o repositorio")
	void deveContarNaoLidas() {
		when(notificacaoRepository.countByDestinatarioIdAndLidaFalse(1L)).thenReturn(4L);

		assertEquals(4L, service.contarNaoLidas(1L));
	}

	@Test
	@DisplayName("marcarComoLida marca a notificacao do destinatario correto")
	void deveMarcarComoLida() {
		Notificacao notificacao = notificacao(1L, false);
		when(notificacaoRepository.findByIdAndDestinatarioId(1L, 1L)).thenReturn(Optional.of(notificacao));
		when(notificacaoRepository.save(notificacao)).thenReturn(notificacao);

		Notificacao resultado = service.marcarComoLida(1L, 1L);

		assertTrue(resultado.isLida());
	}

	@Test
	@DisplayName("marcarComoLida lanca excecao quando a notificacao nao pertence ao destinatario")
	void deveLancarExcecaoAoMarcarComoLidaDeOutroUsuario() {
		when(notificacaoRepository.findByIdAndDestinatarioId(99L, 1L)).thenReturn(Optional.empty());

		assertThrows(NotificacaoNaoEncontradaException.class, () -> service.marcarComoLida(99L, 1L));
	}

	@Test
	@DisplayName("marcarTodasComoLidas marca todas as nao lidas do destinatario e retorna a quantidade")
	void deveMarcarTodasComoLidas() {
		Notificacao naoLida1 = notificacao(1L, false);
		Notificacao naoLida2 = notificacao(2L, false);
		when(notificacaoRepository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(1L, false))
				.thenReturn(List.of(naoLida1, naoLida2));

		int total = service.marcarTodasComoLidas(1L);

		assertEquals(2, total);
		assertTrue(naoLida1.isLida());
		assertTrue(naoLida2.isLida());
		verify(notificacaoRepository, times(1)).saveAll(List.of(naoLida1, naoLida2));
	}
}
