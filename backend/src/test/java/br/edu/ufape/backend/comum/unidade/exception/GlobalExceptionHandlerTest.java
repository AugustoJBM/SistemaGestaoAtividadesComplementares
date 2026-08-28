package br.edu.ufape.backend.comum.unidade.exception;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.certificado.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.comum.exception.ErroResponse;
import br.edu.ufape.backend.comum.exception.GlobalExceptionHandler;
import br.edu.ufape.backend.notificacao.exception.NotificacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.exception.TransicaoEstadoInvalidaException;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	@DisplayName("Branch: Todos os tratamentos de exceções mapeiam os status HTTP corretos")
	void deveCobrirTodosOsHandlers() {
		assertEquals(HttpStatus.BAD_REQUEST, handler
				.tratarCertificadoInvalido(new CertificadoInvalidoException("Certificado inválido")).getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST,
				handler.tratarArquivoAusente(new MissingServletRequestPartException("arquivo")).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND,
				handler.tratarRecursoNaoEncontrado(mock(NoResourceFoundException.class)).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND,
				handler.tratarAtividadeNaoEncontrada(new AtividadeNaoEncontradaException("Atividade não encontrada"))
						.getStatusCode());
		assertEquals(HttpStatus.FORBIDDEN, handler
				.tratarAcessoNegadoAtividade(new AcessoNegadoAtividadeException("Acesso negado")).getStatusCode());
		assertEquals(HttpStatus.FORBIDDEN,
				handler.tratarPerfilNaoPermitido(new PerfilNaoPermitidoException()).getStatusCode());
		assertEquals(HttpStatus.CONFLICT,
				handler.tratarEmailDuplicado(new EmailJaCadastradoException("email@teste.com")).getStatusCode());
		assertEquals(HttpStatus.CONFLICT,
				handler.tratarSolicitacaoEmAberto(new SolicitacaoEmAbertoException()).getStatusCode());
		assertEquals(HttpStatus.CONFLICT,
				handler.tratarTransicaoEstadoInvalida(
						new TransicaoEstadoInvalidaException(StatusSolicitacao.SUBMETIDA, StatusSolicitacao.SUBMETIDA))
						.getStatusCode());
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
				handler.tratarEstudanteSemAtividades(new EstudanteSemAtividadesException()).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND,
				handler.tratarSolicitacaoNaoEncontrada(new SolicitacaoNaoEncontradaException()).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND,
				handler.tratarNotificacaoNaoEncontrada(new NotificacaoNaoEncontradaException()).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED,
				handler.tratarUnauthorized(new UnauthorizedException("Não autorizado")).getStatusCode());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, handler.tratarCatchAll(new Exception("erro")).getStatusCode());
	}

	@Test
	@DisplayName("Branch: tratarRequisicaoInvalida com mensagem presente e mensagem em branco")
	void deveTratarRequisicaoInvalida() {
		ResponseEntity<ErroResponse> r1 = handler
				.tratarRequisicaoInvalida(new IllegalArgumentException("Mensagem especifica"));
		assertEquals("Mensagem especifica", r1.getBody().message());

		ResponseEntity<ErroResponse> r2 = handler.tratarRequisicaoInvalida(new IllegalArgumentException("   "));
		assertEquals("Parâmetros da requisição inválidos ou ausentes.", r2.getBody().message());
	}

	@Test
	@DisplayName("Branch: tratarValidacao com e sem mensagens de campo")
	void deveTratarValidacao() {
		MethodArgumentNotValidException exComMsg = mock(MethodArgumentNotValidException.class);
		BindingResult brComMsg = mock(BindingResult.class);
		when(brComMsg.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "campo", "Campo obrigatorio")));
		when(exComMsg.getBindingResult()).thenReturn(brComMsg);

		ResponseEntity<ErroResponse> r1 = handler.tratarValidacao(exComMsg);
		assertEquals("Campo obrigatorio", r1.getBody().message());

		MethodArgumentNotValidException exSemMsg = mock(MethodArgumentNotValidException.class);
		BindingResult brSemMsg = mock(BindingResult.class);
		when(brSemMsg.getFieldErrors()).thenReturn(List.of());
		when(exSemMsg.getBindingResult()).thenReturn(brSemMsg);

		ResponseEntity<ErroResponse> r2 = handler.tratarValidacao(exSemMsg);
		assertEquals("Erro de validação nos campos da requisição.", r2.getBody().message());
	}
}
