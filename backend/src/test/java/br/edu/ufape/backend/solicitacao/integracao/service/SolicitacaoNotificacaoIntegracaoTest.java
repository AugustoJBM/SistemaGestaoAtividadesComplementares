package br.edu.ufape.backend.solicitacao.integracao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.certificado.model.Certificado;
import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.repository.NotificacaoRepository;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@SpringBootTest
@Transactional
class SolicitacaoNotificacaoIntegracaoTest {

	@Autowired
	private SolicitacaoService solicitacaoService;

	@Autowired
	private SolicitacaoValidacaoRepository solicitacaoRepository;

	@MockitoSpyBean
	private NotificacaoRepository notificacaoRepository;

	@Autowired
	private UsuarioContrato usuarioContrato;

	@Autowired
	private AtividadeComplementarRepository atividadeRepository;

	private Estudante criarEstudanteComAtividade(String email) {
		Estudante estudante = (Estudante) usuarioContrato
				.salvar(new Estudante("Estudante Notificacao", email, "senha123", "MATR-" + System.nanoTime(), "BCC"));

		Certificado certificado = new Certificado("certificado.pdf", "application/pdf", 1024L,
				"/uploads/certificado.pdf");
		AtividadeComplementar atividade = new AtividadeComplementar("Curso de Extensão Spring", "UFAPE",
				LocalDate.now(), 20, Natureza.ACC, Categoria.ENSINO, certificado, estudante);
		atividadeRepository.save(atividade);

		return estudante;
}
	private Avaliador criarAvaliador(String email) {
		return (Avaliador) usuarioContrato
				.salvar(new Avaliador("Avaliador Notificacao", email, "senha123", "REG-" + System.nanoTime(), "BCC"));
	}

