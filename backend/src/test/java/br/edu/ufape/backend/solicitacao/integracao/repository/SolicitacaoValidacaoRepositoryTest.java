package br.edu.ufape.backend.solicitacao.integracao.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;

@DataJpaTest
class SolicitacaoValidacaoRepositoryTest {

	@Autowired
	private SolicitacaoValidacaoRepository repository;

	private SolicitacaoValidacao criarSolicitacao(Long estudanteId, StatusSolicitacao status, List<SolicitacaoAtividade> itens) {
		SolicitacaoValidacao s = new SolicitacaoValidacao(estudanteId, LocalDateTime.now(), status, itens);
		return repository.save(s);
	}

	@Test
	@DisplayName("Deve retornar todas as solicitações quando status for nulo, ordenadas por data de submissão desc")
	void deveRetornarTodasSolicitacoesQuandoStatusForNulo() {
		SolicitacaoAtividade item1 = new SolicitacaoAtividade(1L, "Curso A", 20, "ACC");
		SolicitacaoAtividade item2 = new SolicitacaoAtividade(2L, "Curso B", 30, "ACEX");

		SolicitacaoValidacao s1 = criarSolicitacao(10L, StatusSolicitacao.SUBMETIDA, List.of(item1));
		SolicitacaoValidacao s2 = criarSolicitacao(11L, StatusSolicitacao.APROVADA, List.of(item2));
		SolicitacaoValidacao s3 = criarSolicitacao(12L, StatusSolicitacao.REJEITADA, List.of());

		List<SolicitacaoValidacao> resultado = repository.findByStatusOrderByDataSubmissaoDesc(null);

		assertNotNull(resultado);
		assertEquals(3, resultado.size());
		// Ordenação determinística decrescente
		assertEquals(s3.getId(), resultado.get(0).getId());
		assertEquals(s2.getId(), resultado.get(1).getId());
		assertEquals(s1.getId(), resultado.get(2).getId());

		// Verifica itens carregados
		assertEquals(1, resultado.get(1).getItens().size());
		assertEquals("Curso B", resultado.get(1).getItens().get(0).getTitulo());
	}

	@Test
	@DisplayName("Deve filtrar apenas solicitações com status APROVADA")
	void deveFiltrarApenasAprovadas() {
		criarSolicitacao(10L, StatusSolicitacao.SUBMETIDA, List.of());
		SolicitacaoValidacao s2 = criarSolicitacao(11L, StatusSolicitacao.APROVADA, List.of());
		criarSolicitacao(12L, StatusSolicitacao.REJEITADA, List.of());

		List<SolicitacaoValidacao> resultado = repository.findByStatusOrderByDataSubmissaoDesc(StatusSolicitacao.APROVADA);

		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		assertEquals(s2.getId(), resultado.get(0).getId());
		assertEquals(StatusSolicitacao.APROVADA, resultado.get(0).getStatus());
	}

	@Test
	@DisplayName("Deve retornar lista vazia quando nenhuma solicitação corresponder ao status filtrado")
	void deveRetornarListaVaziaQuandoNenhumStatusCorresponder() {
		criarSolicitacao(10L, StatusSolicitacao.SUBMETIDA, List.of());

		List<SolicitacaoValidacao> resultado = repository.findByStatusOrderByDataSubmissaoDesc(StatusSolicitacao.COM_PENDENCIAS);

		assertNotNull(resultado);
		assertTrue(resultado.isEmpty());
	}

	@Test
	@DisplayName("Deve buscar solicitacao por id com itens via findByIdComItens")
	void deveBuscarSolicitacaoPorIdComItens() {
		SolicitacaoAtividade item = new SolicitacaoAtividade(1L, "Curso A", 20, "ACC");
		SolicitacaoValidacao s = criarSolicitacao(10L, StatusSolicitacao.SUBMETIDA, List.of(item));

		Optional<SolicitacaoValidacao> opt = repository.findByIdComItens(s.getId());

		assertTrue(opt.isPresent());
		assertEquals(s.getId(), opt.get().getId());
		assertEquals(1, opt.get().getItens().size());
		assertEquals("Curso A", opt.get().getItens().get(0).getTitulo());
	}

	@Test
	@DisplayName("findByIdComItens deve retornar vazio quando id nao existir")
	void findByIdComItensRetornaVazioParaIdInexistente() {
		Optional<SolicitacaoValidacao> opt = repository.findByIdComItens(9999L);
		assertFalse(opt.isPresent());
	}
}