package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class AtividadeComplementarServiceTest {

    private static final String EMAIL = "estudante@ufape.edu.br";
    private static final Long ID_ATIVIDADE = 1L;

    @TempDir
    Path tempDir;

    @Mock
    private UsuarioContrato usuarioContrato;

    @Mock
    private AtividadeComplementarRepository atividadeRepository;

    @Mock
    private ArmazenamentoCertificadoService armazenamentoCertificadoService;

    @InjectMocks
    private AtividadeComplementarService service;

    private AtividadeComplementar criarAtividade(Natureza natureza, Categoria categoria, Estudante estudante) {
        return new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                natureza,
                categoria,
                null,
                estudante);
    }

    private AtividadeComplementar criarAtividadeComCertificado(Estudante estudante, String referencia) {
        Certificado certificado = new Certificado("certificado.pdf", "application/pdf", 100L, referencia);
        return new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA,
                certificado,
                estudante);
    }

    @Test
    @DisplayName("Estudante sem atividades retorna lista vazia")
    void estudanteSemAtividadesRetornaListaVazia() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, null, null)).thenReturn(List.of());

        List<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(EMAIL, null, null);

        assertTrue(resultado.isEmpty());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, null, null);
    }

    @Test
    @DisplayName("Estudante com atividades retorna apenas as atividades dele")
    void estudanteComAtividadesRetornaApenasAtividadesDele() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividade1 = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        AtividadeComplementar atividade2 = criarAtividade(Natureza.ACEX, Categoria.EXTENSAO, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, null, null))
                .thenReturn(List.of(atividade1, atividade2));

        List<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(EMAIL, null, null);

        assertEquals(2, resultado.size());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, null, null);
    }

    @Test
    @DisplayName("Filtro apenas por Natureza funciona corretamente")
    void filtroApenasPorNaturezaFuncionaCorretamente() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividadeAcc = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, Natureza.ACC, null))
                .thenReturn(List.of(atividadeAcc));

        List<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, null);

        assertEquals(1, resultado.size());
        assertEquals(Natureza.ACC, resultado.get(0).getNatureza());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, Natureza.ACC, null);
    }

    @Test
    @DisplayName("Filtro por Natureza e Categoria funciona corretamente")
    void filtroPorNaturezaECategoriaFuncionaCorretamente() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividade = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, Natureza.ACC, Categoria.PESQUISA))
                .thenReturn(List.of(atividade));

        List<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(
                EMAIL, Natureza.ACC, Categoria.PESQUISA);

        assertEquals(1, resultado.size());
        assertEquals(Natureza.ACC, resultado.get(0).getNatureza());
        assertEquals(Categoria.PESQUISA, resultado.get(0).getCategoria());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, Natureza.ACC, Categoria.PESQUISA);
    }

    @Test
    @DisplayName("Usuario avaliador lanca AcessoNegadoAtividadeException")
    void usuarioAvaliadorLancaAcessoNegadoAtividadeException() {
        Avaliador avaliador = new Avaliador("Avaliador", EMAIL, "hash", "REG-1", "Extensao");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(avaliador));

        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.listarAtividadesDoEstudante(EMAIL, null, null));
    }

    @Test
    @DisplayName("E-mail inexistente lanca AcessoNegadoAtividadeException")
    void emailInexistenteLancaAcessoNegadoAtividadeException() {
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.listarAtividadesDoEstudante(EMAIL, null, null));
    }

    @Test
    @DisplayName("Deve excluir atividade do proprio estudante removendo entidade e arquivo")
    void deveExcluirAtividadeDoProprioEstudanteRemovendoEntidadeEArquivo() throws IOException {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Path arquivoCertificado = tempDir.resolve("certificado.pdf");
        Files.createFile(arquivoCertificado);

        AtividadeComplementar atividade = criarAtividadeComCertificado(estudante, arquivoCertificado.toString());

        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByIdAndEstudante(ID_ATIVIDADE, estudante)).thenReturn(Optional.of(atividade));

        service.excluirAtividade(ID_ATIVIDADE, EMAIL);

        assertTrue(Files.notExists(arquivoCertificado), "O arquivo do certificado deveria ter sido removido");
        verify(atividadeRepository).delete(atividade);
    }

    @Test
    @DisplayName("Exclusao de atividade que nao pertence ao estudante lanca acesso negado")
    void exclusaoDeAtividadeDeOutroEstudanteLancaAcessoNegado() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByIdAndEstudante(ID_ATIVIDADE, estudante)).thenReturn(Optional.empty());

        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.excluirAtividade(ID_ATIVIDADE, EMAIL));
        verify(atividadeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Exclusao com id inexistente lanca erro apropriado")
    void exclusaoComIdInexistenteLancaErroApropriado() {
        // mesmo cenario de "nao encontrado" que "pertence a outro estudante":
        // findByIdAndEstudante nao distingue os dois casos de proposito (evita
        // enumeracao de recursos). Ver comentario em AtividadeComplementarService.
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Long idInexistente = 9999L;
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByIdAndEstudante(idInexistente, estudante)).thenReturn(Optional.empty());

        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.excluirAtividade(idInexistente, EMAIL));
        verify(atividadeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Falha ao remover arquivo do certificado nao deixa o banco inconsistente")
    void falhaAoRemoverArquivoNaoDeixaBancoInconsistente() throws IOException {
        // Files.deleteIfExists lanca DirectoryNotEmptyException (subtipo de
        // IOException) ao tentar apagar um diretorio nao vazio, simulando uma
        // falha real de remocao sem depender de permissoes de SO.
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Path diretorioNaoVazio = tempDir.resolve("certificados-com-falha");
        Files.createDirectory(diretorioNaoVazio);
        Files.createFile(diretorioNaoVazio.resolve("arquivo-interno.txt"));

        AtividadeComplementar atividade = criarAtividadeComCertificado(estudante, diretorioNaoVazio.toString());

        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByIdAndEstudante(ID_ATIVIDADE, estudante)).thenReturn(Optional.of(atividade));

        assertThrows(
                RuntimeException.class,
                () -> service.excluirAtividade(ID_ATIVIDADE, EMAIL));
        verify(atividadeRepository, never()).delete(any());
        assertFalse(Files.notExists(diretorioNaoVazio), "O diretorio nao deveria ter sido removido");
    }

    @Test
    @DisplayName("Falha ao salvar atividade remove o certificado gravado em disco (rollback manual)")
    void falhaAoSalvarAtividadeRemoveCertificadoGravadoEmDisco() throws IOException {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Path arquivoCertificado = tempDir.resolve("certificado.pdf");
        Files.createFile(arquivoCertificado);
        Certificado certificado = new Certificado(
                "certificado.pdf", "application/pdf", 100L, arquivoCertificado.toString());

        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "certificado.pdf", "application/pdf", "conteudo".getBytes());
        CadastroAtividadeRequest request = new CadastroAtividadeRequest(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA);

        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(armazenamentoCertificadoService.armazenar(arquivo)).thenReturn(certificado);
        when(atividadeRepository.save(any())).thenThrow(new RuntimeException("Falha ao persistir atividade"));

        assertThrows(
                RuntimeException.class,
                () -> service.cadastrarAtividade(request, arquivo, EMAIL));

        assertTrue(Files.notExists(arquivoCertificado),
                "O certificado gravado em disco deveria ter sido removido apos falha no cadastro");
        verify(armazenamentoCertificadoService).armazenar(arquivo);
    }
}