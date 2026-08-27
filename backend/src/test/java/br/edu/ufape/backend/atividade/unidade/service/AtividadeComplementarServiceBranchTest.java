package br.edu.ufape.backend.atividade.unidade.service;

import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.certificado.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.certificado.model.Certificado;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeComplementarServiceBranchTest {

	@TempDir
	Path tempDir;

	@Mock
	private AtividadeComplementarRepository atividadeRepository;
	@Mock
	private UsuarioContrato usuarioContrato;
	@Mock
	private ArmazenamentoCertificadoService armazenamentoCertificadoService;
	@Mock
	private AuditoriaConformidadeService auditoriaConformidadeService;
	@Mock
	private ParecerConformidadeRepository parecerConformidadeRepository;

	@InjectMocks
	private AtividadeComplementarService service;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(service, "diretorioCertificados", tempDir);
	}

	@Test
	@DisplayName("Branch: validarTipoArquivo aceita PNG, JPG, JPEG, PDF e rejeita outros")
	void deveValidarTiposDeArquivo() {
		MockMultipartFile png = new MockMultipartFile("arquivo", "c.png", "image/png", new byte[]{1});
		MockMultipartFile jpg = new MockMultipartFile("arquivo", "c.jpg", "image/jpg", new byte[]{1});
		MockMultipartFile jpeg = new MockMultipartFile("arquivo", "c.jpeg", "image/jpeg", new byte[]{1});
		MockMultipartFile pdf = new MockMultipartFile("arquivo", "c.pdf", "application/pdf", new byte[]{1});
		MockMultipartFile exe = new MockMultipartFile("arquivo", "c.exe", "application/octet-stream", new byte[]{1});
		MockMultipartFile semTipo = new MockMultipartFile("arquivo", "c.bin", null, new byte[]{1});

		assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", png));
		assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", jpg));
		assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", jpeg));
		assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", pdf));
		assertThrows(CertificadoInvalidoException.class,
				() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", exe));
		assertThrows(CertificadoInvalidoException.class,
				() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", semTipo));
		assertThrows(CertificadoInvalidoException.class,
				() -> ReflectionTestUtils.invokeMethod(service, "validarTipoArquivo", (Object) null));
	}

	@Test
	@DisplayName("Branch: removerArquivoCertificado com null e referência inválida")
	void deveTratarRemocaoCertificado() {
		assertDoesNotThrow(
				() -> ReflectionTestUtils.invokeMethod(service, "removerArquivoCertificado", (Certificado) null));
		assertDoesNotThrow(
				() -> ReflectionTestUtils.invokeMethod(service, "removerArquivoCertificado", new Certificado()));
	}

	@Test
	@DisplayName("Branch: obterArquivoCertificado valida caminhos fora do diretório permitido")
	void deveBloquearPathTraversalNoCertificado() throws IOException {
		Estudante estudante = new Estudante("Aluno", "aluno@ufape.edu.br", "hash");
		estudante.setId(1L);

		Path foraDoDiretorio = Files.createTempFile("hacker", ".pdf");
		Certificado cert = new Certificado("hacker.pdf", "application/pdf", 100L, foraDoDiretorio.toString());
		AtividadeComplementar atv = new AtividadeComplementar("T", "I", LocalDate.now(), 10, Natureza.ACC,
				Categoria.ENSINO, cert, estudante);
		atv.setId(1L);

		when(usuarioContrato.buscarPorEmail("aluno@ufape.edu.br")).thenReturn(Optional.of(estudante));
		when(atividadeRepository.findById(1L)).thenReturn(Optional.of(atv));

		assertThrows(AtividadeNaoEncontradaException.class,
				() -> service.obterArquivoCertificado(1L, "aluno@ufape.edu.br"));
	}
}
