package br.edu.ufape.backend.solicitacao.dto;

import java.time.LocalDateTime;
import java.util.List;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public record SolicitacaoAvaliadorDetalheResponseDTO(
		Long id,
		String estudanteNome,
		String estudanteEmail,
		LocalDateTime dataSubmissao,
		StatusSolicitacao status,
		String justificativa,
		LocalDateTime dataAvaliacao,
		int cargaHorariaTotal,
		List<SolicitacaoAtividadeResponseDTO> itens
) {
	public SolicitacaoAvaliadorDetalheResponseDTO(SolicitacaoValidacao solicitacao, String estudanteNome, String estudanteEmail) {
		this(
				solicitacao.getId(),
				estudanteNome,
				estudanteEmail,
				solicitacao.getDataSubmissao(),
				solicitacao.getStatus(),
				solicitacao.getJustificativa(),
				solicitacao.getDataAvaliacao(),
				solicitacao.getItens() != null
						? solicitacao.getItens().stream().mapToInt(SolicitacaoAtividade::getCargaHoraria).sum()
						: 0,
				solicitacao.getItens() != null
						? solicitacao.getItens().stream().map(SolicitacaoAtividadeResponseDTO::new).toList()
						: List.of()
		);
	}
}