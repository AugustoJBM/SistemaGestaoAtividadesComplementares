package br.edu.ufape.backend.solicitacao.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResumoResponseDTO;
import br.edu.ufape.backend.solicitacao.exception.EstudanteSemAtividadesException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoEmAbertoException;
import br.edu.ufape.backend.solicitacao.exception.SolicitacaoNaoEncontradaException;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;

@Service
public class SolicitacaoService {

    private final SolicitacaoValidacaoRepository solicitacaoValidacaoRepository;
    private final AtividadeContrato atividadeContrato;

    public SolicitacaoService(
            SolicitacaoValidacaoRepository solicitacaoValidacaoRepository,
            AtividadeContrato atividadeContrato) {
        this.solicitacaoValidacaoRepository = solicitacaoValidacaoRepository;
        this.atividadeContrato = atividadeContrato;
    }

    // ---- Submissao pelo estudante ----

    @Transactional
    public SolicitacaoValidacao submeter(Long estudanteId) {
        if (solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                estudanteId, StatusSolicitacao.STATUS_EM_ABERTO)) {
            throw new SolicitacaoEmAbertoException(
                    "Ja existe uma solicitacao de validacao em aberto para este estudante.");
        }

        List<AtividadeResponseDTO> atividades = atividadeContrato.buscarPorEstudante(estudanteId);

        if (atividades == null || atividades.isEmpty()) {
            throw new EstudanteSemAtividadesException(
                    "Nao e possivel submeter solicitacao sem atividades complementares cadastradas.");
        }

        List<SolicitacaoAtividade> itensSnapshot = atividades.stream()
                .map(a -> new SolicitacaoAtividade(
                        a.id(),
                        a.titulo(),
                        a.cargaHorariaEmHoras(),
                        a.natureza() != null ? a.natureza().name() : null
                ))
                .toList();

        SolicitacaoValidacao solicitacao = new SolicitacaoValidacao(
                estudanteId,
                LocalDateTime.now(),
                StatusSolicitacao.SUBMETIDA,
                new ArrayList<>(itensSnapshot)
        );

        return solicitacaoValidacaoRepository.save(solicitacao);
    }

    // ---- Avaliacao pelo avaliador ----

    @Transactional
    public SolicitacaoValidacao avaliar(Long solicitacaoId, Long avaliadorId,
                                        DecisaoAvaliacao decisao, String justificativa) {
        SolicitacaoValidacao solicitacao = solicitacaoValidacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(solicitacaoId));

        StatusSolicitacao novoStatus = decisao.toStatus();
        MaquinaEstadosSolicitacao.validar(solicitacao.getStatus(), novoStatus);

        if ((novoStatus == StatusSolicitacao.REJEITADA || novoStatus == StatusSolicitacao.COM_PENDENCIAS)
                && (justificativa == null || justificativa.isBlank())) {
            throw new IllegalArgumentException(
                    "Justificativa e obrigatoria para decisao " + novoStatus);
        }

        solicitacao.setStatus(novoStatus);
        solicitacao.setJustificativa(justificativa);
        solicitacao.setAvaliadorId(avaliadorId);
        solicitacao.setDataAvaliacao(LocalDateTime.now());

        return solicitacaoValidacaoRepository.save(solicitacao);
    }

    // ---- Metodos do contrato publico ----

    @Transactional(readOnly = true)
    public List<SolicitacaoResumoResponseDTO> listarDoEstudante(Long estudanteId) {
        return solicitacaoValidacaoRepository.findResumosByEstudanteIdOrderByDataSubmissaoDesc(estudanteId);
    }

    @Transactional(readOnly = true)
    public SolicitacaoValidacao detalhar(Long estudanteId, Long solicitacaoId) {
        return solicitacaoValidacaoRepository.findByIdAndEstudanteId(solicitacaoId, estudanteId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException("Solicitação não encontrada."));
    }

    @Transactional(readOnly = true)
    public boolean existeSolicitacaoEmAbertoComAtividade(Long atividadeId) {
        return solicitacaoValidacaoRepository.existsByAtividadeIdAndStatusIn(
                atividadeId, StatusSolicitacao.STATUS_EM_ABERTO);
    }

    @Transactional(readOnly = true)
    public boolean existeSolicitacaoEmAbertoDoEstudante(Long estudanteId) {
        return solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(
                estudanteId, StatusSolicitacao.STATUS_EM_ABERTO);
    }
}