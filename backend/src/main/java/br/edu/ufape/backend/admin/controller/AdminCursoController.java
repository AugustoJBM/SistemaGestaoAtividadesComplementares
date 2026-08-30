package br.edu.ufape.backend.admin.controller;

import br.edu.ufape.backend.admin.facade.AdminFacade;
import br.edu.ufape.backend.curso.dto.CursoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cursos")
public class AdminCursoController {

	private final AdminFacade adminFacade;

	public AdminCursoController(AdminFacade adminFacade) {
		this.adminFacade = adminFacade;
	}

	@GetMapping
	public ResponseEntity<List<CursoDTO>> listar() {
		return ResponseEntity.ok(adminFacade.listarCursos());
	}

	@PostMapping
	public ResponseEntity<CursoDTO> criar(@Valid @RequestBody CursoDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(adminFacade.criarCurso(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CursoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CursoDTO dto) {
		return ResponseEntity.ok(adminFacade.atualizarCurso(id, dto));
	}
}
