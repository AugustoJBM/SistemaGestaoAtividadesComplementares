package br.edu.ufape.backend.notificacao.contrato;

public interface NotificacaoContrato {
	void notificarMudancaStatusSolicitacao(Long destinatarioId, Long solicitacaoId, String novoStatus,
			String justificativa);
}
