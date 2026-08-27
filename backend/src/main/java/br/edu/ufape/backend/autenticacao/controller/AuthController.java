package br.edu.ufape.backend.autenticacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.autenticacao.dto.UsuarioResponse;
import br.edu.ufape.backend.autenticacao.facade.AuthFacade;
import br.edu.ufape.backend.usuario.model.Usuario;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthFacade authFacade;

	public AuthController(AuthFacade authFacade) {
		this.authFacade = authFacade;
	}

	@PostMapping("/cadastro")
	public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
		Usuario usuario = authFacade.cadastrarUsuario(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponse(usuario));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse loginResponse = authFacade.login(request);
		return ResponseEntity.ok(loginResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
		authFacade.logout(authorization);
		return ResponseEntity.ok().build();
	}
}
