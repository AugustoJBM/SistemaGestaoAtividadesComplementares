package br.edu.ufape.backend.usuarioTest.unidade.service;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;
import br.edu.ufape.backend.usuario.service.UsuarioService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void deveSalvarUsuario() {
        Estudante estudante = new Estudante("Lucas Silva", "lucas@ufape.edu.br", "hash123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(estudante);

        Usuario resultado = usuarioService.salvar(estudante);

        assertNotNull(resultado);
        assertEquals("lucas@ufape.edu.br", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(estudante);
    }

    @Test
    @DisplayName("Deve buscar usuário por e-mail ignorando maiúsculas/minúsculas")
    void deveBuscarUsuarioPorEmail() {
        String email = "aluno@ufape.edu.br";
        Estudante estudante = new Estudante("Aluno Teste", email, "hash123");

        when(usuarioRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(estudante));
        lenient().when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(estudante));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail(email);

        assertTrue(resultado.isPresent());
        assertEquals(email, resultado.get().getEmail());
        verify(usuarioRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    @DisplayName("Deve verificar se e-mail existe ignorando maiúsculas/minúsculas")
    void deveVerificarSeEmailExiste() {
        String email = "aluno@ufape.edu.br";

        when(usuarioRepository.existsByEmailIgnoreCase(email)).thenReturn(true);
        lenient().when(usuarioRepository.existsByEmail(email)).thenReturn(true);

        boolean existe = usuarioService.existePorEmail(email);

        assertTrue(existe);
        verify(usuarioRepository, times(1)).existsByEmailIgnoreCase(email);
    }
}