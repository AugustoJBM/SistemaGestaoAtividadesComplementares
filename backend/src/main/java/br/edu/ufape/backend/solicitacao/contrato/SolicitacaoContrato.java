package br.edu.ufape.backend.solicitacao.contrato;

public interface SolicitacaoContrato {
    boolean existeSolicitacaoEmAbertoComAtividade(Long atividadeId);
    boolean existeSolicitacaoEmAbertoDoEstudante(Long estudanteId);
}