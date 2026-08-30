package br.edu.ufape.backend.admin.controller;

import br.edu.ufape.backend.admin.dto.CadastroInstitucionalRequestDTO;
import br.edu.ufape.backend.admin.dto.UsuarioAdminResponseDTO;
import br.edu.ufape.backend.admin.facade.AdminFacade;
import br.edu.ufape.backend.usuario.model.Role;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/usuarios")
public class AdminUsuarioController {

	private final AdminFacade adminFacade;

	public AdminUsuarioController(AdminFacade adminFacade) {
		this.adminFacade = adminFacade;
	}

	@PostMapping
	public ResponseEntity<UsuarioAdminResponseDTO> cadastrar(
			@Valid @RequestBody CadastroInstitucionalRequestDTO request) {
		UsuarioAdminResponseDTO response = adminFacade.cadastrarUsuarioInstitucional(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<UsuarioAdminResponseDTO>> listar(@RequestParam(required = false) Role role,
			@RequestParam(required = false) Boolean ativo) {
		return ResponseEntity.ok(adminFacade.listarUsuarios(role, ativo));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<UsuarioAdminResponseDTO> alternarStatus(@PathVariable Long id) {
		return ResponseEntity.ok(adminFacade.alternarStatusUsuario(id));
	}
}
