package br.edu.ufape.backend.ia.dto;

public record IngestaoNormativaResponseDTO(
    String nomeDocumento,
    int totalChunksExtraidos,
    String status,
    String mensagem
) {}