package br.edu.ufape.backend.solicitacao.exception;

import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TransicaoEstadoInvalidaException extends RuntimeException {

    public TransicaoEstadoInvalidaException(StatusSolicitacao de, StatusSolicitacao para) {
        super("Transicao invalida: " + de + " -> " + para);
    }
}
