package br.edu.ufape.backend.autenticacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CertificadoInvalidoException.class)
    public ResponseEntity<String> tratarCertificadoInvalido(CertificadoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AcessoNegadoAtividadeException.class)
    public ResponseEntity<String> tratarAcessoNegadoAtividade(AcessoNegadoAtividadeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(PerfilNaoPermitidoException.class)
    public ResponseEntity<String> tratarPerfilNaoPermitido(PerfilNaoPermitidoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<String> tratarEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> tratarUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
