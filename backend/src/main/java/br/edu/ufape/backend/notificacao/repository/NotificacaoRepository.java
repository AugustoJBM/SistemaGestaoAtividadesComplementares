package br.edu.ufape.backend.notificacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ufape.backend.notificacao.model.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

	List<Notificacao> findByDestinatarioIdOrderByDataCriacaoDesc(Long destinatarioId);

	List<Notificacao> findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(Long destinatarioId, boolean lida);

	long countByDestinatarioIdAndLidaFalse(Long destinatarioId);

	Optional<Notificacao> findByIdAndDestinatarioId(Long id, Long destinatarioId);
}
