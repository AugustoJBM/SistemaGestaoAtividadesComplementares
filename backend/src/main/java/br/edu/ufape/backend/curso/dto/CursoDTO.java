package br.edu.ufape.backend.curso.dto;

import br.edu.ufape.backend.curso.model.Curso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CursoDTO(Long id, @NotBlank(message = "Nome do curso é obrigatório") String nome,
		@NotBlank(message = "Código do curso é obrigatório") String codigo,
		@NotNull(message = "Carga horária de ACC é obrigatória") @Min(value = 1, message = "A carga de ACC deve ser positiva") Integer horasAccExigidas,
		@NotNull(message = "Carga horária de ACEX é obrigatória") @Min(value = 1, message = "A carga de ACEX deve ser positiva") Integer horasAcexExigidas,
		boolean ativo) {
	public static CursoDTO fromEntity(Curso curso) {
		return new CursoDTO(curso.getId(), curso.getNome(), curso.getCodigo(), curso.getHorasAccExigidas(),
				curso.getHorasAcexExigidas(), curso.isAtivo());
	}
}
