package br.edu.ufape.backend.atividade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AcessoNegadoAtividadeException extends RuntimeException {
    public AcessoNegadoAtividadeException(String message) {
        super(message);
    }
}