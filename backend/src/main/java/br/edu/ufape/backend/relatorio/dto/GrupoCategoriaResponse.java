package br.edu.ufape.backend.relatorio.dto;

import java.util.List;

public record GrupoCategoriaResponse(
        String categoria,
        int totalHoras,
        List<ItemAtividadeResponse> atividades) {
}
