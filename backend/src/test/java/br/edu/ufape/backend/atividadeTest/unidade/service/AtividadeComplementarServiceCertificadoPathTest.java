package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;


@ExtendWith(MockitoExtension.class)
class AtividadeComplementarServiceCertificadoPathTest {

    private static final String EMAIL = "estudante@ufape.edu.br";
    private static final Long ID_ATIVIDADE = 1L;

    @TempDir
    Path diretorioCertificados;

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

    private AtividadeComplementarService service;
    private Estudante estudante;

    @BeforeEach
    void setUp() {
        service = new AtividadeComplementarService(
                atividadeRepository,
                usuarioContrato,
                armazenamentoCertificadoService,
                auditoriaConformidadeService,
                parecerConformidadeRepository,
                diretorioCertificados.toString());

        estudante = new Estudante("Estudante", EMAIL, "hash");
        estudante.setId(1L);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
    }

    private AtividadeComplementar criarAtividadeComCertificado(String referencia) {
        Certificado certificado = new Certificado("certificado.pdf", "application/pdf", 100L, referencia);
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA,
                certificado,
                estudante);
        atividade.setId(ID_ATIVIDADE);
        return atividade;
    }

    @Test
    @DisplayName("Deve baixar certificado legitimo armazenado dentro do diretorio configurado")
    void deveBaixarCertificadoLegitimoDentroDoDiretorio() throws IOException {
        Path arquivoLegitimo = diretorioCertificados.resolve("legitimo.pdf");
        Files.writeString(arquivoLegitimo, "PDF-CONTENT");

        AtividadeComplementar atividade = criarAtividadeComCertificado(arquivoLegitimo.toString());
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        Resource resource = service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL);

        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("PDF-CONTENT", new String(resource.getInputStream().readAllBytes()));
    }

    @Test
    @DisplayName("Deve recusar referencia de certificado que escapa do diretorio configurado (path traversal)")
    void deveRecusarReferenciaComTraversalForaDoDiretorio() throws IOException {
        Path arquivoForaDoDiretorio = Files.writeString(
                diretorioCertificados.resolveSibling("fora-do-diretorio-" + System.nanoTime() + ".txt"),
                "segredo");
        String referenciaTraversal = diretorioCertificados
                .resolve("../" + arquivoForaDoDiretorio.getFileName())
                .toString();

        AtividadeComplementar atividade = criarAtividadeComCertificado(referenciaTraversal);
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        assertThrows(
                AtividadeNaoEncontradaException.class,
                () -> service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL));
    }
}
