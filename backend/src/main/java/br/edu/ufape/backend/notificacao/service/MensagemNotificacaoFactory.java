package br.edu.ufape.backend.notificacao.service;

import br.edu.ufape.backend.notificacao.model.TipoNotificacao;

public final class MensagemNotificacaoFactory {

	public record MensagemNotificacao(TipoNotificacao tipo, String titulo, String mensagem) {
	}

	private MensagemNotificacaoFactory() {
	}

	public static MensagemNotificacao criar(String novoStatus, String justificativa) {
		if (novoStatus == null) {
			throw new IllegalArgumentException("Status não pode ser nulo");
		}
		if ((novoStatus.equals("COM_PENDENCIAS") || novoStatus.equals("REJEITADA"))
				&& (justificativa == null || justificativa.isBlank())) {
			throw new IllegalArgumentException("Justificativa é obrigatória para o status " + novoStatus);
		}
		return switch (novoStatus) {
			case "SUBMETIDA" -> new MensagemNotificacao(
					TipoNotificacao.SOLICITACAO_SUBMETIDA,
					"Solicitação enviada",
					"Sua solicitação de validação foi enviada e aguarda análise."
			);
			case "EM_ANALISE" -> new MensagemNotificacao(
					TipoNotificacao.SOLICITACAO_EM_ANALISE,
					"Solicitação em análise",
					"Sua solicitação de validação está sendo analisada."
			);
			case "COM_PENDENCIAS" -> new MensagemNotificacao(
					TipoNotificacao.SOLICITACAO_COM_PENDENCIAS,
					"Solicitação com pendências",
					"Sua solicitação apresenta pendências: " + justificativa
			);
			case "APROVADA" -> new MensagemNotificacao(
					TipoNotificacao.SOLICITACAO_APROVADA,
					"Solicitação aprovada",
					"Sua solicitação de validação foi aprovada."
			);
			case "REJEITADA" -> new MensagemNotificacao(
					TipoNotificacao.SOLICITACAO_REJEITADA,
					"Solicitação rejeitada",
					"Sua solicitação foi rejeitada: " + justificativa
			);
			default -> throw new IllegalArgumentException("Status desconhecido para notificação: " + novoStatus);
		};
	}
}
