package br.edu.ufape.backend.comumTest.unidade.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.comum.exception.ErroResponse;
import br.edu.ufape.backend.comum.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar CertificadoInvalidoException retornando 400 com ErroResponse estruturado")
    void deveTratarCertificadoInvalido() {
        CertificadoInvalidoException ex = new CertificadoInvalidoException("Arquivo vazio");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarCertificadoInvalido(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Arquivo vazio", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException retornando 400 com mensagem do campo")
    void deveTratarValidacao() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("cadastroRequest", "email", "Email inválido");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarValidacao(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email inválido", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar erros de requisição inválida retornando 400")
    void deveTratarRequisicaoInvalida() {
        IllegalArgumentException ex = new IllegalArgumentException("Parâmetro inválido");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarRequisicaoInvalida(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Parâmetro inválido", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar NoResourceFoundException retornando 404")
    void deveTratarNoResourceFoundException() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        when(ex.getMessage()).thenReturn("Recurso não encontrado.");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recurso não encontrado.", response.getBody().message());
        assertEquals(404, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar AcessoNegadoAtividadeException retornando 403")
    void deveTratarAcessoNegadoAtividade() {
        AcessoNegadoAtividadeException ex = new AcessoNegadoAtividadeException("Acesso negado");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarAcessoNegadoAtividade(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado", response.getBody().message());
        assertEquals(403, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar PerfilNaoPermitidoException retornando 403")
    void deveTratarPerfilNaoPermitido() {
        PerfilNaoPermitidoException ex = new PerfilNaoPermitidoException();

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarPerfilNaoPermitido(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(403, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar EmailJaCadastradoException retornando 409")
    void deveTratarEmailDuplicado() {
        EmailJaCadastradoException ex = new EmailJaCadastradoException("teste@ufape.edu.br");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarEmailDuplicado(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(409, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar SolicitacaoEmAbertoException retornando 409")
    void deveTratarSolicitacaoEmAberto() {
        SolicitacaoEmAbertoException ex = new SolicitacaoEmAbertoException("Já existe solicitação em aberto");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarSolicitacaoEmAberto(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Já existe solicitação em aberto", response.getBody().message());
        assertEquals(409, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar EstudanteSemAtividadesException retornando 422")
    void deveTratarEstudanteSemAtividades() {
        EstudanteSemAtividadesException ex = new EstudanteSemAtividadesException("Sem atividades para submeter");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarEstudanteSemAtividades(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sem atividades para submeter", response.getBody().message());
        assertEquals(422, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar UnauthorizedException retornando 401")
    void deveTratarUnauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Credenciais inválidas");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Credenciais inválidas", response.getBody().message());
        assertEquals(401, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar Exception inesperada retornando 500 sem expor stacktrace")
    void deveTratarCatchAllSemExporStacktrace() {
        Exception ex = new NullPointerException("NullPointer interno crítico");

        ResponseEntity<ErroResponse> response = exceptionHandler.tratarCatchAll(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocorreu um erro interno inesperado no servidor.", response.getBody().message());
        assertEquals(500, response.getBody().status());
        assertNotNull(response.getBody().timestamp());
        assertFalse(response.getBody().message().contains("NullPointer"));
    }
}