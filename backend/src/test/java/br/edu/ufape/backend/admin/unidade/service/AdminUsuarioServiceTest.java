package br.edu.ufape.backend.admin.unidade.service;

import br.edu.ufape.backend.admin.dto.CadastroInstitucionalRequestDTO;
import br.edu.ufape.backend.admin.dto.UsuarioAdminResponseDTO;
import br.edu.ufape.backend.admin.service.AdminUsuarioService;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioServiceTest {

	@Mock
	private UsuarioContrato usuarioContrato;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AdminUsuarioService adminUsuarioService;

	@Test
	@DisplayName("Deve cadastrar Avaliador institucional com sucesso")
	void deveCadastrarAvaliadorComSucesso() {
		CadastroInstitucionalRequestDTO req = new CadastroInstitucionalRequestDTO("Prof. Carlos", "carlos@ufape.edu.br",
				"senha12345", Role.AVALIADOR, "REG-123", "IA", null, null);

		when(usuarioContrato.existePorEmail("carlos@ufape.edu.br")).thenReturn(false);
		when(passwordEncoder.encode("senha12345")).thenReturn("hashSeguro");
		when(usuarioContrato.salvar(any())).thenAnswer(inv -> {
			Avaliador av = inv.getArgument(0);
			av.setId(10L);
			return av;
		});

		UsuarioAdminResponseDTO res = adminUsuarioService.cadastrarUsuarioInstitucional(req);

		assertNotNull(res);
		assertEquals(10L, res.id());
		assertEquals(Role.AVALIADOR, res.role());
		assertTrue(res.ativo());
	}

	@Test
	@DisplayName("Deve alternar status de usuario entre ativo e inativo")
	void deveAlternarStatusUsuario() {
		Avaliador av = new Avaliador("Prof", "prof@ufape.edu.br", "hash", "REG-01", "BCC");
		av.setId(5L);
		av.setIsActive(true);

		when(usuarioContrato.buscarPorId(5L)).thenReturn(Optional.of(av));
		when(usuarioContrato.salvar(any())).thenAnswer(inv -> inv.getArgument(0));

		UsuarioAdminResponseDTO res = adminUsuarioService.alternarStatusUsuario(5L);

		assertFalse(res.ativo());
		verify(usuarioContrato).salvar(av);
	}

	@Test
	@DisplayName("Deve lancar EmailJaCadastradoException para emails duplicados")
	void deveLancarExcecaoParaEmailDuplicado() {
		CadastroInstitucionalRequestDTO req = new CadastroInstitucionalRequestDTO("Duplicado", "duplicado@ufape.edu.br",
				"senha12345", Role.ADMINISTRADOR, null, null, "TOTAL", "Coord");

		when(usuarioContrato.existePorEmail("duplicado@ufape.edu.br")).thenReturn(true);

		assertThrows(EmailJaCadastradoException.class, () -> adminUsuarioService.cadastrarUsuarioInstitucional(req));
	}
}
