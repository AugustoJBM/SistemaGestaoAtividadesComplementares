package br.edu.ufape.backend.solicitacao.dto;

import java.time.LocalDateTime;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

public record SolicitacaoAvaliadorResumoResponseDTO(Long id, String estudanteNome, LocalDateTime dataSubmissao,
		StatusSolicitacao status, LocalDateTime dataAvaliacao, long totalAtividades, int cargaHorariaTotal) {
	public SolicitacaoAvaliadorResumoResponseDTO(SolicitacaoValidacao solicitacao, String estudanteNome) {
		this(solicitacao.getId(), estudanteNome, solicitacao.getDataSubmissao(), solicitacao.getStatus(),
				solicitacao.getDataAvaliacao(),
				solicitacao.getItens() != null ? (long) solicitacao.getItens().size() : 0L,
				solicitacao.getItens() != null
						? solicitacao.getItens().stream().mapToInt(SolicitacaoAtividade::getCargaHoraria).sum()
						: 0);
	}
}
