package br.edu.ufape.backend.usuario.service;

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

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmailIgnoreCase(email);
    }
}