package br.edu.ufape.backend.atividadeTest.unidade.contrato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.contrato.AtividadeContratoImpl;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class AtividadeContratoImplTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private AtividadeComplementarService atividadeComplementarService;

    private AtividadeContratoImpl atividadeContrato;

    @BeforeEach
    void setUp() {
        atividadeContrato = new AtividadeContratoImpl(atividadeComplementarService);
    }

    @Test
    @DisplayName("Deve delegar busca de atividades por estudante para o service")
    void deveDelegarBuscaPorEstudante() {
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, null, null))
                .thenReturn(List.of());

        List<AtividadeResponseDTO> resultado = atividadeContrato.buscarPorEstudante(EMAIL);

        assertTrue(resultado.isEmpty());
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, null, null);
    }

    @Test
    @DisplayName("Deve delegar busca de atividades por estudante e natureza para o service")
    void deveDelegarBuscaPorEstudanteENatureza() {
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, null))
                .thenReturn(List.of());

        List<AtividadeResponseDTO> resultado = atividadeContrato.buscarPorEstudanteENatureza(EMAIL, Natureza.ACC);

        assertTrue(resultado.isEmpty());
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, Natureza.ACC, null);
    }

    @Test
    @DisplayName("Deve propagar acesso negado ao buscar atividades por estudante")
    void devePropagarAcessoNegadoAoBuscarPorEstudante() {
        AcessoNegadoAtividadeException excecao =
                new AcessoNegadoAtividadeException("Estudante não encontrado");
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, null, null))
                .thenThrow(excecao);

        AcessoNegadoAtividadeException resultado = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> atividadeContrato.buscarPorEstudante(EMAIL));

        assertEquals(excecao, resultado);
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, null, null);
    }

    @Test
    @DisplayName("Deve propagar acesso negado ao buscar por estudante e natureza")
    void devePropagarAcessoNegadoAoBuscarPorEstudanteENatureza() {
        AcessoNegadoAtividadeException excecao =
                new AcessoNegadoAtividadeException("Estudante não encontrado");
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, Natureza.ACC, null))
                .thenThrow(excecao);

        AcessoNegadoAtividadeException resultado = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> atividadeContrato.buscarPorEstudanteENatureza(EMAIL, Natureza.ACC));

        assertEquals(excecao, resultado);
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, Natureza.ACC, null);
    }

    @Test
    @DisplayName("Deve mapear entidades retornadas pelo service para DTOs públicos")
    void deveMapearEntidadesParaDtosPublicos() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.of(2026, 8, 19),
                10,
                Natureza.ACC,
                Categoria.PESQUISA,
                null,
                estudante);
        when(atividadeComplementarService.listarAtividadesDoEstudante(EMAIL, null, null))
                .thenReturn(List.of(atividade));

        List<AtividadeResponseDTO> resultado = atividadeContrato.buscarPorEstudante(EMAIL);

        assertEquals(1, resultado.size());
        assertEquals(atividade.getTitulo(), resultado.get(0).titulo());
        assertEquals(atividade.getInstituicaoResponsavel(), resultado.get(0).instituicaoResponsavel());
        assertEquals(atividade.getDataRealizacao(), resultado.get(0).dataRealizacao());
        assertEquals(atividade.getCargaHorariaEmHoras(), resultado.get(0).cargaHorariaEmHoras());
        assertEquals(atividade.getNatureza(), resultado.get(0).natureza());
        assertEquals(atividade.getCategoria(), resultado.get(0).categoria());
        assertEquals(EMAIL, resultado.get(0).estudanteEmail());
        verify(atividadeComplementarService).listarAtividadesDoEstudante(EMAIL, null, null);
    }
}
