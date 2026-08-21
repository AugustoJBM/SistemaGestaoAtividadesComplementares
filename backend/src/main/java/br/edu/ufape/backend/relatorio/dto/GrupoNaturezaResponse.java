package br.edu.ufape.backend.relatorio.dto;

import java.util.List;

public record GrupoNaturezaResponse(
        String natureza,
        int totalHoras,
        List<GrupoCategoriaResponse> categorias) {
}
