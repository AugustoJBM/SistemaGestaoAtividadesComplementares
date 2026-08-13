package br.edu.ufape.backend.atividade.service;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;

/**
 * Ponto único de decisão sobre o que conta como "atividade válida" para fins
 * de acompanhamento de carga horária (issue #65).
 *
 * Decisão atual: o modelo de {@link AtividadeComplementar} ainda não possui
 * um campo de status/situação (rascunho, pendente, rejeitada, aprovada).
 * Por isso, toda atividade cadastrada é considerada válida.
 *
 * Quando o fluxo de aprovação por avaliador for implementado (issue futura),
 * a regra deve ser ajustada apenas aqui, sem precisar tocar em quem consome
 * este método (ex.: {@link ProgressoService}).
 */
public final class RegraAtividadeValida {

    private RegraAtividadeValida() {
    }

    public static boolean isValida(AtividadeComplementar atividade) {
        return atividade != null;
    }
}