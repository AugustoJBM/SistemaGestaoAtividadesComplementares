package br.edu.ufape.backend.solicitacao.unidade.service;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.notificacao.contrato.NotificacaoContrato;
import br.edu.ufape.backend.solicitacao.controller.SolicitacaoAvaliacaoController;
import br.edu.ufape.backend.solicitacao.controller.SolicitacaoEstudanteController;
import br.edu.ufape.backend.solicitacao.dto.AvaliacaoSolicitacaoRequestDTO;
import br.edu.ufape.backend.solicitacao.facade.SolicitacaoFacade;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprehensiveSolicitacaoBranchTest {

	@Mock
	private SolicitacaoValidacaoRepository repo;
	@Mock
	private AtividadeContrato atividadeContrato;
	@Mock
	private NotificacaoContrato notificacaoContrato;
	@Mock
	private UsuarioContrato usuarioContrato;

	@InjectMocks
	private SolicitacaoService solicitacaoService;

	@Test
	@DisplayName("SolicitacaoService: Submissão com atividade de natureza nula")
	void deveSubmeterComNaturezaNula() {
		AtividadeResponseDTO dtoSemNatureza = new AtividadeResponseDTO(1L, "Titulo", "UFAPE", LocalDate.now(), 20, null,
				Categoria.ENSINO, LocalDateTime.now(), "a@u.br");
		when(repo.existsByEstudanteIdAndStatusIn(eq(1L), any())).thenReturn(false);
		when(atividadeContrato.buscarPorEstudante(1L)).thenReturn(List.of(dtoSemNatureza));
		when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

		SolicitacaoValidacao s = solicitacaoService.submeter(1L);
		assertNull(s.getItens().get(0).getNatureza());
	}

	@Test
	@DisplayName("SolicitacaoFacade: Métodos de submissão, listagem, detalhe e avaliação cobrem todas as branches")
	void deveCobrirBranchesSolicitacaoFacade() {
		SolicitacaoFacade facade = new SolicitacaoFacade(solicitacaoService, usuarioContrato);

		when(usuarioContrato.buscarPorEmail("inexistente@u.br")).thenReturn(Optional.empty());
		assertThrows(AcessoNegadoAtividadeException.class, () -> facade.submeter("inexistente@u.br"));

		when(usuarioContrato.buscarPorEmail("admin@u.br"))
				.thenReturn(Optional.of(new Administrador("Admin", "admin@u.br", "h", "N1", "TI")));
		assertThrows(AcessoNegadoAtividadeException.class, () -> facade.listarDoEstudante("admin@u.br"));

		Estudante est = new Estudante("Aluno", "aluno@u.br", "h");
		est.setId(10L);
		when(usuarioContrato.buscarPorEmail("aluno@u.br")).thenReturn(Optional.of(est));
		when(repo.existsByEstudanteIdAndStatusIn(eq(10L), any())).thenReturn(false);
		when(atividadeContrato.buscarPorEstudante(10L)).thenReturn(List.of(new AtividadeResponseDTO(1L, "T", "I",
				LocalDate.now(), 10, Natureza.ACC, Categoria.ENSINO, LocalDateTime.now(), "aluno@u.br")));
		when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

		assertNotNull(facade.submeter("aluno@u.br"));
		assertNotNull(facade.submeter(10L));

		when(usuarioContrato.buscarPorEmail("avaliador.sumiu@u.br")).thenReturn(Optional.empty());
		assertThrows(UnauthorizedException.class,
				() -> facade.avaliar(1L, "avaliador.sumiu@u.br", DecisaoAvaliacao.APROVADA, null));
	}

	@Test
	@DisplayName("Solicitacao Controllers: Authentication nula retorna 401")
	void deveRetornar401ControllersSolicitacao() {
		SolicitacaoFacade facade = mock(SolicitacaoFacade.class);
		SolicitacaoAvaliacaoController c1 = new SolicitacaoAvaliacaoController(facade);
		assertEquals(HttpStatus.UNAUTHORIZED,
				c1.avaliar(1L, new AvaliacaoSolicitacaoRequestDTO(DecisaoAvaliacao.APROVADA, null), null)
						.getStatusCode());

		SolicitacaoEstudanteController c2 = new SolicitacaoEstudanteController(facade);
		assertEquals(HttpStatus.UNAUTHORIZED, c2.submeter(null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED, c2.listar(null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED, c2.detalhar(1L, null).getStatusCode());
	}
}
