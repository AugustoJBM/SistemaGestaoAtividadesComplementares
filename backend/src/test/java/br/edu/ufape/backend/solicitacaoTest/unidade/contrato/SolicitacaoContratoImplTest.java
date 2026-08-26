package br.edu.ufape.backend.solicitacaoTest.unidade.contrato;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.solicitacao.contrato.SolicitacaoContratoImpl;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;

@ExtendWith(MockitoExtension.class)
class SolicitacaoContratoImplTest {

    @Mock
    private SolicitacaoService solicitacaoService;

    private SolicitacaoContratoImpl solicitacaoContrato;

    @BeforeEach
    void setUp() {
        solicitacaoContrato = new SolicitacaoContratoImpl(solicitacaoService);
    }

    @Test
    @DisplayName("Deve delegar existeSolicitacaoEmAbertoComAtividade para o service")
    void deveDelegarExisteSolicitacaoEmAbertoComAtividade() {
        Long atividadeId = 1L;
        when(solicitacaoService.existeSolicitacaoEmAbertoComAtividade(atividadeId)).thenReturn(true);

        boolean resultado = solicitacaoContrato.existeSolicitacaoEmAbertoComAtividade(atividadeId);

        assertTrue(resultado);
        verify(solicitacaoService).existeSolicitacaoEmAbertoComAtividade(atividadeId);
    }

    @Test
    @DisplayName("Deve delegar existeSolicitacaoEmAbertoDoEstudante para o service")
    void deveDelegarExisteSolicitacaoEmAbertoDoEstudante() {
        Long estudanteId = 1L;
        when(solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(estudanteId)).thenReturn(true);

        boolean resultado = solicitacaoContrato.existeSolicitacaoEmAbertoDoEstudante(estudanteId);

        assertTrue(resultado);
        verify(solicitacaoService).existeSolicitacaoEmAbertoDoEstudante(estudanteId);
    }
}