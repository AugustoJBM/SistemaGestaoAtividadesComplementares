package br.edu.ufape.backend.ia.dto;

import br.edu.ufape.backend.ia.model.RegulamentoChunk;

public record RegulamentoChunkResponseDTO(
    Long id,
    String artigo,
    String conteudoTexto
) {
    public static RegulamentoChunkResponseDTO fromEntity(RegulamentoChunk entity) {
        return new RegulamentoChunkResponseDTO(
            entity.getId(),
            entity.getArtigo(),
            entity.getConteudoTexto()
        );
    }
}