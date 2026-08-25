package br.edu.ufape.backend.certificadosTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;

class ArmazenamentoCertificadoServiceTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Deve gravar arquivo com sucesso e gerar nome seguro")
    void deveArmazenarArquivoComSucesso() {
        ArmazenamentoCertificadoService service = new ArmazenamentoCertificadoService(tempDir.toString());
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "meu certificado (final).pdf", "application/pdf", "conteudo-binario".getBytes());

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
        MockMultipartFile arquivoSemNome = new MockMultipartFile("arquivo", "   ", "application/pdf", "conteudo".getBytes());

        assertThrows(CertificadoInvalidoException.class, () -> service.armazenar(arquivoSemNome));
    }
}