	@Test
	@DisplayName("Rollback da submissao deve desfazer a solicitacao e sua notificacao")
	void deveReverterNotificacaoQuandoTransacaoDaSolicitacaoFalhar() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.rollback@ufape.edu.br");
		SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudante.getId());
		assertEquals(1, notificacaoRepository.countByDestinatarioIdAndLidaFalse(estudante.getId()));

		TestTransaction.flagForRollback();
		TestTransaction.end();
		TestTransaction.start();

		assertFalse(solicitacaoRepository.existsById(solicitacao.getId()));
		assertTrue(notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId()).isEmpty());
	}

	@Test
	@DisplayName("Falha ao gravar notificacao da submissao deve abortar a transacao")
	void deveReverterSubmissaoQuandoNotificacaoFalhar() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.falha.submeter@ufape.edu.br");
		doThrow(new DataAccessResourceFailureException("Falha ao gravar notificacao"))
				.when(notificacaoRepository).save(any(Notificacao.class));

		assertThrows(DataAccessResourceFailureException.class, () -> solicitacaoService.submeter(estudante.getId()));
		TestTransaction.flagForCommit();
		assertThrows(UnexpectedRollbackException.class, TestTransaction::end);
		TestTransaction.start();

		assertTrue(solicitacaoRepository.findByEstudanteId(estudante.getId()).isEmpty());
		assertTrue(notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId()).isEmpty());
	}

	@Test
	@DisplayName("Falha ao gravar notificacao da avaliacao deve abortar a transacao")
	void deveReverterAvaliacaoQuandoNotificacaoFalhar() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.falha.avaliar@ufape.edu.br");
		Avaliador avaliador = criarAvaliador("avaliador.notif.falha.avaliar@ufape.edu.br");
		SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudante.getId());
		doThrow(new DataAccessResourceFailureException("Falha ao gravar notificacao"))
				.when(notificacaoRepository).save(any(Notificacao.class));

		assertThrows(DataAccessResourceFailureException.class,
				() -> solicitacaoService.avaliar(solicitacao.getId(), avaliador.getId(), DecisaoAvaliacao.APROVADA, null));
		TestTransaction.flagForCommit();
		assertThrows(UnexpectedRollbackException.class, TestTransaction::end);
		TestTransaction.start();

		assertFalse(solicitacaoRepository.existsById(solicitacao.getId()));
		assertTrue(notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId()).isEmpty());
	}

	@Test
	@DisplayName("Integracao: Submeter solicitacao deve persistir notificacao SOLICITACAO_SUBMETIDA de verdade no banco")
	void devePersistirNotificacaoAoSubmeterSolicitacao() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.submeter@ufape.edu.br");

		SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudante.getId());

		assertNotNull(solicitacao);
		assertEquals(StatusSolicitacao.SUBMETIDA, solicitacao.getStatus());

		List<Notificacao> notificacoes = notificacaoRepository
				.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId());
		assertEquals(1, notificacoes.size());

		Notificacao notif = notificacoes.get(0);
		assertEquals(TipoNotificacao.SOLICITACAO_SUBMETIDA, notif.getTipo());
		assertEquals("Solicitação enviada", notif.getTitulo());
		assertEquals("Sua solicitação de validação foi enviada e aguarda análise.", notif.getMensagem());
		assertEquals(solicitacao.getId(), notif.getSolicitacaoId());
		assertEquals(estudante.getId(), notif.getDestinatarioId());
		assertFalse(notif.isLida());
	}

	@Test
	@DisplayName("Integracao: Avaliar solicitacao com APROVADA deve persistir segunda notificacao SOLICITACAO_APROVADA")
	void devePersistirNotificacaoAoAprovarSolicitacao() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.aprovar@ufape.edu.br");
		Avaliador avaliador = criarAvaliador("avaliador.notif.aprovar@ufape.edu.br");

		SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudante.getId());
		solicitacaoService.avaliar(solicitacao.getId(), avaliador.getId(), DecisaoAvaliacao.APROVADA, null);

		List<Notificacao> notificacoes = notificacaoRepository
				.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId());
		assertEquals(2, notificacoes.size());

		Notificacao maisRecente = notificacoes.get(0);
		assertEquals(TipoNotificacao.SOLICITACAO_APROVADA, maisRecente.getTipo());
		assertEquals("Solicitação aprovada", maisRecente.getTitulo());
		assertEquals("Sua solicitação de validação foi aprovada.", maisRecente.getMensagem());
		assertEquals(solicitacao.getId(), maisRecente.getSolicitacaoId());

		Notificacao primeira = notificacoes.get(1);
		assertEquals(TipoNotificacao.SOLICITACAO_SUBMETIDA, primeira.getTipo());
	}

	@Test
	@DisplayName("Integracao: Avaliar solicitacao com COM_PENDENCIAS deve persistir notificacao contendo a justificativa")
	void devePersistirNotificacaoComJustificativaAoAvaliarComPendencias() {
		Estudante estudante = criarEstudanteComAtividade("estudante.notif.pendencias@ufape.edu.br");
		Avaliador avaliador = criarAvaliador("avaliador.notif.pendencias@ufape.edu.br");

		String justificativa = "Falta assinatura do coordenador no certificado.";
		SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudante.getId());
		solicitacaoService.avaliar(solicitacao.getId(), avaliador.getId(), DecisaoAvaliacao.COM_PENDENCIAS,
				justificativa);

		List<Notificacao> notificacoes = notificacaoRepository
				.findByDestinatarioIdOrderByDataCriacaoDesc(estudante.getId());
		assertEquals(2, notificacoes.size());

		Notificacao maisRecente = notificacoes.get(0);
		assertEquals(TipoNotificacao.SOLICITACAO_COM_PENDENCIAS, maisRecente.getTipo());
		assertEquals("Solicitação com pendências", maisRecente.getTitulo());
		assertTrue(maisRecente.getMensagem().contains(justificativa));
		assertEquals("Sua solicitação apresenta pendências: " + justificativa, maisRecente.getMensagem());
		assertEquals(solicitacao.getId(), maisRecente.getSolicitacaoId());
	}
}
