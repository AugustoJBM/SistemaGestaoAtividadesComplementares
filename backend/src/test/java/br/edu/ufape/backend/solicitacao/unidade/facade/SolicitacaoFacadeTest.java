package br.edu.ufape.backend.solicitacao.unidade.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.solicitacao.dto.SolicitacaoAvaliadorResumoResponseDTO;
import br.edu.ufape.backend.solicitacao.facade.SolicitacaoFacade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class SolicitacaoFacadeTest {

	@Mock
	private SolicitacaoService solicitacaoService;

	@Mock
	private UsuarioContrato usuarioContrato;

	private SolicitacaoFacade facade;

	@BeforeEach
	void setUp() {
		facade = new SolicitacaoFacade(solicitacaoService, usuarioContrato);
	}

	@Test
	@DisplayName("Deve listar para avaliacao resolvendo nome do estudante e agregando itens")
	void deveListarParaAvaliacaoResolvendoNome() {
		SolicitacaoAtividade item1 = new SolicitacaoAtividade(1L, "Curso A", 20, "ACC");
		SolicitacaoAtividade item2 = new SolicitacaoAtividade(2L, "Curso B", 40, "ACEX");

		SolicitacaoValidacao s1 = new SolicitacaoValidacao(10L, LocalDateTime.now(), StatusSolicitacao.SUBMETIDA, List.of(item1, item2));
		s1.setId(100L);

		Estudante estudante = new Estudante("Lucas Silva", "lucas@ufape.edu.br", "hash");
		estudante.setId(10L);

		when(solicitacaoService.listarParaAvaliacao(StatusSolicitacao.SUBMETIDA)).thenReturn(List.of(s1));
		when(usuarioContrato.buscarPorId(10L)).thenReturn(Optional.of(estudante));

		List<SolicitacaoAvaliadorResumoResponseDTO> resultado = facade.listarParaAvaliacao(StatusSolicitacao.SUBMETIDA);

		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		SolicitacaoAvaliadorResumoResponseDTO dto = resultado.get(0);
		assertEquals(100L, dto.id());
		assertEquals("Lucas Silva", dto.estudanteNome());
		assertEquals(StatusSolicitacao.SUBMETIDA, dto.status());
		assertEquals(2L, dto.totalAtividades());
		assertEquals(60, dto.cargaHorariaTotal());
		verify(solicitacaoService).listarParaAvaliacao(StatusSolicitacao.SUBMETIDA);
		verify(usuarioContrato).buscarPorId(10L);
	}

	@Test
	@DisplayName("Deve usar fallback quando estudante for removido e nao quebrar requisicao")
	void deveUsarFallbackQuandoEstudanteNaoEncontrado() {
		SolicitacaoValidacao s1 = new SolicitacaoValidacao(999L, LocalDateTime.now(), StatusSolicitacao.APROVADA, List.of());
		s1.setId(101L);

		when(solicitacaoService.listarParaAvaliacao(null)).thenReturn(List.of(s1));
		when(usuarioContrato.buscarPorId(999L)).thenReturn(Optional.empty());

		List<SolicitacaoAvaliadorResumoResponseDTO> resultado = facade.listarParaAvaliacao(null);

		assertNotNull(resultado);
		assertEquals(1, resultado.size());
		SolicitacaoAvaliadorResumoResponseDTO dto = resultado.get(0);
		assertEquals(101L, dto.id());
		assertEquals("Estudante Não Encontrado", dto.estudanteNome());
		assertEquals(0L, dto.totalAtividades());
		assertEquals(0, dto.cargaHorariaTotal());
	}
}