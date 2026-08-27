package br.edu.ufape.backend.atividade.dto;

import java.time.LocalDate;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record AtualizarAtividadeRequestDTO(
        @NotBlank(message = "O título é obrigatório.") String titulo,

        @NotBlank(message = "A instituição responsável é obrigatória.") String instituicaoResponsavel,

        @NotNull(message = "A data de realização é obrigatória.") @PastOrPresent(message = "Data de realização não pode ser no futuro.") LocalDate dataRealizacao,

        @NotNull(message = "A carga horária é obrigatória.") @Min(value = 1, message = "A carga horária deve ser de no mínimo 1 hora.") Integer cargaHoraria,

        @NotNull(message = "A natureza é obrigatória.") Natureza natureza,

        @NotNull(message = "A categoria é obrigatória.") Categoria categoria) {
}