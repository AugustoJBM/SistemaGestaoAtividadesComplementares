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

    @Transactional
    public SolicitacaoValidacao submeter(Long estudanteId) {
        // Regra 1: Verificar se ja existe solicitacao em aberto para este estudante
        if (solicitacaoValidacaoRepository.existsByEstudanteIdAndStatusIn(estudanteId, StatusSolicitacao.STATUS_EM_ABERTO)) {
            throw new SolicitacaoEmAbertoException("Já existe uma solicitação de validação em aberto para este estudante.");
        }

        // Regra 2: Buscar atividades via AtividadeContrato (interface publica do modulo de atividades)
        List<AtividadeResponseDTO> atividades = atividadeContrato.buscarPorEstudante(estudanteId);

        // Regra 3: Se nao possuir nenhuma atividade, lancar excecao de negocio (422)
        if (atividades == null || atividades.isEmpty()) {
            throw new EstudanteSemAtividadesException("Não é possível submeter solicitação sem atividades complementares cadastradas.");
        }

        // Regra 4: Criar SolicitacaoValidacao e converter atividades para snapshots imutaveis (SolicitacaoAtividade)
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