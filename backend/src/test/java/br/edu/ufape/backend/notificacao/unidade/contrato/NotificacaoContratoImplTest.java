package br.edu.ufape.backend.notificacao.unidade.contrato;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.notificacao.contrato.NotificacaoContratoImpl;
import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory;
import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory.MensagemNotificacao;
import br.edu.ufape.backend.notificacao.service.NotificacaoService;

@ExtendWith(MockitoExtension.class)
class NotificacaoContratoImplTest {

	@Mock
	private NotificacaoService notificacaoService;

	private NotificacaoContratoImpl contrato;

	@BeforeEach
	void setUp() {
		contrato = new NotificacaoContratoImpl(notificacaoService);
	}

	@Test
	@DisplayName("Deve delegar criacao de notificacao para NotificacaoService resolvendo a mensagem dinamicamente")
	void deveDelegarCriacaoDeNotificacao() {
		Long destinatarioId = 1L;
		Long solicitacaoId = 10L;
		String novoStatus = "SUBMETIDA";

		MensagemNotificacao esperada = MensagemNotificacaoFactory.criar(novoStatus, null);

		contrato.notificarMudancaStatusSolicitacao(destinatarioId, solicitacaoId, novoStatus, null);

		verify(notificacaoService).registrar(destinatarioId, esperada.tipo(), esperada.titulo(), esperada.mensagem(),
				solicitacaoId);
	}

	@Test
	@DisplayName("Deve propagar excecao para o chamador quando NotificacaoService falhar")
	void devePropagarExcecaoQuandoServiceFalhar() {
		Long destinatarioId = 1L;
		Long solicitacaoId = 10L;
		String novoStatus = "APROVADA";
		MensagemNotificacao esperada = MensagemNotificacaoFactory.criar(novoStatus, null);

		RuntimeException falha = new RuntimeException("Falha de conexao com banco");
		doThrow(falha)
				.when(notificacaoService)
				.registrar(destinatarioId, esperada.tipo(), esperada.titulo(), esperada.mensagem(), solicitacaoId);

		RuntimeException excecao = assertThrows(RuntimeException.class,
				() -> contrato.notificarMudancaStatusSolicitacao(destinatarioId, solicitacaoId, novoStatus, null));
		assertSame(falha, excecao);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "DESCONHECIDO", "submetida", "   " })
	@DisplayName("Status invalido deve propagar erro sem registrar notificacao")
	void deveRejeitarStatusInvalidoSemRegistrarNotificacao(String novoStatus) {
		assertThrows(IllegalArgumentException.class,
				() -> contrato.notificarMudancaStatusSolicitacao(1L, 10L, novoStatus, null));

		verifyNoInteractions(notificacaoService);
	}
}
