package br.edu.ufape.backend.atividade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface AtividadeComplementarRepository extends JpaRepository<AtividadeComplementar, Long> {

    List<AtividadeComplementar> findByEstudante(Usuario estudante);

    List<AtividadeComplementar> findByEstudanteAndNatureza(Usuario estudante, Natureza natureza);

    @Query(value = """
            SELECT a FROM AtividadeComplementar a
            WHERE a.estudante = :estudante
            AND (:natureza IS NULL OR a.natureza = :natureza)
            AND (:categoria IS NULL OR a.categoria = :categoria)
            """,
            countQuery = """
            SELECT COUNT(a) FROM AtividadeComplementar a
            WHERE a.estudante = :estudante
            AND (:natureza IS NULL OR a.natureza = :natureza)
            AND (:categoria IS NULL OR a.categoria = :categoria)
            """)
    Page<AtividadeComplementar> findByEstudanteComFiltros(
            @Param("estudante") Usuario estudante,
            @Param("natureza") Natureza natureza,
            @Param("categoria") Categoria categoria,
            Pageable pageable);

    Optional<AtividadeComplementar> findByIdAndEstudante(Long id, Usuario estudante);
}
