package br.edu.ufape.backend.atividadeTest.unidade.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
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
    private AtividadeFacade atividadeFacade;

    @Test
    @DisplayName("Mapeia Page<AtividadeComplementar> para Page<AtividadeResponse> incluindo o estudanteEmail")
    void listarAtividadesDoEstudanteMapeiaEntidadeParaDtoComEstudanteEmail() {
        // Arrange
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Minicurso de Testes",
                "UFAPE",
                LocalDate.of(2025, 6, 10),
                8,
                Natureza.ACC,
                Categoria.EXTENSAO,
                null,
                estudante);
        Pageable pageable = PageRequest.of(0, 20);
        Page<AtividadeComplementar> pageEntidade = new PageImpl<>(List.of(atividade), pageable, 1);

        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, null, null, pageable))
                .thenReturn(pageEntidade);

        // Act
        Page<AtividadeResponse> resultado = atividadeFacade.listarAtividadesDoEstudante(EMAIL, null, null, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        AtividadeResponse dto = resultado.getContent().get(0);
        assertEquals(atividade.getTitulo(), dto.titulo());
        assertEquals(atividade.getNatureza(), dto.natureza());
        assertEquals(atividade.getCategoria(), dto.categoria());
        assertEquals(EMAIL, dto.estudanteEmail());
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, null, null, pageable);
    }

    @Test
    @DisplayName("Retorna Page vazia quando o service nao encontra atividades")
    void listarAtividadesDoEstudanteRetornaPageVaziaQuandoServiceNaoEncontraAtividades() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, Categoria.PESQUISA, pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // Act
        Page<AtividadeResponse> resultado = atividadeFacade.listarAtividadesDoEstudante(
                EMAIL, Natureza.ACC, Categoria.PESQUISA, pageable);

        // Assert
        assertEquals(0, resultado.getTotalElements());
        assertEquals(true, resultado.getContent().isEmpty());
    }
}
