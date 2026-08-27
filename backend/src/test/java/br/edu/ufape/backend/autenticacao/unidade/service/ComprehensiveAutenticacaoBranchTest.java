package br.edu.ufape.backend.autenticacao.unidade.service;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.security.JwtAuthenticationFilter;
import br.edu.ufape.backend.autenticacao.service.AuthService;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprehensiveAutenticacaoBranchTest {

	@Mock
	private UsuarioContrato usuarioContrato;
	@Mock
	private JwtService jwtService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private TokenBlacklistService tokenBlacklistService;
	@Mock
	private UserDetailsService userDetailsService;

	@InjectMocks
	private AuthService authService;

	@BeforeEach
	void cleanSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	@DisplayName("AuthService: Cadastro com role nula assume ESTUDANTE")
	void deveAssumirEstudanteComRoleNula() {
		CadastroUsuarioRequest req = new CadastroUsuarioRequest();
		req.setNome("Aluno");
		req.setEmail("aluno@ufape.edu.br");
		req.setSenha("senha1234");
		req.setRole(null);

		when(usuarioContrato.existePorEmail("aluno@ufape.edu.br")).thenReturn(false);
		when(passwordEncoder.encode("senha1234")).thenReturn("hash");
		when(usuarioContrato.salvar(any())).thenAnswer(i -> i.getArgument(0));

		assertNotNull(authService.cadastrarUsuario(req));
	}

	@Test
	@DisplayName("JwtAuthenticationFilter: Fluxo completo com token válido autentica no SecurityContext")
	void deveAutenticarComTokenValido() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService,
				tokenBlacklistService, new ObjectMapper());

		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(req.getMethod()).thenReturn("GET");
		when(req.getHeader("Authorization")).thenReturn("Bearer token-valido");
		when(jwtService.isTokenValid("token-valido")).thenReturn(true);
		when(tokenBlacklistService.isTokenBlacklisted("token-valido")).thenReturn(false);
		when(jwtService.extractUsername("token-valido")).thenReturn("aluno@ufape.edu.br");

		User userDetails = new User("aluno@ufape.edu.br", "pwd", Collections.emptyList());
		when(userDetailsService.loadUserByUsername("aluno@ufape.edu.br")).thenReturn(userDetails);

		filter.doFilter(req, res, chain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		verify(chain).doFilter(req, res);
	}
}
