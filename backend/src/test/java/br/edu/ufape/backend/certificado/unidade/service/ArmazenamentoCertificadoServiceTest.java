package br.edu.ufape.backend.certificado.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import br.edu.ufape.backend.certificado.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.certificado.model.Certificado;
import br.edu.ufape.backend.certificado.service.ArmazenamentoCertificadoService;

class ArmazenamentoCertificadoServiceTest {

	@TempDir
	Path tempDir;

	@Test
	@DisplayName("Deve gravar arquivo com sucesso e gerar nome seguro")
	void deveArmazenarArquivoComSucesso() {
		ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "meu certificado (final).pdf", "application/pdf",
				"conteudo-binario".getBytes());

		Certificado certificado = service.armazenar(arquivo);

		assertNotNull(certificado);
		assertEquals("meu certificado (final).pdf", certificado.getNomeArquivo());
		assertEquals("application/pdf", certificado.getTipoConteudo());
		assertTrue(Files.exists(Path.of(certificado.getReferencia())));
	}

	@Test
	@DisplayName("Deve lançar CertificadoInvalidoException quando o arquivo for nulo ou vazio")
	void deveLancarExcecaoParaArquivoVazio() {
		ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
		MockMultipartFile arquivoVazio = new MockMultipartFile("arquivo", "vazio.pdf", "application/pdf", new byte[0]);

		assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(null));
		assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(arquivoVazio));
	}

	@Test
	@DisplayName("Deve lançar CertificadoInvalidoException quando o nome original for nulo ou em branco")
	void deveLancarExcecaoParaNomeInvalido() {
		ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
		MockMultipartFile arquivoSemNome = new MockMultipartFile("arquivo", "   ", "application/pdf",
				"conteudo".getBytes());

		assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(arquivoSemNome));
	}

    @Test
    @DisplayName("Deve lançar CertificadoInvalidoException para tipos de arquivo não permitidos")
    void deveRejeitarTipoInvalido() {
        ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
        MockMultipartFile arquivoExe = new MockMultipartFile("arquivo", "script.exe", "application/x-msdownload", "malicioso".getBytes());

        assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(arquivoExe));
    }

    @Test
    @DisplayName("Deve lançar CertificadoInvalidoException quando o arquivo exceder o tamanho limite de 5MB")
    void deveRejeitarArquivoAcimaDoLimite() {
        ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
        byte[] conteudoGrande = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile arquivoGrande = new MockMultipartFile("arquivo", "grande.pdf", "application/pdf", conteudoGrande);

        assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(arquivoGrande));
    }

    @Test
    @DisplayName("Deve sanitizar nomes de arquivos com tentativas de path traversal")
    void deveSanitizarNomeComPathTraversal() {
        ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
        MockMultipartFile arquivoMalicioso = new MockMultipartFile("arquivo", "../../etc/passwd", "application/pdf", "conteudo".getBytes());

        Certificado certificado = service.armazenar(arquivoMalicioso);

        assertNotNull(certificado);
        assertTrue(Path.of(certificado.getReferencia()).startsWith(tempDir));
    }
}
