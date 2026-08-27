package br.edu.ufape.backend.solicitacaoTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResumoResponseDTO;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.exception.TransicaoEstadoInvalidaException;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
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

    private SolicitacaoValidacao criarSolicitacao(StatusSolicitacao status) {
        SolicitacaoValidacao s = new SolicitacaoValidacao(1L);
        s.setId(10L);
        s.setStatus(status);
        return s;
    }

    @Test
    @DisplayName("Deve submeter solicitacao com sucesso criando snapshots imutaveis das atividades")
    void deveSubmeterSolicitacaoComSucesso() {
        Long estudanteId = 1L;

        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(false);

        List<AtividadeResponseDTO> atividades = List.of(
                new AtividadeResponseDTO(
                        10L, "Curso de Java", "SENAI", LocalDate.now(), 40,
                        Natureza.ACC, Categoria.ENSINO, LocalDateTime.now(), "estudante@ufape.edu.br"
                ),
                new AtividadeResponseDTO(
                        11L, "Projeto de Pesquisa", "UFAPE", LocalDate.now(), 60,
                        Natureza.ACEX, Categoria.PESQUISA, LocalDateTime.now(), "estudante@ufape.edu.br"
                )
        );

        when(atividadeContrato.buscarPorEstudante(estudanteId)).thenReturn(atividades);

        SolicitacaoValidacao solicitacaoSalva = new SolicitacaoValidacao();
        solicitacaoSalva.setId(100L);
        solicitacaoSalva.setStatus(StatusSolicitacao.SUBMETIDA);
        when(solicitacaoValidacaoRepository.save(any(SolicitacaoValidacao.class))).thenAnswer(invocation -> {
            SolicitacaoValidacao s = invocation.getArgument(0);
            s.setId(100L);
            return s;
        });

        SolicitacaoValidacao resultado = solicitacaoService.submeter(estudanteId);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(StatusSolicitacao.SUBMETIDA, resultado.getStatus());
        assertEquals(estudanteId, resultado.getEstudanteId());
        assertNotNull(resultado.getDataSubmissao());
        assertEquals(2, resultado.getItens().size());

        SolicitacaoAtividade item1 = resultado.getItens().get(0);
        assertEquals(10L, item1.getAtividadeId());
        assertEquals("Curso de Java", item1.getTitulo());
        assertEquals(40, item1.getCargaHoraria());
        assertEquals("ACC", item1.getNatureza());

        SolicitacaoAtividade item2 = resultado.getItens().get(1);
        assertEquals(11L, item2.getAtividadeId());
        assertEquals("Projeto de Pesquisa", item2.getTitulo());
        assertEquals(60, item2.getCargaHoraria());
        assertEquals("ACEX", item2.getNatureza());

        ArgumentCaptor<SolicitacaoValidacao> captor = ArgumentCaptor.forClass(SolicitacaoValidacao.class);
        verify(solicitacaoValidacaoRepository).save(captor.capture());
        SolicitacaoValidacao capturada = captor.getValue();
        assertEquals(StatusSolicitacao.SUBMETIDA, capturada.getStatus());
    }

    @Test
    @DisplayName("Deve lancar SolicitacaoEmAbertoException quando ja houver solicitacao em aberto")
    void deveLancarExcecaoQuandoJaExisteSolicitacaoEmAberto() {
        Long estudanteId = 1L;

        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(true);

        assertThrows(
                SolicitacaoEmAbertoException.class,
                () -> solicitacaoService.submeter(estudanteId)
        );

        verify(atividadeContrato, never()).buscarPorEstudante(anyLong());
        verify(solicitacaoValidacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar EstudanteSemAtividadesException quando estudante nao possuir atividades")
    void deveLancarExcecaoQuandoEstudanteNaoPossuiAtividades() {
        Long estudanteId = 1L;

        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                eq(estudanteId), eq(StatusSolicitacao.STATUS_EM_ABERTO)))
                .thenReturn(false);

        when(atividadeContrato.buscarPorEstudante(estudanteId)).thenReturn(List.of());

        assertThrows(
                EstudanteSemAtividadesException.class,
                () -> solicitacaoService.submeter(estudanteId)
        );

        verify(solicitacaoValidacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve verificar se existe solicitacao em aberto com determinada atividade")
    void deveVerificarExisteSolicitacaoEmAbertoComAtividade() {
        Long atividadeId = 5L;

        when(solicitacaoValidacaoRepository.existsByAtividadeIdAndStatusIn(
                atividadeId, StatusSolicitacao.STATUS_EM_ABERTO))
                .thenReturn(true);

        boolean resultado = solicitacaoService.existeSolicitacaoEmAbertoComAtividade(atividadeId);

        assertTrue(resultado);
        verify(solicitacaoValidacaoRepository).existsByAtividadeIdAndStatusIn(atividadeId, StatusSolicitacao.STATUS_EM_ABERTO);
    }

    @Test
    @DisplayName("Deve verificar se existe solicitacao em aberto do estudante")
    void deveVerificarExisteSolicitacaoEmAbertoDoEstudante() {
        Long estudanteId = 1L;

        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                estudanteId, StatusSolicitacao.STATUS_EM_ABERTO))
                .thenReturn(true);

        boolean resultado = solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(estudanteId);

        assertTrue(resultado);
        verify(solicitacaoValidacaoRepository).existsByEstudanteIdAndStatusIn(estudanteId, StatusSolicitacao.STATUS_EM_ABERTO);
    }

    @Test
    @DisplayName("Listagem: Deve listar resumos de solicitações do estudante ordenadas por data de submissão desc")
    void deveListarSolicitacoesDoEstudanteOrdenadasPorDataSubmissaoDesc() {
        Long estudanteId = 100L;
        SolicitacaoResumoResponseDTO r1 = new SolicitacaoResumoResponseDTO(1L, StatusSolicitacao.APROVADA, LocalDateTime.now().minusDays(2), LocalDateTime.now(), 3L);
        SolicitacaoResumoResponseDTO r2 = new SolicitacaoResumoResponseDTO(2L, StatusSolicitacao.SUBMETIDA, LocalDateTime.now().minusDays(1), null, 2L);

        when(solicitacaoValidacaoRepository.findResumosByEstudanteIdOrderByDataSubmissaoDesc(estudanteId))
                .thenReturn(List.of(r2, r1));

        List<SolicitacaoResumoResponseDTO> resultado = solicitacaoService.listarDoEstudante(estudanteId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(2L, resultado.get(0).id());
        assertEquals(1L, resultado.get(1).id());
        verify(solicitacaoValidacaoRepository).findResumosByEstudanteIdOrderByDataSubmissaoDesc(estudanteId);
    }

    @Test
    @DisplayName("Listagem: Deve retornar lista vazia quando o estudante não possuir solicitações")
    void deveRetornarListaVaziaQuandoEstudanteNaoPossuiSolicitacoes() {
        Long estudanteId = 100L;
        when(solicitacaoValidacaoRepository.findResumosByEstudanteIdOrderByDataSubmissaoDesc(estudanteId))
                .thenReturn(List.of());

        List<SolicitacaoResumoResponseDTO> resultado = solicitacaoService.listarDoEstudante(estudanteId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(solicitacaoValidacaoRepository).findResumosByEstudanteIdOrderByDataSubmissaoDesc(estudanteId);
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
        solicitacao.setJustificativa("Certificado ilegível");

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId))
                .thenReturn(Optional.of(solicitacao));

        SolicitacaoValidacao resultado = solicitacaoService.detalhar(estudanteId, solicitacaoId);

        assertNotNull(resultado);
        assertEquals(solicitacaoId, resultado.getId());
        assertEquals(estudanteId, resultado.getEstudanteId());
        assertEquals(StatusSolicitacao.COM_PENDENCIAS, resultado.getStatus());
        assertEquals("Certificado ilegível", resultado.getJustificativa());
        assertEquals(1, resultado.getItens().size());
        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoId, estudanteId);
    }

    @Test
    @DisplayName("Detalhe: Deve retornar solicitação aprovada sem justificativa")
    void deveDetalharSolicitacaoAprovadaSemJustificativa() {
        Long estudanteId = 100L;
        Long solicitacaoId = 11L;
        SolicitacaoValidacao solicitacao = new SolicitacaoValidacao(
                estudanteId, LocalDateTime.now().minusDays(1), StatusSolicitacao.APROVADA, List.of(
                        new SolicitacaoAtividade(2L, "Monitoria", 40, "ACC")
                )
        );
        solicitacao.setId(solicitacaoId);
        solicitacao.setDataAvaliacao(LocalDateTime.now());

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId))
                .thenReturn(Optional.of(solicitacao));

        SolicitacaoValidacao resultado = solicitacaoService.detalhar(estudanteId, solicitacaoId);

        assertNotNull(resultado);
        assertEquals(StatusSolicitacao.APROVADA, resultado.getStatus());
        assertNull(resultado.getJustificativa());
        assertNotNull(resultado.getDataAvaliacao());
        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoId, estudanteId);
    }

    @Test
    @DisplayName("Detalhe: Deve lançar SolicitacaoNaoEncontradaException quando não pertencer ao estudante ou não existir")
    void deveLancarExcecaoQuandoSolicitacaoNaoPertencerAoEstudanteOuNaoExistir() {
        Long estudanteId = 100L;
        Long solicitacaoId = 999L;

        when(solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId))
                .thenReturn(Optional.empty());

        assertThrows(
                SolicitacaoNaoEncontradaException.class,
                () -> solicitacaoService.detalhar(estudanteId, solicitacaoId)
        );

        verify(solicitacaoValidacaoRepository).findByIdAndEstudanteId(solicitacaoId, estudanteId);
    }

    // ---- avaliar ----

    @Test
    @DisplayName("Deve aprovar solicitacao SUBMETIDA e persistir avaliadorId e dataAvaliacao")
    void deveAprovarSolicitacaoSubmetida() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.SUBMETIDA);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoValidacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitacaoValidacao resultado = solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.APROVADA, null);

        assertEquals(StatusSolicitacao.APROVADA, resultado.getStatus());
        assertEquals(99L, resultado.getAvaliadorId());
        assertNotNull(resultado.getDataAvaliacao());
        verify(solicitacaoValidacaoRepository).save(solicitacao);
    }

    @Test
    @DisplayName("Deve rejeitar solicitacao com justificativa e persistir texto")
    void deveRejeitarComJustificativa() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.EM_ANALISE);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoValidacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SolicitacaoValidacao resultado = solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.REJEITADA, "Documentacao insuficiente.");

        assertEquals(StatusSolicitacao.REJEITADA, resultado.getStatus());
        assertEquals("Documentacao insuficiente.", resultado.getJustificativa());
    }

    @Test
    @DisplayName("Rejeicao sem justificativa deve lancar IllegalArgumentException — 400")
    void rejeicaoSemJustificativaDeveLancar400() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.SUBMETIDA);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        assertThrows(IllegalArgumentException.class, () ->
                solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.REJEITADA, null));
        verify(solicitacaoValidacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejeicao com justificativa em branco deve lancar IllegalArgumentException — 400")
    void rejeicaoComJustificativaEmBrancoDeveLancar400() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.SUBMETIDA);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        assertThrows(IllegalArgumentException.class, () ->
                solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.REJEITADA, "   "));
    }

    @Test
    @DisplayName("COM_PENDENCIAS sem justificativa deve lancar IllegalArgumentException — 400")
    void comPendenciasSemJustificativaDeveLancar400() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.EM_ANALISE);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        assertThrows(IllegalArgumentException.class, () ->
                solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.COM_PENDENCIAS, null));
    }

    @Test
    @DisplayName("Solicitacao inexistente deve lancar SolicitacaoNaoEncontradaException — 404")
    void solicitacaoInexistenteDeveLancar404() {
        when(solicitacaoValidacaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(SolicitacaoNaoEncontradaException.class, () ->
                solicitacaoService.avaliar(999L, 99L, DecisaoAvaliacao.APROVADA, null));
    }

    @Test
    @DisplayName("Reavaliar solicitacao ja APROVADA deve lancar TransicaoEstadoInvalidaException — 409")
    void reavaliarAprovadaDeveLancar409() {
        SolicitacaoValidacao solicitacao = criarSolicitacao(StatusSolicitacao.APROVADA);
        when(solicitacaoValidacaoRepository.findById(10L)).thenReturn(Optional.of(solicitacao));

        assertThrows(TransicaoEstadoInvalidaException.class, () ->
                solicitacaoService.avaliar(10L, 99L, DecisaoAvaliacao.REJEITADA, "Tentativa invalida."));
    }

    // ---- contrato ----

    @Test
    @DisplayName("Contrato: deve verificar existencia de solicitacao em aberto com a atividade (true)")
    void deveVerificarExistenciaDeSolicitacaoEmAbertoComAtividade() {
        when(solicitacaoValidacaoRepository.existsByAtividadeIdAndStatusIn(
                eq(10L), eq(StatusSolicitacao.STATUS_EM_ABERTO))).thenReturn(true);

        assertTrue(solicitacaoService.existeSolicitacaoEmAbertoComAtividade(10L));
    }

    @Test
    @DisplayName("Contrato: deve retornar false quando nao houver solicitacao em aberto com a atividade")
    void deveRetornarFalseParaAtividadeSemSolicitacaoAberta() {
        when(solicitacaoValidacaoRepository.existsByAtividadeIdAndStatusIn(
                eq(10L), eq(StatusSolicitacao.STATUS_EM_ABERTO))).thenReturn(false);

        assertFalse(solicitacaoService.existeSolicitacaoEmAbertoComAtividade(10L));
    }

    @Test
    @DisplayName("Contrato: deve verificar existencia de solicitacao em aberto do estudante (true)")
    void deveVerificarExistenciaDeSolicitacaoEmAbertoDoEstudante() {
        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                eq(100L), eq(StatusSolicitacao.STATUS_EM_ABERTO))).thenReturn(true);

        assertTrue(solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(100L));
    }

    @Test
    @DisplayName("Contrato: deve retornar false quando estudante nao tiver solicitacao em aberto")
    void deveRetornarFalseParaEstudanteSemSolicitacaoAberta() {
        when(solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                eq(100L), eq(StatusSolicitacao.STATUS_EM_ABERTO))).thenReturn(false);

        assertFalse(solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(100L));
    }
}
