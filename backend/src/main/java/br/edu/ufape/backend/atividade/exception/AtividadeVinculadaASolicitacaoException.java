package br.edu.ufape.backend.atividade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AtividadeVinculadaASolicitacaoException extends RuntimeException {
    public AtividadeVinculadaASolicitacaoException() {
        super("a atividade faz parte de uma solicitação em análise");
    }
}
