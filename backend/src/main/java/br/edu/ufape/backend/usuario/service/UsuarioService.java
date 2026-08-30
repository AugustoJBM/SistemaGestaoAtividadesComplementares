package br.edu.ufape.backend.usuario.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> buscarPorEmail(String email) {
		return usuarioRepository.findByEmailIgnoreCase(email);
	}

	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	public boolean existePorEmail(String email) {
		return usuarioRepository.existsByEmailIgnoreCase(email);
	}

	public Usuario salvar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
}
