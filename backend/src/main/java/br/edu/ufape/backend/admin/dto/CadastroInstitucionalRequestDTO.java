package br.edu.ufape.backend.admin.dto;

import br.edu.ufape.backend.usuario.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroInstitucionalRequestDTO(@NotBlank(message = "Nome é obrigatório") String nome,

		@NotBlank(message = "Email é obrigatório") @Email(message = "Formato de e-mail institucional inválido") String email,

		@NotBlank(message = "Senha provisória é obrigatória") @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String senha,

		@NotNull(message = "O papel (Role) é obrigatório") Role role,

		// Campos específicos para AVALIADOR
		String registro, String areaAtuacao,

		// Campos específicos para ADMINISTRADOR
		String nivelAcesso, String setor) {
}
