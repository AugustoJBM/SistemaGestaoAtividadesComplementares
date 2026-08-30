package br.edu.ufape.backend.usuario.contrato;

import java.util.List;
import java.util.Optional;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface UsuarioContrato {
	List<Usuario> listarTodos();
	Optional<Usuario> buscarPorEmail(String email);
	Optional<Usuario> buscarPorId(Long id);
	boolean existePorEmail(String email);
	Usuario salvar(Usuario usuario);
}
