package br.edu.ufape.backend.solicitacao.exception;

public class SolicitacaoEmAbertoException extends RuntimeException {

    public SolicitacaoEmAbertoException(String mensagem) {
        super(mensagem);
    }

    public SolicitacaoEmAbertoException() {
        super("Já existe uma solicitação de validação em aberto para este estudante.");
    }
}

