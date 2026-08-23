package br.edu.ufape.backend.ia.repository;

import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegulamentoChunkRepository extends JpaRepository<RegulamentoChunk, Long> {
}