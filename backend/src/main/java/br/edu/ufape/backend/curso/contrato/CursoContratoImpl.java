package br.edu.ufape.backend.curso.contrato;

import br.edu.ufape.backend.curso.dto.CursoDTO;
import br.edu.ufape.backend.curso.service.CursoService;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class CursoContratoImpl implements CursoContrato {

	private final CursoService cursoService;

	public CursoContratoImpl(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@Override
	public Optional<CursoDTO> buscarPorNomeOuCodigo(String termo) {
		return cursoService.buscarPorNomeOuCodigo(termo).map(CursoDTO::fromEntity);
	}
}
