package br.edu.ufape.backend.solicitacao.contrato;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;

@Component
public class SolicitacaoContratoImpl implements SolicitacaoContrato {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoContratoImpl(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @Override
    public boolean existeSolicitacaoEmAbertoComAtividade(Long atividadeId) {
        return solicitacaoService.existeSolicitacaoEmAbertoComAtividade(atividadeId);
    }

    @Override
    public boolean existeSolicitacaoEmAbertoDoEstudante(Long estudanteId) {
        return solicitacaoService.existeSolicitacaoEmAbertoDoEstudante(estudanteId);
    }
}