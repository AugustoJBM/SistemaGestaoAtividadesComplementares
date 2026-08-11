package br.edu.ufape.backend.autenticacaoTest.unidade.service;

import br.edu.ufape.backend.autenticacao.service.CustomUserDetailsService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioContrato usuarioContrato;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(usuarioContrato);
    }

    @Test
    @DisplayName("Deve carregar UserDetails com sucesso quando email existir")
    void deveCarregarUserDetailsQuandoEmailExistir() {
        Estudante estudante = new Estudante("Teste", "teste@ufape.edu.br", "senhaHash");
        when(usuarioContrato.buscarPorEmail("teste@ufape.edu.br")).thenReturn(Optional.of(estudante));

        UserDetails userDetails = userDetailsService.loadUserByUsername("teste@ufape.edu.br");

        assertNotNull(userDetails);
        assertEquals("teste@ufape.edu.br", userDetails.getUsername());
        assertEquals("senhaHash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDANTE")));
    }

    @Test
    @DisplayName("Deve lancar UsernameNotFoundException quando email nao existir")
    void deveLancarExcecaoQuandoEmailNaoExistir() {
        when(usuarioContrato.buscarPorEmail("inexistente@ufape.edu.br")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inexistente@ufape.edu.br"));
    }
}