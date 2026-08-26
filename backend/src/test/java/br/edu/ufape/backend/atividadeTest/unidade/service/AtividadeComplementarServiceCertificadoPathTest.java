package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assumptions;
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

    private void criarSymlink(Path link, Path alvo) {
        try {
            Files.createSymbolicLink(link, alvo);
        } catch (IOException | UnsupportedOperationException e) {
            Assumptions.abort("Ambiente nao suporta criacao de symlinks: " + e.getClass().getSimpleName());
        }
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

    @Test
    @DisplayName("Deve recusar symlink dentro do diretorio que aponta para arquivo fora da raiz")
    void deveRecusarSymlinkApontandoParaForaDaRaiz() throws IOException {
        Path arquivoExterno = Files.writeString(
                diretorioCertificados.resolveSibling("externo-" + System.nanoTime() + ".pdf"),
                "SEGREDO");
        Path link = diretorioCertificados.resolve("link.pdf");
        criarSymlink(link, arquivoExterno);

        AtividadeComplementar atividade = criarAtividadeComCertificado(link.toString());
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        AtividadeNaoEncontradaException excecao = assertThrows(
                AtividadeNaoEncontradaException.class,
                () -> service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL));

        assertEquals("Arquivo físico do certificado não encontrado no servidor.", excecao.getMessage());
        assertFalse(excecao.getMessage().contains(arquivoExterno.toString()));
    }

    @Test
    @DisplayName("Deve baixar certificado quando o symlink aponta para arquivo dentro da propria raiz")
    void devePermitirSymlinkDentroDaRaiz() throws IOException {
        Path alvo = diretorioCertificados.resolve("alvo.pdf");
        Files.writeString(alvo, "PDF-INTERNO");
        Path link = diretorioCertificados.resolve("interno-link.pdf");
        criarSymlink(link, alvo);

        AtividadeComplementar atividade = criarAtividadeComCertificado(link.toString());
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        Resource resource = service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL);

        assertTrue(resource.exists());
        assertEquals("PDF-INTERNO", new String(resource.getInputStream().readAllBytes()));
    }

    @Test
    @DisplayName("Deve tratar arquivo inexistente dentro da raiz como nao encontrado sem vazar detalhes do filesystem")
    void deveTratarArquivoInexistenteComoNaoEncontrado() {
        Path inexistente = diretorioCertificados.resolve("nao-existe.pdf");

        AtividadeComplementar atividade = criarAtividadeComCertificado(inexistente.toString());
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        AtividadeNaoEncontradaException excecao = assertThrows(
                AtividadeNaoEncontradaException.class,
                () -> service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL));

        assertEquals("Arquivo físico do certificado não encontrado no servidor.", excecao.getMessage());
        assertFalse(excecao.getMessage().contains(inexistente.toString()));
        assertNull(excecao.getCause());
    }

    @Test
    @DisplayName("Deve retornar a mesma mensagem para symlink quebrado, sem expor o alvo")
    void deveTratarSymlinkQuebradoComoNaoEncontrado() throws IOException {
        Path alvoRemovido = diretorioCertificados.resolve("alvo-removido.pdf");
        Files.writeString(alvoRemovido, "TEMP");
        Path link = diretorioCertificados.resolve("quebrado.pdf");
        criarSymlink(link, alvoRemovido);
        Files.delete(alvoRemovido);

        AtividadeComplementar atividade = criarAtividadeComCertificado(link.toString());
        when(atividadeRepository.findById(ID_ATIVIDADE)).thenReturn(Optional.of(atividade));

        AtividadeNaoEncontradaException excecao = assertThrows(
                AtividadeNaoEncontradaException.class,
                () -> service.obterArquivoCertificado(ID_ATIVIDADE, EMAIL));

        assertEquals("Arquivo físico do certificado não encontrado no servidor.", excecao.getMessage());
        assertFalse(excecao.getMessage().contains(alvoRemovido.toString()));
    }
}
