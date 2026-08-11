package br.edu.ufape.backend.autenticacaoTest.unidade.facade;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.autenticacao.facade.AuthFacade;
import br.edu.ufape.backend.autenticacao.service.AuthService;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private AuthService authService;

    private AuthFacade authFacade;

    @BeforeEach
    void setUp() {
        authFacade = new AuthFacade(authService);
    }

    @Test
    @DisplayName("Deve delegar cadastro de usuario para o AuthService")
    void deveDelegarCadastrarUsuario() {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        Estudante estudante = new Estudante("Facade Teste", "facade@ufape.edu.br", "senhaHash");
        when(authService.cadastrarUsuario(request)).thenReturn(estudante);

        Usuario resultado = authFacade.cadastrarUsuario(request);

        assertNotNull(resultado);
        verify(authService, times(1)).cadastrarUsuario(request);
    }

    @Test
    @DisplayName("Deve delegar login para o AuthService")
    void deveDelegarLogin() {
        LoginRequest request = new LoginRequest();
        LoginResponse response = new LoginResponse("token", "Bearer");
        when(authService.login(request)).thenReturn(response);

        LoginResponse resultado = authFacade.login(request);

        assertNotNull(resultado);
        assertEquals("token", resultado.getToken());
        verify(authService, times(1)).login(request);
    }

    @Test
    @DisplayName("Deve delegar logout para o AuthService")
    void deveDelegarLogout() {
        String header = "Bearer token-123";

        authFacade.logout(header);

        verify(authService, times(1)).logout(header);
    }
}