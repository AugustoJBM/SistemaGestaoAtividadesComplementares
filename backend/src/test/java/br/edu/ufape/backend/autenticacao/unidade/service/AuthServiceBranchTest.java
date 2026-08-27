package br.edu.ufape.backend.autenticacao.unidade.service;

import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.autenticacao.service.AuthService;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceBranchTest {

	@Mock
	private UsuarioContrato usuarioContrato;
	@Mock
	private JwtService jwtService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private TokenBlacklistService tokenBlacklistService;

	@InjectMocks
	private AuthService authService;

	@Test
	@DisplayName("Branch: logout com header nulo ou sem Bearer lanca UnauthorizedException")
	void deveLancarExcecaoLogoutInvalido() {
		assertThrows(UnauthorizedException.class, () -> authService.logout(null));
		assertThrows(UnauthorizedException.class, () -> authService.logout("Basic 12345"));
	}

	@Test
    @DisplayName("Branch: logout com token invalido lanca UnauthorizedException")
    void deveLancarExcecaoLogoutTokenInvalido() {
        when(jwtService.isTokenValid("token-expirado")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> authService.logout("Bearer token-expirado"));
    }

	@Test
    @DisplayName("Branch: logout com token valido adiciona na blacklist")
    void deveFazerLogoutComSucesso() {
        when(jwtService.isTokenValid("token-ok")).thenReturn(true);
        assertDoesNotThrow(() -> authService.logout("Bearer token-ok"));
        verify(tokenBlacklistService).blacklistToken("token-ok");
    }

	@Test
	@DisplayName("Branch: login com usuario nulo trata como string vazia e lanca Unauthorized")
	void deveTratarUsuarioNuloNoLogin() {
		LoginRequest req = new LoginRequest();
		req.setUsuario(null);
		req.setSenha("123");

		when(usuarioContrato.buscarPorEmail("")).thenReturn(Optional.empty());
		assertThrows(UnauthorizedException.class, () -> authService.login(req));
	}
}
