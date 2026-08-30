package br.edu.ufape.backend.admin.dto;

import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.usuario.model.Usuario;

public record UsuarioAdminResponseDTO(Long id, String nome, String email, Role role, boolean ativo,
		String detalheInstitucional) {
	public static UsuarioAdminResponseDTO fromEntity(Usuario usuario) {
		String detalhe = "-";
		if (usuario instanceof Avaliador av) {
			detalhe = "Registro: " + (av.getRegistro() != null ? av.getRegistro() : "N/A") + " | Área: "
					+ (av.getAreaAtuacao() != null ? av.getAreaAtuacao() : "Geral");
		} else if (usuario instanceof Administrador adm) {
			detalhe = "Setor: " + (adm.getSetor() != null ? adm.getSetor() : "Coordenação") + " | Nível: "
					+ (adm.getNivelAcesso() != null ? adm.getNivelAcesso() : "TOTAL");
		} else if (usuario instanceof Estudante est) {
			detalhe = "Matrícula: " + (est.getMatricula() != null ? est.getMatricula() : "N/A") + " | Curso: "
					+ (est.getCurso() != null ? est.getCurso() : "BCC");
		}
		return new UsuarioAdminResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole(),
				usuario.getIsActive(), detalhe);
	}
}
