package br.edu.ufape.backend.atividade.dto;

import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoAvaliador;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoDecisaoRequestDTO(
        @NotNull(message = "A decisão do avaliador é obrigatória")
        DecisaoAvaliador decisao,
        String justificativa
) {}