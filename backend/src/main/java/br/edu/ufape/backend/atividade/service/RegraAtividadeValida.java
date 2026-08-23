package br.edu.ufape.backend.atividade.service;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.StatusAtividade;

public final class RegraAtividadeValida {

    private RegraAtividadeValida() {
    }

    public static boolean isAprovada(AtividadeComplementar atividade) {
        return atividade != null && atividade.getStatus() == StatusAtividade.APROVADA;
    }

    public static boolean isPendente(AtividadeComplementar atividade) {
        return atividade != null && (atividade.getStatus() == null || atividade.getStatus() == StatusAtividade.PENDENTE);
    }

    public static boolean isValida(AtividadeComplementar atividade) {
        return atividade != null;
    }
}