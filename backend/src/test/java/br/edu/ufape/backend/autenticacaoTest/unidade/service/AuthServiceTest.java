package br.edu.ufape.backend.autenticacaoTest.unidade.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioContrato, jwtService, passwordEncoder, tokenBlacklistService);
    }

    @Test
    @DisplayName("Deve cadastrar estudante com sucesso")
    void deveCadastrarEstudanteComSucesso() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setNome("Estudante Teste");
        request.setEmail("novo@ufape.edu.br");
        request.setSenha("senha1234");
        request.setRole(Role.ESTUDANTE);

        when(usuarioContrato.existePorEmail("novo@ufape.edu.br")).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("senhaCriptografada");
        when(usuarioContrato.salvar(any(Estudante.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = authService.cadastrarUsuario(request);

        assertNotNull(resultado);
        assertEquals("Estudante Teste", resultado.getNome());
        verify(usuarioContrato, times(1)).salvar(any(Estudante.class));
    }

    @Test
    @DisplayName("Deve lancar EmailJaCadastradoException se email ja existir")
    void deveLancarExcecaoQuandoEmailExistir() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setEmail("existente@ufape.edu.br");

        when(usuarioContrato.existePorEmail("existente@ufape.edu.br")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> authService.cadastrarUsuario(request));
        verify(usuarioContrato, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lancar PerfilNaoPermitidoException se tentar cadastrar perfil diferente de ESTUDANTE")
    void deveLancarExcecaoQuandoPerfilNaoForEstudante() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setEmail("novo@ufape.edu.br");
        request.setRole(Role.ADMINISTRADOR);

        when(usuarioContrato.existePorEmail("novo@ufape.edu.br")).thenReturn(false);

        assertThrows(PerfilNaoPermitidoException.class, () -> authService.cadastrarUsuario(request));
        verify(usuarioContrato, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token JWT")
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest();
        request.setUsuario("aluno@ufape.edu.br");
        request.setSenha("senha1234");

        Estudante estudante = new Estudante("Aluno", "aluno@ufape.edu.br", "senhaHash");

        when(usuarioContrato.buscarPorEmail("aluno@ufape.edu.br")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senha1234", "senhaHash")).thenReturn(true);
        when(jwtService.generateToken("aluno@ufape.edu.br")).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
    }

    @Test
    @DisplayName("Deve lancar UnauthorizedException quando a senha de login for incorreta")
    void deveLancarExcecaoQuandoSenhaForIncorreta() {
        LoginRequest request = new LoginRequest();
        request.setUsuario("aluno@ufape.edu.br");
        request.setSenha("senhaErrada");

        Estudante estudante = new Estudante("Aluno", "aluno@ufape.edu.br", "senhaHash");

        when(usuarioContrato.buscarPorEmail("aluno@ufape.edu.br")).thenReturn(Optional.of(estudante));
        when(passwordEncoder.matches("senhaErrada", "senhaHash")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
