package br.edu.ufape.backend.curso.service;

import br.edu.ufape.backend.curso.dto.CursoDTO;
import br.edu.ufape.backend.curso.model.Curso;
import br.edu.ufape.backend.curso.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

	private final CursoRepository cursoRepository;

	public CursoService(CursoRepository cursoRepository) {
		this.cursoRepository = cursoRepository;
	}

	@Transactional(readOnly = true)
	public List<CursoDTO> listarCursos() {
		return cursoRepository.findAll().stream().map(CursoDTO::fromEntity).toList();
	}

	@Transactional
	public CursoDTO criarCurso(CursoDTO dto) {
		if (cursoRepository.existsByCodigoIgnoreCase(dto.codigo())) {
			throw new IllegalArgumentException("Já existe um curso cadastrado com o código: " + dto.codigo());
		}
		Curso curso = new Curso(dto.nome(), dto.codigo(), dto.horasAccExigidas(), dto.horasAcexExigidas());
		return CursoDTO.fromEntity(cursoRepository.save(curso));
	}

	@Transactional
	public CursoDTO atualizarCurso(Long id, CursoDTO dto) {
		Curso curso = cursoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Curso não encontrado com ID: " + id));

		curso.setNome(dto.nome());
		curso.setCodigo(dto.codigo());
		curso.setHorasAccExigidas(dto.horasAccExigidas());
		curso.setHorasAcexExigidas(dto.horasAcexExigidas());
		curso.setAtivo(dto.ativo());

		return CursoDTO.fromEntity(cursoRepository.save(curso));
	}

	@Transactional(readOnly = true)
	public Optional<Curso> buscarPorNomeOuCodigo(String termo) {
		if (termo == null || termo.isBlank())
			return Optional.empty();
		return cursoRepository.findByNomeIgnoreCase(termo.trim())
				.or(() -> cursoRepository.findByCodigoIgnoreCase(termo.trim()));
	}
}
