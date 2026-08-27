package br.edu.ufape.backend.usuario.unidade.contrato;

import br.edu.ufape.backend.usuario.contrato.UsuarioContratoImpl;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;
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
class UsuarioContratoImplTest {

	@Mock
	private UsuarioService usuarioService;

	private UsuarioContratoImpl usuarioContrato;

	@BeforeEach
	void setUp() {
		usuarioContrato = new UsuarioContratoImpl(usuarioService);
	}

	@Test
	@DisplayName("Deve delegar salvamento para o UsuarioService")
	void deveDelegarSalvar() {
		Estudante estudante = new Estudante("Maria", "maria@ufape.edu.br", "senhaHash");
		when(usuarioService.salvar(estudante)).thenReturn(estudante);

		Usuario resultado = usuarioContrato.salvar(estudante);

		assertNotNull(resultado);
		verify(usuarioService, times(1)).salvar(estudante);
	}

	@Test
	@DisplayName("Deve delegar busca por email para o UsuarioService")
	void deveDelegarBuscarPorEmail() {
		Estudante estudante = new Estudante("Maria", "maria@ufape.edu.br", "senhaHash");
		when(usuarioService.buscarPorEmail("maria@ufape.edu.br")).thenReturn(Optional.of(estudante));

		Optional<Usuario> resultado = usuarioContrato.buscarPorEmail("maria@ufape.edu.br");

		assertTrue(resultado.isPresent());
		verify(usuarioService, times(1)).buscarPorEmail("maria@ufape.edu.br");
	}

	@Test
    @DisplayName("Deve delegar verificacao de existencia por email para o UsuarioService")
    void deveDelegarExistePorEmail() {
        when(usuarioService.existePorEmail("maria@ufape.edu.br")).thenReturn(true);

        boolean existe = usuarioContrato.existePorEmail("maria@ufape.edu.br");

        assertTrue(existe);
        verify(usuarioService, times(1)).existePorEmail("maria@ufape.edu.br");
    }
}
