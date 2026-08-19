package br.edu.ufape.backend.comum.exception;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CertificadoInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarCertificadoInvalido(CertificadoInvalidoException ex) {
        ErroResponse erro = new ErroResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Erro de validação nos campos da requisição.");
        ErroResponse erro = new ErroResponse(mensagem, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class,
            MultipartException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(Exception ex) {
        ErroResponse erro = new ErroResponse("Parâmetros da requisição inválidos ou ausentes.", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(NoResourceFoundException ex) {
        ErroResponse erro = new ErroResponse("Recurso não encontrado.", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AcessoNegadoAtividadeException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegadoAtividade(AcessoNegadoAtividadeException ex) {
        ErroResponse erro = new ErroResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(PerfilNaoPermitidoException.class)
    public ResponseEntity<ErroResponse> tratarPerfilNaoPermitido(PerfilNaoPermitidoException ex) {
        ErroResponse erro = new ErroResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailDuplicado(EmailJaCadastradoException ex) {
        ErroResponse erro = new ErroResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErroResponse> tratarUnauthorized(UnauthorizedException ex) {
        ErroResponse erro = new ErroResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarCatchAll(Exception ex) {
        ErroResponse erro = new ErroResponse(
                "Ocorreu um erro interno inesperado no servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}