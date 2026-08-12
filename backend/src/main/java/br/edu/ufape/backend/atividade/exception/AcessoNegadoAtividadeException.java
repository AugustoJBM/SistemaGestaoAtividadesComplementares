package br.edu.ufape.backend.atividade.exception;

public class AcessoNegadoAtividadeException extends RuntimeException {

    public AcessoNegadoAtividadeException(String mensagem) {
        super(mensagem);
    }
}
