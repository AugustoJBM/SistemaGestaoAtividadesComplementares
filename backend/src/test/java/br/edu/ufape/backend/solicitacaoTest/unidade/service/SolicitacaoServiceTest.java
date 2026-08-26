package br.edu.ufape.backend.solicitacaoTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoValidacaoRepository solicitacaoValidacaoRepository;

    @Mock
    private AtividadeContrato atividadeContrato;

    private SolicitacaoService solicitacaoService;

    @BeforeEach
    void setUp() {
        solicitacaoService = new SolicitacaoService(solicitacaoValidacaoRepository, atividadeContrato);
    }

    @Test
    @DisplayName("Caminho feliz: Deve submeter nova solicitação criando snapshots imutáveis com status SUBMETIDA")
    void deveSubmeterSolicitacaoComSucesso() {
        Long estudanteId = 100L;
        List<AtividadeResponseDTO> atividades = List.of(
                new AtividadeResponseDTO(1L, "Curso de Java", "UFAPE", LocalDate.now(), 40, Natureza.ACC, Categoria.ENSINO, LocalDateTime.now(), "estudante@ufape.edu.br", StatusAtividade.PENDENTE),
                new AtividadeResponseDTO(2L, "Projeto de Extensão", "UFAPE", LocalDate.now(), 60, Natureza.ACEX, Categoria.EXTENSAO, LocalDateTime.now(), "estudante@ufape.edu.br", StatusAtividade.PENDENTE)
        );

        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(false);
        when(atividadeContrato.buscarPorEstudante(estudanteId))
                .thenReturn(atividades);
        when(solicitacaoValidacaoRepository.save(any(SolicitacaoValidacao.class)))
                .thenAnswer(invocation -> {
                    SolicitacaoValidacao sol = invocation.getArgument(0);
                    sol.setId(1L);
                    return sol;
                });

        SolicitacaoValidacao resultado = solicitacaoService.submeter(estudanteId);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(estudanteId, resultado.getEstudanteId());
        assertEquals(StatusSolicitacao.SUBMETIDA, resultado.getStatus());
        assertNotNull(resultado.getDataSubmissao());
        assertEquals(2, resultado.getItens().size());

        SolicitacaoAtividade item1 = resultado.getItens().get(0);
        assertEquals(1L, item1.getAtividadeId());
        assertEquals("Curso de Java", item1.getTitulo());
        assertEquals(40, item1.getCargaHoraria());
        assertEquals("ACC", item1.getNatureza());

        SolicitacaoAtividade item2 = resultado.getItens().get(1);
        assertEquals(2L, item2.getAtividadeId());
        assertEquals("Projeto de Extensão", item2.getTitulo());
        assertEquals(60, item2.getCargaHoraria());
        assertEquals("ACEX", item2.getNatureza());

        ArgumentCaptor<SolicitacaoValidacao> captor = ArgumentCaptor.forClass(SolicitacaoValidacao.class);
        verify(solicitacaoValidacaoRepository).save(captor.capture());
        assertEquals(StatusSolicitacao.SUBMETIDA, captor.getValue().getStatus());
        assertEquals(estudanteId, captor.getValue().getEstudanteId());
    }

    @Test
    @DisplayName("Regra 1: Deve lançar SolicitacaoEmAbertoException se o estudante já tiver solicitação em aberto")
    void deveLancarExcecaoQuandoJaExisteSolicitacaoEmAberto() {
        Long estudanteId = 100L;
        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(true);

        assertThrows(SolicitacaoEmAbertoException.class, () -> solicitacaoService.submeter(estudanteId));

        verify(atividadeContrato, never()).buscarPorEstudante(anyLong());
        verify(solicitacaoValidacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Regra 3: Deve lançar EstudanteSemAtividadesException se o estudante não possuir atividades")
    void deveLancarExcecaoQuandoEstudanteNaoPossuiAtividades() {
        Long estudanteId = 100L;
        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(false);
        when(atividadeContrato.buscarPorEstudante(estudanteId))
                .thenReturn(List.of());

        assertThrows(EstudanteSemAtividadesException.class, () -> solicitacaoService.submeter(estudanteId));

        verify(solicitacaoValidacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Contrato: Deve verificar existência de solicitação em aberto com atividade")
    void deveVerificarExistenciaDeSolicitacaoEmAbertoComAtividade() {
        Long atividadeId = 10L;
        when(solicitacaoValidacaoRepository.existsByAtividadeIdAndStatusIn(eq(atividadeId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(true);

        boolean resultado = solicitacaoService.existeSolicitacaoEmAbertoComAtividade(atividadeId);

        assertTrue(resultado);
        verify(solicitacaoValidacaoRepository).existsByAtividadeIdAndStatusIn(atividadeId, StatusSolicitacao.STATUS_EM_ABERTO);
    }

    @Test
    @DisplayName("Contrato: Deve verificar existência de solicitação em aberto do estudante")
    void deveVerificarExistenciaDeSolicitacaoEmAbertoDoEstudante() {
        Long estudanteId = 100L;
        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(true);

        boolean resultado = solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(estudanteId);

        assertTrue(resultado);
        verify(solicitacaoValidacaoRepository).existsByEstudanteIdAndStatusIn(estudanteId, StatusSolicitacao.STATUS_EM_ABERTO);
    }

    @Test
    @DisplayName("Listagem: Deve listar solicitações do estudante ordenadas por data de submissão desc")
    void deveListarSolicitacoesDoEstudanteOrdenadasPorDataSubmissaoDesc() {
        Long estudanteId = 100L;
        SolicitacaoValidacao s1 = new SolicitacaoValidacao(estudanteId, LocalDateTime.now().minusDays(2), StatusSolicitacao.APROVADA, List.of());
        s1.setId(1L);
        SolicitacaoValidacao s2 = new SolicitacaoValidacao(estudanteId, LocalDateTime.now().minusDays(1), StatusSolicitacao.SUBMETIDA, List.of());
        s2.setId(2L);

        when(solicitacaoValidacaoRepository.findByEstudanteIdOrderByDataSubmissaoDesc(estudanteId))
                .thenReturn(List.of(s2, s1));

        List<SolicitacaoValidacao> resultado = solicitacaoService.listarDoEstudante(estudanteId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
        assertEquals(1L, resultado.get(1).getId());
        verify(solicitacaoValidacaoRepository).findByEstudanteIdOrderByDataSubmissaoDesc(estudanteId);
    }

    @Test
    @DisplayName("Listagem: Deve retornar lista vazia quando o estudante não possuir solicitações")
    void deveRetornarListaVaziaQuandoEstudanteNaoPossuiSolicitacoes() {
        Long estudanteId = 100L;
        when(solicitacaoValidacaoRepository.findByEstudanteIdOrderByDataSubmissaoDesc(estudanteId))
                .thenReturn(List.of());

        List<SolicitacaoValidacao> resultado = solicitacaoService.listarDoEstudante(estudanteId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(solicitacaoValidacaoRepository).findByEstudanteIdOrderByDataSubmissaoDesc(estudanteId);
    }

    @Test
    @DisplayName("Detalhe: Deve retornar solicitação detalhada quando pertencer ao estudante autenticado")
    void deveDetalharSolicitacaoPertencenteAoEstudante() {
        Long estudanteId = 100L;
        Long solicitacaoId = 10L;
        SolicitacaoValidacao solicitacao = new SolicitacaoValidacao(
                estudanteId, LocalDateTime.now().minusDays(3), StatusSolicitacao.COM_PENDENCIAS, List.of(
                        new SolicitacaoAtividade(1L, "Curso", 20, "ACC")
                )
        );
        solicitacao.setId(solicitacaoId);
        solicitacao.setJustificativa("Falta assinatura no certificado");
        solicitacao.setDataAvaliacao(LocalDateTime.now().minusDays(1));

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId))
                .thenReturn(Optional.of(solicitacao));

        SolicitacaoValidacao resultado = solicitacaoService.detalhar(estudanteId, solicitacaoId);

        assertNotNull(resultado);
        assertEquals(solicitacaoId, resultado.getId());
        assertEquals(StatusSolicitacao.COM_PENDENCIAS, resultado.getStatus());
        assertEquals("Falta assinatura no certificado", resultado.getJustificativa());
        assertNotNull(resultado.getDataAvaliacao());
        assertEquals(1, resultado.getItens().size());
        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoId, estudanteId);
    }

    @Test
    @DisplayName("Detalhe: Deve retornar solicitação aprovada sem justificativa sem falhas")
    void deveDetalharSolicitacaoAprovadaSemJustificativa() {
        Long estudanteId = 100L;
        Long solicitacaoId = 10L;
        SolicitacaoValidacao solicitacao = new SolicitacaoValidacao(
                estudanteId, LocalDateTime.now().minusDays(3), StatusSolicitacao.APROVADA, List.of()
        );
        solicitacao.setId(solicitacaoId);
        solicitacao.setJustificativa(null);

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId))
                .thenReturn(Optional.of(solicitacao));

        SolicitacaoValidacao resultado = solicitacaoService.detalhar(estudanteId, solicitacaoId);

        assertNotNull(resultado);
        assertEquals(solicitacaoId, resultado.getId());
        assertEquals(StatusSolicitacao.APROVADA, resultado.getStatus());
        assertNull(resultado.getJustificativa());
        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoId, estudanteId);
    }

    @Test
    @DisplayName("Detalhe: Deve lançar SolicitacaoNaoEncontradaException quando solicitação não existir ou for de outro estudante")
    void deveLancarExcecaoQuandoSolicitacaoNaoPertencerAoEstudante() {
        Long estudanteId = 100L;
        Long solicitacaoIdInvalida = 999L;

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoIdInvalida, estudanteId))
                .thenReturn(Optional.empty());

        assertThrows(SolicitacaoNaoEncontradaException.class, () -> solicitacaoService.detalhar(estudanteId, solicitacaoIdInvalida));
        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoIdInvalida, estudanteId);
    }
}
