package br.edu.ufape.backend.ia.exception;

public class IaProcessamentoException extends RuntimeException {
    public IaProcessamentoException(String mensagem) {
        super(mensagem);
    }

    public IaProcessamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}