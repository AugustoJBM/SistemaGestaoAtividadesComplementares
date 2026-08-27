package br.edu.ufape.backend.solicitacao.model;

public enum DecisaoAvaliacao {
    APROVADA,
    REJEITADA,
    COM_PENDENCIAS;

    public StatusSolicitacao toStatus() {
        return StatusSolicitacao.valueOf(this.name());
    }
}
