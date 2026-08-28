package br.edu.ufape.backend.usuario.contrato;

import java.util.Optional;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface UsuarioContrato {
	Optional<Usuario> buscarPorEmail(String email);
	Optional<Usuario> buscarPorId(Long id);
	boolean existePorEmail(String email);
	Usuario salvar(Usuario usuario);
}
