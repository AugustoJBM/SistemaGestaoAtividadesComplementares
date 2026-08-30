package br.edu.ufape.backend.curso.contrato;

import br.edu.ufape.backend.curso.dto.CursoDTO;
import java.util.Optional;

public interface CursoContrato {
	Optional<CursoDTO> buscarPorNomeOuCodigo(String termo);
}
