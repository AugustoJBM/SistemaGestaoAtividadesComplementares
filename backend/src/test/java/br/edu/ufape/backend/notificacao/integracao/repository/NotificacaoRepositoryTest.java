package br.edu.ufape.backend.notificacao.integracao.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.repository.NotificacaoRepository;

@DataJpaTest
class NotificacaoRepositoryTest {

	@Autowired
	private NotificacaoRepository repository;

	private Notificacao criarNotificacao(Long destinatarioId, TipoNotificacao tipo, String titulo, boolean lida) {
		Notificacao n = new Notificacao(destinatarioId, tipo, titulo, "Mensagem de teste", 100L);
		n.setLida(lida);
		return repository.save(n);
	}

	@Test
	@DisplayName("Deve listar notificações do destinatário ordenadas por data de criação decrescente")
	void deveOrdenarPorDataCriacaoDesc() {
		Long destinatarioId = 1L;
		Notificacao n1 = criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_SUBMETIDA, "Notificação 1",
				false);
		Notificacao n2 = criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_EM_ANALISE, "Notificação 2",
				false);
		Notificacao n3 = criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_APROVADA, "Notificação 3", true);

		List<Notificacao> resultado = repository.findByDestinatarioIdOrderByDataCriacaoDesc(destinatarioId);

		assertEquals(3, resultado.size());
		// Mais recente primeiro
		assertEquals(n3.getId(), resultado.get(0).getId());
		assertEquals(n2.getId(), resultado.get(1).getId());
		assertEquals(n1.getId(), resultado.get(2).getId());
	}

	@Test
	@DisplayName("Deve filtrar notificações apenas por não lidas (lida = false)")
	void deveFiltrarApenasNaoLidas() {
		Long destinatarioId = 1L;
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_SUBMETIDA, "Nao lida 1", false);
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_EM_ANALISE, "Lida 1", true);
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_APROVADA, "Nao lida 2", false);

		List<Notificacao> naoLidas = repository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(destinatarioId,
				false);

		assertEquals(2, naoLidas.size());
		assertTrue(naoLidas.stream().noneMatch(Notificacao::isLida));
	}

	@Test
	@DisplayName("Deve filtrar notificações apenas por lidas (lida = true)")
	void deveFiltrarApenasLidas() {
		Long destinatarioId = 1L;
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_SUBMETIDA, "Nao lida 1", false);
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_EM_ANALISE, "Lida 1", true);
		criarNotificacao(destinatarioId, TipoNotificacao.SOLICITACAO_APROVADA, "Lida 2", true);

		List<Notificacao> lidas = repository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(destinatarioId, true);

		assertEquals(2, lidas.size());
		assertTrue(lidas.stream().allMatch(Notificacao::isLida));
	}

	@Test
	@DisplayName("Deve contar notificações não lidas corretamente e retornar 0 se usuário não possuir notificações")
	void deveContarNaoLidas() {
		Long destinatarioComNotificacoes = 1L;
		Long destinatarioSemNotificacoes = 999L;

		criarNotificacao(destinatarioComNotificacoes, TipoNotificacao.SOLICITACAO_SUBMETIDA, "Nao lida 1", false);
		criarNotificacao(destinatarioComNotificacoes, TipoNotificacao.SOLICITACAO_EM_ANALISE, "Nao lida 2", false);
		criarNotificacao(destinatarioComNotificacoes, TipoNotificacao.SOLICITACAO_APROVADA, "Lida 1", true);

		long contagem = repository.countByDestinatarioIdAndLidaFalse(destinatarioComNotificacoes);
		long contagemSemNotificacoes = repository.countByDestinatarioIdAndLidaFalse(destinatarioSemNotificacoes);

		assertEquals(2L, contagem);
		assertEquals(0L, contagemSemNotificacoes);
	}

	@Test
	@DisplayName("Deve respeitar escopo por destinatário em findByIdAndDestinatarioId")
	void deveRespeitarEscopoPorDestinatario() {
		Long donoId = 1L;
		Long outroUsuarioId = 2L;

		Notificacao notificacao = criarNotificacao(donoId, TipoNotificacao.SOLICITACAO_SUBMETIDA, "Titulo", false);

		Optional<Notificacao> propria = repository.findByIdAndDestinatarioId(notificacao.getId(), donoId);
		Optional<Notificacao> alheia = repository.findByIdAndDestinatarioId(notificacao.getId(), outroUsuarioId);
		Optional<Notificacao> inexistente = repository.findByIdAndDestinatarioId(9999L, donoId);

		assertTrue(propria.isPresent());
		assertEquals(notificacao.getId(), propria.get().getId());
		assertFalse(alheia.isPresent());
		assertFalse(inexistente.isPresent());
	}
}
