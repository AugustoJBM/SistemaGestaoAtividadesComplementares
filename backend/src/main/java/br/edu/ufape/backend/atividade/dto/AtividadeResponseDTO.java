package br.edu.ufape.backend.atividade.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.StatusAtividade;

public record AtividadeResponseDTO(
        Long id,
        String titulo,
        String instituicaoResponsavel,
        LocalDate dataRealizacao,
        Integer cargaHorariaEmHoras,
        Natureza natureza,
        Categoria categoria,
        LocalDateTime dataCadastro,
        String estudanteEmail,
        StatusAtividade status) {

    public AtividadeResponseDTO(
            Long id,
            String titulo,
            String instituicaoResponsavel,
            LocalDate dataRealizacao,
            Integer cargaHorariaEmHoras,
            Natureza natureza,
            Categoria categoria,
            LocalDateTime dataCadastro,
            String estudanteEmail) {
        this(id, titulo, instituicaoResponsavel, dataRealizacao, cargaHorariaEmHoras, natureza, categoria, dataCadastro,
                estudanteEmail, StatusAtividade.PENDENTE);
    }

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
                atividade.getEstudante() != null ? atividade.getEstudante().getEmail() : null,
                atividade.getStatus() != null ? atividade.getStatus() : StatusAtividade.PENDENTE);
    }
}