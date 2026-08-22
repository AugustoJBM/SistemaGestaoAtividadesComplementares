package br.edu.ufape.backend.atividadeTest.unidade.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponseDTO;

class ProgressoModalidadeResponseTest {

    @Test
    @DisplayName("Deve retornar 0% quando as horas exigidas forem zero (sem divisao por zero)")
    void deveRetornarZeroQuandoHorasExigidasForZero() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(50, 0, 0);
        assertEquals(0, progresso.getPercentualConcluido());
        assertEquals(50, progresso.getHorasAcumuladas());
        assertEquals(0, progresso.getHorasExigidas());
    }

    @Test
    @DisplayName("Deve retornar 0% quando as horas exigidas forem negativas (sem percentual negativo)")
    void deveRetornarZeroQuandoHorasExigidasForNegativa() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(50, 0, -10);
        assertEquals(0, progresso.getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve retornar 0% quando o estudante nao possuir horas acumuladas")
    void deveRetornarZeroQuandoNaoHouverHorasAcumuladas() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(0, 0, 200);
        assertEquals(0, progresso.getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve truncar para 0% quando 1 de 200 horas forem cumpridas (divisao inteira)")
    void deveTruncarParaZeroQuandoUmaHoraDeDuzentas() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(1, 0, 200);
        assertEquals(0, progresso.getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve retornar 99% quando 199 de 200 horas forem cumpridas")
    void deveRetornarNoventaENoveQuandoQuaseCompleto() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(199, 0, 200);
        assertEquals(99, progresso.getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve retornar 100% quando as horas acumuladas forem exatamente as exigidas")
    void deveRetornarCemQuandoExatamenteCompleto() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(200, 0, 200);
        assertEquals(100, progresso.getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve limitar o percentual em 100% quando as horas acumuladas excederem as exigidas")
    void deveLimitarPercentualEmCemQuandoExcederCargaExigida() {
        ProgressoModalidadeResponseDTO progresso = new ProgressoModalidadeResponseDTO(350, 0, 200);
        assertEquals(100, progresso.getPercentualConcluido());
        assertEquals(350, progresso.getHorasAcumuladas());
        assertEquals(200, progresso.getHorasExigidas());
    }
}