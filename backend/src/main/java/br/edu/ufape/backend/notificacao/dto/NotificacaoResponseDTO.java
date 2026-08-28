package br.edu.ufape.backend.notificacao.dto;

import java.time.LocalDateTime;

import br.edu.ufape.backend.notificacao.model.Notificacao;

public record NotificacaoResponseDTO(Long id, String tipo, String titulo, String mensagem, Long solicitacaoId,
		boolean lida, LocalDateTime dataCriacao) {
	public NotificacaoResponseDTO(Notificacao notificacao) {
		this(notificacao.getId(), notificacao.getTipo().name(), notificacao.getTitulo(), notificacao.getMensagem(),
				notificacao.getSolicitacaoId(), notificacao.isLida(), notificacao.getDataCriacao());
	}
}
