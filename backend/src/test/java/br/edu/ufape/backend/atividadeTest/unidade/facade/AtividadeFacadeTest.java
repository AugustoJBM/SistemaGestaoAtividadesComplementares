package br.edu.ufape.backend.atividadeTest.unidade.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.ProgressoService;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class AtividadeFacadeTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private ProgressoService progressoService;

    @Mock
    private AtividadeComplementarService atividadeComplementarService;

    @InjectMocks
    private AtividadeFacade facade;

    @Test
    @DisplayName("obterProgresso delega para ProgressoService com o email exato")
    void obterProgressoDelegaParaProgressoService() {
        ProgressoResponseDTO respostaEsperada = mock(ProgressoResponseDTO.class);
        when(progressoService.obterProgresso(EMAIL)).thenReturn(respostaEsperada);

        ProgressoResponseDTO resposta = facade.obterProgresso(EMAIL);

        assertSame(respostaEsperada, resposta);
        verify(progressoService).obterProgresso(EMAIL);
        verifyNoMoreInteractions(progressoService, atividadeComplementarService);
    }

    @Test
    @DisplayName("listarAtividadesDoEstudante delega para AtividadeComplementarService com os argumentos exatos")
    void listarAtividadesDoEstudanteDelegaParaAtividadeComplementarService() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA,
                null,
                estudante);
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, Categoria.PESQUISA))
                .thenReturn(List.of(atividade));

        List<AtividadeResponseDTO> resultado = facade.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, Categoria.PESQUISA);

        assertEquals(1, resultado.size());
        assertEquals(atividade.getTitulo(), resultado.get(0).titulo());
        verify(atividadeComplementarService)
                .listarAtividadesDoEstudante(eq(EMAIL), eq(Natureza.ACC), eq(Categoria.PESQUISA));
        verifyNoMoreInteractions(progressoService, atividadeComplementarService);
    }

    @Test
    @DisplayName("cadastrarAtividade delega para AtividadeComplementarService com os argumentos exatos")
    void cadastrarAtividadeDelegaParaAtividadeComplementarService() {
        CadastroAtividadeRequestDTO request = new CadastroAtividadeRequestDTO(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA);
        MultipartFile arquivo = new MockMultipartFile(
                "arquivo", "certificado.pdf", "application/pdf", "conteudo".getBytes());
        AtividadeResponseDTO respostaEsperada = mock(AtividadeResponseDTO.class);
        when(atividadeComplementarService.cadastrarAtividade(request, arquivo, EMAIL))
                .thenReturn(respostaEsperada);

        AtividadeResponseDTO resposta = facade.cadastrarAtividade(request, arquivo, EMAIL);

        assertSame(respostaEsperada, resposta);
        verify(atividadeComplementarService).cadastrarAtividade(eq(request), eq(arquivo), eq(EMAIL));
        verifyNoMoreInteractions(progressoService, atividadeComplementarService);
    }
}
