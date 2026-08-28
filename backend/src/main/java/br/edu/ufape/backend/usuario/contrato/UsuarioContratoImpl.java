package br.edu.ufape.backend.usuario.contrato;

import java.util.Optional;
import org.springframework.stereotype.Component;

import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.service.UsuarioService;

@Component
public class UsuarioContratoImpl implements UsuarioContrato {

	private final UsuarioService usuarioService;

	public UsuarioContratoImpl(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@Override
	public Optional<Usuario> buscarPorEmail(String email) {
		return usuarioService.buscarPorEmail(email);
	}

	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioService.buscarPorId(id);
	}

	@Override
	public boolean existePorEmail(String email) {
		return usuarioService.existePorEmail(email);
	}

	@Override
	public Usuario salvar(Usuario usuario) {
		return usuarioService.salvar(usuario);
	}
}
