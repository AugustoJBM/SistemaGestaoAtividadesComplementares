package br.edu.ufape.backend.notificacao.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory;
import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory.MensagemNotificacao;

class MensagemNotificacaoFactoryTest {

	@Test
	@DisplayName("Deve resolver mensagem de notificacao para evento SUBMETIDA")
	void deveResolverMensagemParaEventoSubmetida() {
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar("SUBMETIDA", null);

		assertEquals(TipoNotificacao.SOLICITACAO_SUBMETIDA, msg.tipo());
		assertEquals("Solicitação enviada", msg.titulo());
		assertEquals("Sua solicitação de validação foi enviada e aguarda análise.", msg.mensagem());
	}

	@Test
	@DisplayName("Deve resolver mensagem de notificacao para evento EM_ANALISE")
	void deveResolverMensagemParaEventoEmAnalise() {
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar("EM_ANALISE", null);

		assertEquals(TipoNotificacao.SOLICITACAO_EM_ANALISE, msg.tipo());
		assertEquals("Solicitação em análise", msg.titulo());
		assertEquals("Sua solicitação de validação está sendo analisada.", msg.mensagem());
	}

	@Test
	@DisplayName("Deve resolver mensagem de notificacao para evento COM_PENDENCIAS interpolando justificativa")
	void deveResolverMensagemParaEventoComPendencias() {
		String justificativa = "Falta comprovante de carga horária.";
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar("COM_PENDENCIAS", justificativa);

		assertEquals(TipoNotificacao.SOLICITACAO_COM_PENDENCIAS, msg.tipo());
		assertEquals("Solicitação com pendências", msg.titulo());
		assertEquals("Sua solicitação apresenta pendências: Falta comprovante de carga horária.", msg.mensagem());
	}

	@Test
	@DisplayName("Deve resolver mensagem de notificacao para evento APROVADA")
	void deveResolverMensagemParaEventoAprovada() {
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar("APROVADA", null);

		assertEquals(TipoNotificacao.SOLICITACAO_APROVADA, msg.tipo());
		assertEquals("Solicitação aprovada", msg.titulo());
		assertEquals("Sua solicitação de validação foi aprovada.", msg.mensagem());
	}

	@Test
	@DisplayName("Deve resolver mensagem de notificacao para evento REJEITADA interpolando justificativa")
	void deveResolverMensagemParaEventoRejeitada() {
		String justificativa = "Atividade fora do período letivo.";
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar("REJEITADA", justificativa);

		assertEquals(TipoNotificacao.SOLICITACAO_REJEITADA, msg.tipo());
		assertEquals("Solicitação rejeitada", msg.titulo());
		assertEquals("Sua solicitação foi rejeitada: Atividade fora do período letivo.", msg.mensagem());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "   ", "\t", "\n" })
	@DisplayName("Deve lancar IllegalArgumentException para COM_PENDENCIAS com justificativa nula ou em branco")
	void deveLancarExcecaoParaComPendenciasSemJustificativa(String justificativaInvalida) {
		assertThrows(IllegalArgumentException.class,
				() -> MensagemNotificacaoFactory.criar("COM_PENDENCIAS", justificativaInvalida));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "   ", "\t", "\n" })
	@DisplayName("Deve lancar IllegalArgumentException para REJEITADA com justificativa nula ou em branco")
	void deveLancarExcecaoParaRejeitadaSemJustificativa(String justificativaInvalida) {
		assertThrows(IllegalArgumentException.class,
				() -> MensagemNotificacaoFactory.criar("REJEITADA", justificativaInvalida));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "DESCONHECIDO", "submetida", "   " })
	@DisplayName("Deve lancar IllegalArgumentException para status nulo ou desconhecido")
	void deveLancarExcecaoParaStatusInvalido(String novoStatus) {
		assertThrows(IllegalArgumentException.class, () -> MensagemNotificacaoFactory.criar(novoStatus, "justificativa"));
	}
}
