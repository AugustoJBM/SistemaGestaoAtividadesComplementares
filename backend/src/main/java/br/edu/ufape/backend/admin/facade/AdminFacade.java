package br.edu.ufape.backend.admin.facade;

import br.edu.ufape.backend.admin.dto.CadastroInstitucionalRequestDTO;
import br.edu.ufape.backend.admin.dto.UsuarioAdminResponseDTO;
import br.edu.ufape.backend.admin.service.AdminUsuarioService;
import br.edu.ufape.backend.curso.dto.CursoDTO;
import br.edu.ufape.backend.curso.service.CursoService;
import br.edu.ufape.backend.usuario.model.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminFacade {

	private final AdminUsuarioService adminUsuarioService;
	private final CursoService cursoService;

	public AdminFacade(AdminUsuarioService adminUsuarioService, CursoService cursoService) {
		this.adminUsuarioService = adminUsuarioService;
		this.cursoService = cursoService;
	}

	public UsuarioAdminResponseDTO cadastrarUsuarioInstitucional(CadastroInstitucionalRequestDTO request) {
		return adminUsuarioService.cadastrarUsuarioInstitucional(request);
	}

	public List<UsuarioAdminResponseDTO> listarUsuarios(Role role, Boolean ativo) {
		return adminUsuarioService.listarUsuarios(role, ativo);
	}

	public UsuarioAdminResponseDTO alternarStatusUsuario(Long id) {
		return adminUsuarioService.alternarStatusUsuario(id);
	}

	public List<CursoDTO> listarCursos() {
		return cursoService.listarCursos();
	}

	public CursoDTO criarCurso(CursoDTO dto) {
		return cursoService.criarCurso(dto);
	}

	public CursoDTO atualizarCurso(Long id, CursoDTO dto) {
		return cursoService.atualizarCurso(id, dto);
	}
}
