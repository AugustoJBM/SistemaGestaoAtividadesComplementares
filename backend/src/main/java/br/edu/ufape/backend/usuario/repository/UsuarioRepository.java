package br.edu.ufape.backend.usuario.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
}