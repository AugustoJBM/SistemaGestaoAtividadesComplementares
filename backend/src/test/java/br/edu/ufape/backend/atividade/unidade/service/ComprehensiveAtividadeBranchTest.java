package br.edu.ufape.backend.atividade.unidade.service;

import br.edu.ufape.backend.atividade.controller.AtividadeController;
import br.edu.ufape.backend.atividade.controller.AtividadeProgressoController;
import br.edu.ufape.backend.atividade.dto.AtualizarAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.certificado.controller.CertificadoController;
import br.edu.ufape.backend.certificado.facade.CertificadoFacade;
import br.edu.ufape.backend.certificado.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprehensiveAtividadeBranchTest {

	@TempDir
	Path tempDir;

	@Mock
	private AtividadeComplementarRepository atividadeRepo;
	@Mock
	private UsuarioContrato usuarioContrato;
	@Mock
	private ArmazenamentoCertificadoService armazenamentoService;
	@Mock
	private AuditoriaConformidadeService auditoriaService;
	@Mock
	private ParecerConformidadeRepository parecerRepo;
	@Mock
	private Authentication authentication;

	@InjectMocks
	private AtividadeComplementarService atividadeService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(atividadeService, "diretorioCertificados", tempDir);
	}

	// --- ATIVIDADE REPOSITORY DEFAULT METHOD BRANCHES ---
	@Test
	@DisplayName("AtividadeRepository: findByEstudanteComFiltros cobre todas as 4 combinações de branch")
	void deveCobrirBranchesRepositoryFiltros() {
		Estudante est = new Estudante();
		AtividadeComplementarRepository repo = mock(AtividadeComplementarRepository.class);
		doCallRealMethod().when(repo).findByEstudanteComFiltros(any(), any(), any());

		repo.findByEstudanteComFiltros(est, Natureza.ACC, Categoria.ENSINO);
		verify(repo).findByEstudanteAndNaturezaAndCategoria(est, Natureza.ACC, Categoria.ENSINO);

		repo.findByEstudanteComFiltros(est, Natureza.ACC, null);
		verify(repo).findByEstudanteAndNatureza(est, Natureza.ACC);

		repo.findByEstudanteComFiltros(est, null, Categoria.PESQUISA);
		verify(repo).findByEstudanteAndCategoria(est, Categoria.PESQUISA);

		repo.findByEstudanteComFiltros(est, null, null);
		verify(repo).findByEstudante(est);
	}

	// --- ATIVIDADE CONTROLLER (AUTHENTICATION NULL & VALIDATION HANDLER) ---
	@Test
	@DisplayName("AtividadeController: Endpoints retornam 401 quando authentication for nula")
	void deveRetornar401ParaAuthNulaEmAtividadeController() {
		AtividadeFacade facade = mock(AtividadeFacade.class);
		AtividadeController controller = new AtividadeController(facade);

		assertEquals(HttpStatus.UNAUTHORIZED,
				controller.cadastrar(
						new CadastroAtividadeRequestDTO("T", "I", LocalDate.now(), 10, Natureza.ACC, Categoria.ENSINO),
						null, null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED, controller.listar(null, null, null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED,
				controller.atualizar(1L,
						new AtualizarAtividadeRequestDTO("T", "I", LocalDate.now(), 10, Natureza.ACC, Categoria.ENSINO),
						null, null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED, controller.excluir(1L, null).getStatusCode());
	}

	@Test
	@DisplayName("AtividadeController: tratarFalhaDeValidacao extrai mensagem de erro de campo")
	void deveTratarFalhaValidacaoBindException() {
		AtividadeController controller = new AtividadeController(mock(AtividadeFacade.class));
		BindException be = new BindException(new Object(), "objeto");
		be.addError(new FieldError("objeto", "titulo", "Titulo obrigatorio"));

		ResponseEntity<Map<String, String>> res = controller.tratarFalhaDeValidacao(be);
		assertEquals("titulo: Titulo obrigatorio", res.getBody().get("message"));
	}

	@Test
	@DisplayName("AtividadeProgressoController: authentication nula retorna 401")
	void deveRetornar401AuthNulaProgresso() {
		AtividadeProgressoController c = new AtividadeProgressoController(mock(AtividadeFacade.class));
		assertEquals(HttpStatus.UNAUTHORIZED, c.progresso(null).getStatusCode());
	}

	// --- CERTIFICADO CONTROLLER ---
	@Test
	@DisplayName("CertificadoController: authentication nula retorna 401")
	void deveRetornar401AuthNulaCertificadoController() {
		CertificadoController c = new CertificadoController(mock(CertificadoFacade.class));
		assertEquals(HttpStatus.UNAUTHORIZED, c.obterCertificado(1L, null).getStatusCode());
	}

	// --- ARMAZENAMENTO CERTIFICADO SERVICE IOEXCEPTION ---
	@Test
	@DisplayName("ArmazenamentoCertificadoService: Falha de escrita no disco lança RuntimeException")
	void deveLancarRuntimeExceptionEmFalhaGravacao() {
		ArmazenamentoCertificadoService serv = new ArmazenamentoCertificadoService(tempDir.toString());
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "cert.pdf", "application/pdf", new byte[]{1}) {
			@Override
			public java.io.InputStream getInputStream() throws java.io.IOException {
				throw new java.io.IOException("Falha de IO forcada");
			}
		};
		assertThrows(RuntimeException.class, () -> serv.armazenar(arquivo));
	}
}
