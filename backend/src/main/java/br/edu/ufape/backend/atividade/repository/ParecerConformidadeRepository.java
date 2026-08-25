package br.edu.ufape.backend.atividade.repository;

import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParecerConformidadeRepository extends JpaRepository<ParecerConformidade, Long> {

    Optional<ParecerConformidade> findByAtividadeId(Long atividadeId);

    long countByDecisaoFinalAvaliadorIsNotNull();

    long countByAvaliadorConcordouComIATrue();

    @Query("SELECT COALESCE(AVG(p.tempoProcessamentoMs), 0.0) FROM ParecerConformidade p")
    Double calcularTempoMedioMs();
}