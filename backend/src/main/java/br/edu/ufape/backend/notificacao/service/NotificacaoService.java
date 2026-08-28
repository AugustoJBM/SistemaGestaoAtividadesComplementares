package br.edu.ufape.backend.notificacao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.backend.notificacao.exception.NotificacaoNaoEncontradaException;
import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.model.TipoNotificacao;
import br.edu.ufape.backend.notificacao.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

	private final NotificacaoRepository notificacaoRepository;

	public NotificacaoService(NotificacaoRepository notificacaoRepository) {
		this.notificacaoRepository = notificacaoRepository;
	}

	@Transactional
	public Notificacao registrar(Long destinatarioId, TipoNotificacao tipo, String titulo, String mensagem,
			Long solicitacaoId) {
		Notificacao notificacao = new Notificacao(destinatarioId, tipo, titulo, mensagem, solicitacaoId);
		return notificacaoRepository.save(notificacao);
	}

	@Transactional(readOnly = true)
	public List<Notificacao> listar(Long destinatarioId, Boolean apenasNaoLidas) {
		if (apenasNaoLidas == null) {
			return notificacaoRepository.findByDestinatarioIdOrderByDataCriacaoDesc(destinatarioId);
		}
		return notificacaoRepository.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(destinatarioId, !apenasNaoLidas);
	}

	@Transactional(readOnly = true)
	public long contarNaoLidas(Long destinatarioId) {
		return notificacaoRepository.countByDestinatarioIdAndLidaFalse(destinatarioId);
	}

	@Transactional
	public Notificacao marcarComoLida(Long notificacaoId, Long destinatarioId) {
		Notificacao notificacao = notificacaoRepository.findByIdAndDestinatarioId(notificacaoId, destinatarioId)
				.orElseThrow(() -> new NotificacaoNaoEncontradaException(notificacaoId));
		if (!notificacao.isLida()) {
			notificacao.setLida(true);
			return notificacaoRepository.save(notificacao);
		}
		return notificacao;
	}

	@Transactional
	public int marcarTodasComoLidas(Long destinatarioId) {
		List<Notificacao> naoLidas = notificacaoRepository
				.findByDestinatarioIdAndLidaOrderByDataCriacaoDesc(destinatarioId, false);
		if (naoLidas.isEmpty()) {
			return 0;
		}
		naoLidas.forEach(notificacao -> notificacao.setLida(true));
		notificacaoRepository.saveAll(naoLidas);
		return naoLidas.size();
	}
}
