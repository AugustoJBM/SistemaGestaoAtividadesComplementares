package br.edu.ufape.backend.solicitacao.dto;

import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoSolicitacaoRequestDTO(
        @NotNull(message = "A decisao e obrigatoria.")
        DecisaoAvaliacao decisao,
        String justificativa) {
}
