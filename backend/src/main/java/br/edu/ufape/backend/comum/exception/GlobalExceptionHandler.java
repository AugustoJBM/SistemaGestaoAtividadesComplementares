package br.edu.ufape.backend.comum.exception;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.certificado.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.exception.TransicaoEstadoInvalidaException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(CertificadoInvalidoException.class)
	public ResponseEntity<ErroResponse> tratarCertificadoInvalido(CertificadoInvalidoException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.filter(Objects::nonNull).findFirst().orElse("Erro de validação nos campos da requisição.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(mensagem, HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErroResponse> tratarArquivoAusente(MissingServletRequestPartException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse("Arquivo de certificado não pode ser vazio", HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
			MultipartException.class, IllegalArgumentException.class})
	public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(Exception ex) {
		String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
				? ex.getMessage()
				: "Parâmetros da requisição inválidos ou ausentes.";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroResponse(msg, HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(NoResourceFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErroResponse("Recurso não encontrado.", HttpStatus.NOT_FOUND.value()));
	}

	@ExceptionHandler(AtividadeNaoEncontradaException.class)
	public ResponseEntity<ErroResponse> tratarAtividadeNaoEncontrada(AtividadeNaoEncontradaException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
	}

	@ExceptionHandler(AcessoNegadoAtividadeException.class)
	public ResponseEntity<ErroResponse> tratarAcessoNegadoAtividade(AcessoNegadoAtividadeException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
	}

	@ExceptionHandler(PerfilNaoPermitidoException.class)
	public ResponseEntity<ErroResponse> tratarPerfilNaoPermitido(PerfilNaoPermitidoException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
	}

	@ExceptionHandler(EmailJaCadastradoException.class)
	public ResponseEntity<ErroResponse> tratarEmailDuplicado(EmailJaCadastradoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
	}

	@ExceptionHandler(SolicitacaoEmAbertoException.class)
	public ResponseEntity<ErroResponse> tratarSolicitacaoEmAberto(SolicitacaoEmAbertoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
	}

	@ExceptionHandler(TransicaoEstadoInvalidaException.class)
	public ResponseEntity<ErroResponse> tratarTransicaoEstadoInvalida(TransicaoEstadoInvalidaException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
	}

	@ExceptionHandler(EstudanteSemAtividadesException.class)
	public ResponseEntity<ErroResponse> tratarEstudanteSemAtividades(EstudanteSemAtividadesException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
	}

	@ExceptionHandler(SolicitacaoNaoEncontradaException.class)
	public ResponseEntity<ErroResponse> tratarSolicitacaoNaoEncontrada(SolicitacaoNaoEncontradaException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErroResponse> tratarUnauthorized(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErroResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResponse> tratarCatchAll(Exception ex) {
		log.error("Erro interno não tratado no servidor", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErroResponse(
				"Ocorreu um erro interno inesperado no servidor.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
}
