package br.edu.ufape.backend.autenticacaoTest.unidade.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.autenticacao.service.AuthService;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.usuario.model.Usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

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
    @DisplayName("Deve cadastrar estudante com sucesso")
    void deveCadastrarEstudanteComSucesso() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setNome("Lucas Silva");
        request.setEmail("lucas@ufape.edu.br");
        request.setSenha("senha1234");
        request.setRole(Role.ESTUDANTE);

        when(usuarioContrato.existePorEmail("lucas@ufape.edu.br")).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("hashSeguro");
        when(usuarioContrato.salvar(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuarioSalvo = authService.cadastrarUsuario(request);

        assertNotNull(usuarioSalvo);
        assertEquals("lucas@ufape.edu.br", usuarioSalvo.getEmail());
        assertEquals("hashSeguro", usuarioSalvo.getSenhaHash());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuário com e-mail já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setEmail("duplicado@ufape.edu.br");
        request.setSenha("senha1234");

        when(usuarioContrato.existePorEmail("duplicado@ufape.edu.br")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> authService.cadastrarUsuario(request));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar autocadastrar perfil não permitido")
    void deveLancarExcecaoParaPerfilNaoPermitido() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setEmail("avaliador@ufape.edu.br");
        request.setSenha("senha1234");
        request.setRole(Role.AVALIADOR);

        when(usuarioContrato.existePorEmail("avaliador@ufape.edu.br")).thenReturn(false);

        assertThrows(PerfilNaoPermitidoException.class, () -> authService.cadastrarUsuario(request));
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e gerar token JWT com role")
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest();
        request.setUsuario("aluno@ufape.edu.br");
        request.setSenha("senha1234");

        Estudante estudante = new Estudante("Aluno Teste", "aluno@ufape.edu.br", "hashSeguro");

        when(usuarioContrato.buscarPorEmail("aluno@ufape.edu.br")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senha1234", "hashSeguro")).thenReturn(true);
        when(jwtService.generateToken("aluno@ufape.edu.br", "ESTUDANTE")).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        verify(jwtService, times(1)).generateToken("aluno@ufape.edu.br", "ESTUDANTE");
    }

    @Test
    @DisplayName("Deve lançar exceção de não autorizado quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        LoginRequest request = new LoginRequest();
        request.setUsuario("inexistente@ufape.edu.br");
        request.setSenha("senha1234");

        when(usuarioContrato.buscarPorEmail("inexistente@ufape.edu.br")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Deve lançar exceção de não autorizado quando senha for inválida")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        LoginRequest request = new LoginRequest();
        request.setUsuario("aluno@ufape.edu.br");
        request.setSenha("senhaIncorreta");

        Estudante estudante = new Estudante("Aluno Teste", "aluno@ufape.edu.br", "hashSeguro");

        when(usuarioContrato.buscarPorEmail("aluno@ufape.edu.br")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaIncorreta", "hashSeguro")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Deve realizar logout e invalidar o token na blacklist")
    void deveRealizarLogoutComSucesso() {
        String authHeader = "Bearer token-valido";

        when(jwtService.isTokenValid("token-valido")).thenReturn(true);

        authService.logout(authHeader);

        verify(tokenBlacklistService, times(1)).blacklistToken("token-valido");
    }

    @Test
    @DisplayName("Deve lançar exceção no logout quando header for ausente ou inválido")
    void deveLancarExcecaoLogoutHeaderInvalido() {
        assertThrows(UnauthorizedException.class, () -> authService.logout(null));
        assertThrows(UnauthorizedException.class, () -> authService.logout("Invalido token"));
    }
}