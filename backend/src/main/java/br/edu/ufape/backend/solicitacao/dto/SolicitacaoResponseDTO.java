package br.edu.ufape.backend.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public record SolicitacaoResponseDTO(
        Long id,
        StatusSolicitacao status,
        LocalDateTime dataSubmissao,
        List<SolicitacaoAtividadeResponseDTO> itens
) {
    public SolicitacaoResponseDTO(SolicitacaoValidacao solicitacao) {
        this(
                solicitacao.getId(),
                solicitacao.getStatus(),
                solicitacao.getDataSubmissao(),
                solicitacao.getItens() != null
                        ? solicitacao.getItens().stream().map(SolicitacaoAtividadeResponseDTO::new).toList()
                        : List.of()
        );
    }
}

