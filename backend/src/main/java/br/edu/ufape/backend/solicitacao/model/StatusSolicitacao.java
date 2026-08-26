package br.edu.ufape.backend.solicitacao.model;

import java.util.List;

public enum StatusSolicitacao {
    SUBMETIDA,
    EM_ANALISE,
    COM_PENDENCIAS,
    APROVADA,
    REJEITADA;

    public static final List<StatusSolicitacao> STATUS_EM_ABERTO = List.of(
            SUBMETIDA,
            EM_ANALISE,
            COM_PENDENCIAS
    );

    public boolean isEmAberto() {
        return STATUS_EM_ABERTO.contains(this);
    }
}