package br.edu.ufape.backend.ia.dto;

import java.time.LocalDate;

public record ExtracaoCertificadoResponseDTO(
    String titulo,
    String instituicaoResponsavel,
    LocalDate dataRealizacao,
    Integer cargaHoraria,
    String natureza,
    String categoria
) {}