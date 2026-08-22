package br.edu.ufape.backend.atividade.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;

public record AtividadeResponseDTO(
    Long id,
    String titulo,
    String instituicaoResponsavel,
    LocalDate dataRealizacao,
    Integer cargaHorariaEmHoras,
    Natureza natureza,
    Categoria categoria,
    LocalDateTime dataCadastro,
    String estudanteEmail
) {
    public AtividadeResponseDTO(AtividadeComplementar atividade) {
        this(
            atividade.getId(),
            atividade.getTitulo(),
            atividade.getInstituicaoResponsavel(),
            atividade.getDataRealizacao(),
            atividade.getCargaHorariaEmHoras(),
            atividade.getNatureza(),
            atividade.getCategoria(),
            atividade.getDataCadastro(),
            atividade.getEstudante() != null ? atividade.getEstudante().getEmail() : null
        );
    }
}