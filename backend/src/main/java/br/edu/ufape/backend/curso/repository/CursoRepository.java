package br.edu.ufape.backend.curso.repository;

import br.edu.ufape.backend.curso.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
	Optional<Curso> findByNomeIgnoreCase(String nome);
	Optional<Curso> findByCodigoIgnoreCase(String codigo);
	boolean existsByNomeIgnoreCase(String nome);
	boolean existsByCodigoIgnoreCase(String codigo);
}
