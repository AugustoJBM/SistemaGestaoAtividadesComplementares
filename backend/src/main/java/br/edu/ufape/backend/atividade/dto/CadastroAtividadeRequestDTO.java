package br.edu.ufape.backend.atividade.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;

public record CadastroAtividadeRequestDTO(
        @NotBlank(message = "Título é obrigatório")
        String titulo,
        @NotBlank(message = "Instituição responsável é obrigatória")
        String instituicaoResponsavel,
        @NotNull(message = "Data de realização é obrigatória")
        @PastOrPresent(message = "Data de realização não pode ser no futuro")
        LocalDate dataRealizacao,
        @NotNull(message = "Carga horária é obrigatória")
        @Positive(message = "Carga horária deve ser maior que zero")
        Integer cargaHoraria,
        @NotNull(message = "Natureza é obrigatória")
        Natureza natureza,
        @NotNull(message = "Categoria é obrigatória")
        Categoria categoria) {
}
