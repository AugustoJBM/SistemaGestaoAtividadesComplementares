package br.edu.ufape.backend.solicitacao.dto;

import java.time.LocalDateTime;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public record SolicitacaoResumoResponseDTO(
        Long id,
        StatusSolicitacao status,
        LocalDateTime dataSubmissao,
        LocalDateTime dataAvaliacao,
        Long totalAtividades
) {
    public SolicitacaoResumoResponseDTO(SolicitacaoValidacao solicitacao) {
        this(
                solicitacao.getId(),
                solicitacao.getStatus(),
                solicitacao.getDataSubmissao(),
                solicitacao.getDataAvaliacao(),
                solicitacao.getItens() != null ? (long) solicitacao.getItens().size() : 0L
        );
    }
}