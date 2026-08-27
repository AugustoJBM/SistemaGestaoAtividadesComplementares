package br.edu.ufape.backend.relatorio;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.relatorio.dto.RelatorioAtividadesResponse;
import br.edu.ufape.backend.relatorio.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceBranchTest {

	@Mock
	private AtividadeContrato atividadeContrato;
	private RelatorioService service;

	@BeforeEach
	void setUp() {
		service = new RelatorioService(atividadeContrato);
	}

	@Test
	@DisplayName("Branch: Relatório com apenas ACEX (sem ACC) cobre o pulo de grupo vazio")
	void deveGerarRelatorioSemAcc() {
		AtividadeResponseDTO acex = new AtividadeResponseDTO(1L, "Extensao Comunitária", "UFAPE", LocalDate.now(), 40,
				Natureza.ACEX, Categoria.EXTENSAO, LocalDateTime.now(), "aluno@ufape.edu.br");
		when(atividadeContrato.buscarPorEstudante("aluno@ufape.edu.br")).thenReturn(List.of(acex));

		RelatorioAtividadesResponse res = service.gerarRelatorio("aluno@ufape.edu.br");
		assertEquals(0, res.totalHorasAcc());
		assertEquals(40, res.totalHorasAcex());
		assertEquals(40, res.totalHorasGeral());
		assertEquals(1, res.naturezas().size());
		assertEquals("ACEX", res.naturezas().get(0).natureza());
	}

	@Test
    @DisplayName("Branch: Relatório sem nenhuma atividade retorna listas vazias")
    void deveGerarRelatorioVazio() {
        when(atividadeContrato.buscarPorEstudante("aluno@ufape.edu.br")).thenReturn(List.of());

        RelatorioAtividadesResponse res = service.gerarRelatorio("aluno@ufape.edu.br");
        assertEquals(0, res.totalHorasGeral());
        assertTrue(res.naturezas().isEmpty());
    }
}
