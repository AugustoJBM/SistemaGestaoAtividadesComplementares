package br.edu.ufape.backend.autenticacao.security;

import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtService jwtService;
	@Mock
	private UserDetailsService userDetailsService;
	@Mock
	private TokenBlacklistService tokenBlacklistService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;

	private JwtAuthenticationFilter filter;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
		filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenBlacklistService, new ObjectMapper());
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
    @DisplayName("Branch: Requisicao OPTIONS deve passar direto no filtro")
    void devePassarRequisicaoOptions() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

	@Test
    @DisplayName("Branch: Sem header Authorization deve prosseguir sem autenticar")
    void devePassarSemHeaderAuthorization() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

	@Test
    @DisplayName("Branch: Token invalido ou na blacklist deve retornar 401")
    void deveRetornar401ParaTokenInvalido() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtService.isTokenValid("token-invalido")).thenReturn(false);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

	@Test
    @DisplayName("Branch: Usuario do token nao encontrado no banco deve retornar 401")
    void deveRetornar401QuandoUsuarioNaoExiste() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtService.isTokenValid("token-valido")).thenReturn(true);
        when(tokenBlacklistService.isTokenBlacklisted("token-valido")).thenReturn(false);
        when(jwtService.extractUsername("token-valido")).thenReturn("sumiu@ufape.edu.br");
        when(userDetailsService.loadUserByUsername("sumiu@ufape.edu.br")).thenThrow(new UsernameNotFoundException(""));
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        filter.doFilterInternal(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
