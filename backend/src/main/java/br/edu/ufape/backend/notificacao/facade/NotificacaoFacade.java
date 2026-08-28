package br.edu.ufape.backend.notificacao.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.notificacao.dto.ContagemNaoLidasResponseDTO;
import br.edu.ufape.backend.notificacao.dto.NotificacaoResponseDTO;
import br.edu.ufape.backend.notificacao.model.Notificacao;
import br.edu.ufape.backend.notificacao.service.NotificacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Usuario;

@Component
public class NotificacaoFacade {

	private final NotificacaoService notificacaoService;
	private final UsuarioContrato usuarioContrato;

	public NotificacaoFacade(NotificacaoService notificacaoService, UsuarioContrato usuarioContrato) {
		this.notificacaoService = notificacaoService;
		this.usuarioContrato = usuarioContrato;
	}

	public List<NotificacaoResponseDTO> listar(String email, Boolean apenasNaoLidas) {
		Long destinatarioId = obterDestinatarioId(email);
		return notificacaoService.listar(destinatarioId, apenasNaoLidas).stream().map(NotificacaoResponseDTO::new)
				.toList();
	}

	public ContagemNaoLidasResponseDTO contarNaoLidas(String email) {
		Long destinatarioId = obterDestinatarioId(email);
		return new ContagemNaoLidasResponseDTO(notificacaoService.contarNaoLidas(destinatarioId));
	}

	public NotificacaoResponseDTO marcarComoLida(String email, Long notificacaoId) {
		Long destinatarioId = obterDestinatarioId(email);
		Notificacao notificacao = notificacaoService.marcarComoLida(notificacaoId, destinatarioId);
		return new NotificacaoResponseDTO(notificacao);
	}

	public void marcarTodasComoLidas(String email) {
		Long destinatarioId = obterDestinatarioId(email);
		notificacaoService.marcarTodasComoLidas(destinatarioId);
	}

	private Long obterDestinatarioId(String email) {
		Usuario usuario = usuarioContrato.buscarPorEmail(email)
				.orElseThrow(() -> new UnauthorizedException("Usuário não encontrado ou não autenticado."));
		return usuario.getId();
	}
}
