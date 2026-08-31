package br.edu.ufape.backend.notificacao.contrato;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory;
import br.edu.ufape.backend.notificacao.service.MensagemNotificacaoFactory.MensagemNotificacao;
import br.edu.ufape.backend.notificacao.service.NotificacaoService;

@Component
public class NotificacaoContratoImpl implements NotificacaoContrato {

	private final NotificacaoService notificacaoService;

	public NotificacaoContratoImpl(NotificacaoService notificacaoService) {
		this.notificacaoService = notificacaoService;
	}

	@Override
	public void notificarMudancaStatusSolicitacao(Long destinatarioId, Long solicitacaoId, String novoStatus,
			String justificativa) {
		MensagemNotificacao msg = MensagemNotificacaoFactory.criar(novoStatus, justificativa);
		notificacaoService.registrar(destinatarioId, msg.tipo(), msg.titulo(), msg.mensagem(), solicitacaoId);
	}
}
