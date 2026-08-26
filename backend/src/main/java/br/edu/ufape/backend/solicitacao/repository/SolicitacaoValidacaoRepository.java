package br.edu.ufape.backend.solicitacao.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResumoResponseDTO;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public interface SolicitacaoValidacaoRepository extends JpaRepository<SolicitacaoValidacao, Long> {

    boolean existsByEstudanteIdAndStatusIn(Long estudanteId, Collection<StatusSolicitacao> statusList);

    @Query("SELECT COUNT(s) > 0 FROM SolicitacaoValidacao s JOIN s.itens item " +
           "WHERE item.atividadeId = :atividadeId AND s.status IN :statusList")
    boolean existsByAtividadeIdAndStatusIn(
            @Param("atividadeId") Long atividadeId,
            @Param("statusList") Collection<StatusSolicitacao> statusList);

    List<SolicitacaoValidacao> findByEstudanteId(Long estudanteId);

    @Query("SELECT new br.edu.ufape.backend.solicitacao.dto.SolicitacaoResumoResponseDTO(" +
           "s.id, s.status, s.dataSubmissao, s.dataAvaliacao, COUNT(item)) " +
           "FROM SolicitacaoValidacao s LEFT JOIN s.itens item " +
           "WHERE s.estudanteId = :estudanteId " +
           "GROUP BY s.id, s.status, s.dataSubmissao, s.dataAvaliacao " +
           "ORDER BY s.dataSubmissao DESC")
    List<SolicitacaoResumoResponseDTO> findResumosByEstudanteIdOrderByDataSubmissaoDesc(@Param("estudanteId") Long estudanteId);

    @Query("SELECT s FROM SolicitacaoValidacao s LEFT JOIN FETCH s.itens WHERE s.id = :id AND s.estudanteId = :estudanteId")
    Optional<SolicitacaoValidacao> findByIdAndEstudanteId(@Param("id") Long id, @Param("estudanteId") Long estudanteId);
}