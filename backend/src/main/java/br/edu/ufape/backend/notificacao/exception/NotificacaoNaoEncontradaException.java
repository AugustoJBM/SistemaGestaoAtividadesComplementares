package br.edu.ufape.backend.notificacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotificacaoNaoEncontradaException extends RuntimeException {
	public NotificacaoNaoEncontradaException(Long id) {
		super("Notificação não encontrada: " + id);
	}
	public NotificacaoNaoEncontradaException(String mensagem) {
		super(mensagem);
	}
	public NotificacaoNaoEncontradaException() {
		super("Notificação não encontrada.");
	}
}
