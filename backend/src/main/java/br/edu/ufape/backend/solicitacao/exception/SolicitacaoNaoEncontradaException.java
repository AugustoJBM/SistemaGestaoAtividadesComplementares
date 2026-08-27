package br.edu.ufape.backend.solicitacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SolicitacaoNaoEncontradaException extends RuntimeException {

    public SolicitacaoNaoEncontradaException(Long id) {
        super("Solicitação não encontrada: " + id);
    }

    public SolicitacaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public SolicitacaoNaoEncontradaException() {
        super("Solicitação não encontrada.");
    }
}
