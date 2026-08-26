package br.edu.ufape.backend.solicitacao.exception;

public class EstudanteSemAtividadesException extends RuntimeException {

    public EstudanteSemAtividadesException(String mensagem) {
        super(mensagem);
    }

    public EstudanteSemAtividadesException() {
        super("Não é possível submeter solicitação sem atividades complementares cadastradas.");
    }
}