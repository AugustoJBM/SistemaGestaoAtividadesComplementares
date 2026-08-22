package br.edu.ufape.backend.relatorioTest.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.relatorio.dto.GrupoNaturezaResponse;
import br.edu.ufape.backend.relatorio.dto.RelatorioAtividadesResponse;
import br.edu.ufape.backend.relatorio.service.RelatorioService;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private AtividadeContrato atividadeContrato;

    private RelatorioService relatorioService;

    @BeforeEach
    void setUp() {
        relatorioService = new RelatorioService(atividadeContrato);
    }

    private AtividadeResponseDTO atividade(Long id, Natureza natureza, Categoria categoria, int horas) {
        return new AtividadeResponseDTO(
                id,
                "Atividade " + id,
                "UFAPE",
                LocalDate.of(2026, 3, 10),
                horas,
                natureza,
                categoria,
                LocalDateTime.of(2026, 3, 11, 9, 0),
                EMAIL);
    }

    @Test
    @DisplayName("Estudante sem atividades gera relatorio vazio com totais zerados")
    void deveGerarRelatorioVazio() {
        when(atividadeContrato.buscarPorEstudante(EMAIL)).thenReturn(List.of());

        RelatorioAtividadesResponse relatorio = relatorioService.gerarRelatorio(EMAIL);

        assertTrue(relatorio.naturezas().isEmpty());
        assertEquals(0, relatorio.totalHorasAcc());
        assertEquals(0, relatorio.totalHorasAcex());
        assertEquals(0, relatorio.totalHorasGeral());
        assertEquals(EMAIL, relatorio.estudanteEmail());
    }

    @Test
    @DisplayName("Estudante somente com ACC acumula apenas o total de ACC")
    void deveConsolidarSomenteAcc() {
        when(atividadeContrato.buscarPorEstudante(EMAIL)).thenReturn(List.of(
                atividade(1L, Natureza.ACC, Categoria.PESQUISA, 10),
                atividade(2L, Natureza.ACC, Categoria.PESQUISA, 5)));

        RelatorioAtividadesResponse relatorio = relatorioService.gerarRelatorio(EMAIL);

        assertEquals(1, relatorio.naturezas().size());
        assertEquals("ACC", relatorio.naturezas().get(0).natureza());
        assertEquals(15, relatorio.naturezas().get(0).totalHoras());
        assertEquals(15, relatorio.totalHorasAcc());
        assertEquals(0, relatorio.totalHorasAcex());
        assertEquals(15, relatorio.totalHorasGeral());
    }

    @Test
    @DisplayName("Mistura de ACC e ACEX e agrupada com ACC primeiro e totais separados")
    void deveAgruparAccEAcex() {
        when(atividadeContrato.buscarPorEstudante(EMAIL)).thenReturn(List.of(
                atividade(1L, Natureza.ACEX, Categoria.EXTENSAO, 8),
                atividade(2L, Natureza.ACC, Categoria.ENSINO, 12)));

        RelatorioAtividadesResponse relatorio = relatorioService.gerarRelatorio(EMAIL);

        assertEquals(List.of("ACC", "ACEX"),
                relatorio.naturezas().stream().map(GrupoNaturezaResponse::natureza).toList());
        assertEquals(12, relatorio.totalHorasAcc());
        assertEquals(8, relatorio.totalHorasAcex());
        assertEquals(20, relatorio.totalHorasGeral());
    }

    @Test
    @DisplayName("Multiplas categorias na mesma natureza sao ordenadas e somadas por categoria")
    void deveAgruparMultiplasCategoriasDaMesmaNatureza() {
        when(atividadeContrato.buscarPorEstudante(EMAIL)).thenReturn(List.of(
                atividade(1L, Natureza.ACC, Categoria.PESQUISA, 4),
                atividade(2L, Natureza.ACC, Categoria.ENSINO, 6),
                atividade(3L, Natureza.ACC, Categoria.ENSINO, 3)));

        GrupoNaturezaResponse acc = relatorioService.gerarRelatorio(EMAIL).naturezas().get(0);

        assertEquals(List.of("ENSINO", "PESQUISA"),
                acc.categorias().stream().map(c -> c.categoria()).toList());
        assertEquals(9, acc.categorias().get(0).totalHoras());
        assertEquals(2, acc.categorias().get(0).atividades().size());
        assertEquals(4, acc.categorias().get(1).totalHoras());
        assertEquals(13, acc.totalHoras());
    }

    @Test
    @DisplayName("Cada atividade carrega sua carga horaria e o total confere com a soma dos itens")
    void deveExporCargaHorariaDeCadaAtividade() {
        when(atividadeContrato.buscarPorEstudante(EMAIL)).thenReturn(List.of(
                atividade(1L, Natureza.ACC, Categoria.PESQUISA, 7),
                atividade(2L, Natureza.ACEX, Categoria.EXTENSAO, 9)));

        RelatorioAtividadesResponse relatorio = relatorioService.gerarRelatorio(EMAIL);

        int somaDosItens = relatorio.naturezas().stream()
                .flatMap(natureza -> natureza.categorias().stream())
                .flatMap(categoria -> categoria.atividades().stream())
                .mapToInt(item -> item.cargaHorariaEmHoras())
                .sum();

        assertEquals(relatorio.totalHorasGeral(), somaDosItens);
        assertEquals(16, somaDosItens);
    }
}
