package br.edu.ufape.backend.autenticacao.facade;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.autenticacao.service.AuthService;
import br.edu.ufape.backend.usuario.model.Usuario;

@Component
public class AuthFacade {

	private final AuthService authService;

	public AuthFacade(AuthService authService) {
		this.authService = authService;
	}

	public Usuario cadastrarUsuario(CadastroUsuarioRequest request) {
		return authService.cadastrarUsuario(request);
	}

	public LoginResponse login(LoginRequest request) {
		return authService.login(request);
	}

	public void logout(String authorizationHeader) {
		authService.logout(authorizationHeader);
	}
}
