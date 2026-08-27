package br.edu.ufape.backend.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public record SolicitacaoDetalheResponseDTO(
        Long id,
        StatusSolicitacao status,
        LocalDateTime dataSubmissao,
        LocalDateTime dataAvaliacao,
        String justificativa,
        List<SolicitacaoAtividadeResponseDTO> itens,
        Integer totalAtividades) {

    public SolicitacaoDetalheResponseDTO(SolicitacaoValidacao solicitacao) {
        this(
                solicitacao.getId(),
                solicitacao.getStatus(),
                solicitacao.getDataSubmissao(),
                solicitacao.getDataAvaliacao(),
                solicitacao.getJustificativa(),
                solicitacao.getItens() != null
                        ? solicitacao.getItens().stream().map(SolicitacaoAtividadeResponseDTO::new).toList()
                        : List.of(),
                solicitacao.getItens() != null ? solicitacao.getItens().size() : 0
        );
    }
}
