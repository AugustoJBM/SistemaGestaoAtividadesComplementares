package br.edu.ufape.backend.usuarioTest.unidade.service;

import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;
import br.edu.ufape.backend.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    @DisplayName("Deve salvar usuario com sucesso")
    void deveSalvarUsuario() {
        Estudante estudante = new Estudante("Jose", "jose@ufape.edu.br", "senhaHash");
        when(usuarioRepository.save(estudante)).thenReturn(estudante);

        Usuario resultado = usuarioService.salvar(estudante);

        assertNotNull(resultado);
        assertEquals("Jose", resultado.getNome());
        verify(usuarioRepository, times(1)).save(estudante);
    }

    @Test
    @DisplayName("Deve buscar usuario por e-mail com sucesso")
    void deveBuscarUsuarioPorEmail() {
        Estudante estudante = new Estudante("Jose", "jose@ufape.edu.br", "senhaHash");
        when(usuarioRepository.findByEmail("jose@ufape.edu.br")).thenReturn(Optional.of(estudante));

        Optional<Usuario> resultado = usuarioService.buscarPorEmail("jose@ufape.edu.br");

        assertTrue(resultado.isPresent());
        assertEquals("jose@ufape.edu.br", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Deve verificar se e-mail existe no banco")
    void deveVerificarSeEmailExiste() {
        when(usuarioRepository.existsByEmail("jose@ufape.edu.br")).thenReturn(true);

        boolean existe = usuarioService.existePorEmail("jose@ufape.edu.br");

        assertTrue(existe);
        verify(usuarioRepository, times(1)).existsByEmail("jose@ufape.edu.br");
    }
}