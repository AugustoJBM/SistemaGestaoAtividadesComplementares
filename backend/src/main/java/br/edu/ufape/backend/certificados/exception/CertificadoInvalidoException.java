package br.edu.ufape.backend.certificados.exception;

public class CertificadoInvalidoException extends RuntimeException {

    public CertificadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
