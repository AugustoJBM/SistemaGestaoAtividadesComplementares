package br.edu.ufape.backend.atividade.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.backend.atividade.dto.AvaliacaoDecisaoRequestDTO;
import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoAvaliador;
import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoIA;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

@Service
public class AvaliacaoAtividadeService {

    private final AtividadeComplementarRepository atividadeRepository;
    private final AuditoriaConformidadeService auditoriaConformidadeService;
    private final ParecerConformidadeRepository parecerRepository;

    public AvaliacaoAtividadeService(
            AtividadeComplementarRepository atividadeRepository,
            AuditoriaConformidadeService auditoriaConformidadeService,
            ParecerConformidadeRepository parecerRepository) {
        this.atividadeRepository = atividadeRepository;
        this.auditoriaConformidadeService = auditoriaConformidadeService;
        this.parecerRepository = parecerRepository;
    }

    @Transactional
    public ParecerResponseDTO avaliarAtividade(Long atividadeId, AvaliacaoDecisaoRequestDTO request) {
        AtividadeComplementar atividade = atividadeRepository.findById(atividadeId)
                .orElseThrow(() -> new AtividadeNaoEncontradaException("Atividade não encontrada."));

        // Bloqueio de reavaliação de atividades em estado terminal
        if (atividade.getStatus() != null && atividade.getStatus() != StatusAtividade.PENDENTE) {
            throw new IllegalArgumentException(
                    String.format("A atividade #%d já foi finalizada como %s e não pode ser reavaliada.",
                            atividadeId, atividade.getStatus()));
        }

        // Garante que o parecer da IA exista para a atividade
        ParecerConformidade parecer = auditoriaConformidadeService.auditarOuObterParecer(atividade);

        // Atualiza a decisão final do avaliador
        parecer.setDecisaoFinalAvaliador(request.decisao());

        // Confronta decisão do avaliador com a recomendação da IA
        boolean concordou = (request.decisao() == DecisaoAvaliador.DEFERIDO
                && parecer.getDecisaoIA() == DecisaoIA.DEFERIDO)
                || (request.decisao() == DecisaoAvaliador.INDEFERIDO && parecer.getDecisaoIA() == DecisaoIA.INDEFERIDO);
        parecer.setAvaliadorConcordouComIA(concordou);

        // Atualiza o estado da atividade no ciclo acadêmico
        if (request.decisao() == DecisaoAvaliador.DEFERIDO) {
            atividade.setStatus(StatusAtividade.APROVADA);
        } else {
            atividade.setStatus(StatusAtividade.REJEITADA);
        }

        atividadeRepository.save(atividade);
        ParecerConformidade salvo = parecerRepository.save(parecer);
        return ParecerResponseDTO.fromEntity(salvo);
    }
}