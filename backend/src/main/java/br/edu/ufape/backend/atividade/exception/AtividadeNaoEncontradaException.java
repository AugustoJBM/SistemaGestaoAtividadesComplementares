package br.edu.ufape.backend.atividade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AtividadeNaoEncontradaException extends RuntimeException {
    public AtividadeNaoEncontradaException(String message) {
        super(message);
    }
}