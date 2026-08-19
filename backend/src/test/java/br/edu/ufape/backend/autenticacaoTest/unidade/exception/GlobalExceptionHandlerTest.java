package br.edu.ufape.backend.autenticacaoTest.unidade.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar CertificadoInvalidoException retornando 400 Bad Request com a mensagem original")
    void deveTratarCertificadoInvalidoException() {
        String mensagem = "Certificado inválido. Aceitos: PDF, PNG ou JPEG";
        CertificadoInvalidoException ex = new CertificadoInvalidoException(mensagem);

        ResponseEntity<String> response = exceptionHandler.tratarCertificadoInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mensagem, response.getBody());
    }

    @Test
    @DisplayName("Deve tratar AcessoNegadoAtividadeException retornando 403 Forbidden com a mensagem original")
    void deveTratarAcessoNegadoAtividadeException() {
        String mensagem = "Apenas estudantes podem listar atividades complementares.";
        AcessoNegadoAtividadeException ex = new AcessoNegadoAtividadeException(mensagem);

        ResponseEntity<String> response = exceptionHandler.tratarAcessoNegadoAtividade(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(mensagem, response.getBody());
    }

    @Test
    @DisplayName("Deve tratar PerfilNaoPermitidoException retornando 403 Forbidden com a mensagem original")
    void deveTratarPerfilNaoPermitidoException() {
        PerfilNaoPermitidoException ex = new PerfilNaoPermitidoException();

        ResponseEntity<String> response = exceptionHandler.tratarPerfilNaoPermitido(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody());
    }

    @Test
    @DisplayName("Deve tratar EmailJaCadastradoException retornando 409 Conflict com a mensagem original")
    void deveTratarEmailJaCadastradoException() {
        String email = "aluno@ufape.edu.br";
        EmailJaCadastradoException ex = new EmailJaCadastradoException(email);

        ResponseEntity<String> response = exceptionHandler.tratarEmailDuplicado(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody());
    }

    @Test
    @DisplayName("Deve tratar UnauthorizedException retornando 401 Unauthorized com a mensagem original")
    void deveTratarUnauthorizedException() {
        String mensagem = "Credenciais inválidas";
        UnauthorizedException ex = new UnauthorizedException(mensagem);

        ResponseEntity<String> response = exceptionHandler.tratarUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(mensagem, response.getBody());
    }
}