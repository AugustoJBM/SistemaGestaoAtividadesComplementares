package br.edu.ufape.backend.relatorio.dto;

import java.util.List;

public record RelatorioAtividadesResponse(
        String estudanteEmail,
        List<GrupoNaturezaResponse> naturezas,
        int totalHorasAcc,
        int totalHorasAcex,
        int totalHorasGeral) {
}
