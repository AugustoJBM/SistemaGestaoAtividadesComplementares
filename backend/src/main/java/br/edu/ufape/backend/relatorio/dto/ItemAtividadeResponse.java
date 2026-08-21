package br.edu.ufape.backend.relatorio.dto;

import java.time.LocalDate;

public record ItemAtividadeResponse(
        Long id,
        String titulo,
        String instituicaoResponsavel,
        LocalDate dataRealizacao,
        Integer cargaHorariaEmHoras) {
}
