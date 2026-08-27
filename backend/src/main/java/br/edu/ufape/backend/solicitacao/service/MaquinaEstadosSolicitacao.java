package br.edu.ufape.backend.solicitacao.service;

import br.edu.ufape.backend.solicitacao.exception.TransicaoEstadoInvalidaException;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Maquina de estados para SolicitacaoValidacao. Ponto unico e testavel
 * para todas as transicoes validas do ciclo de vida de uma solicitacao.
 *
 * <p>Transicoes validas:</p>
 * <ul>
 *   <li>SUBMETIDA  -> EM_ANALISE (preparacao para fila de pendentes — US futura)</li>
 *   <li>SUBMETIDA  -> APROVADA | REJEITADA | COM_PENDENCIAS (transicao implicita)</li>
 *   <li>EM_ANALISE -> APROVADA | REJEITADA | COM_PENDENCIAS</li>
 *   <li>COM_PENDENCIAS -> SUBMETIDA (re-submissao pelo estudante — US futura;
 *       nenhum endpoint expoe esta transicao ainda)</li>
 * </ul>
 *
 * <p>Estados terminais definitivos: APROVADA e REJEITADA.</p>
 */
public final class MaquinaEstadosSolicitacao {

    private static final Map<StatusSolicitacao, Set<StatusSolicitacao>> TRANSICOES_VALIDAS = Map.of(
            StatusSolicitacao.SUBMETIDA, EnumSet.of(
                    StatusSolicitacao.EM_ANALISE,        // transicao explicita (fila de pendentes, US futura)
                    StatusSolicitacao.APROVADA,
                    StatusSolicitacao.REJEITADA,
                    StatusSolicitacao.COM_PENDENCIAS),
            StatusSolicitacao.EM_ANALISE, EnumSet.of(
                    StatusSolicitacao.APROVADA,
                    StatusSolicitacao.REJEITADA,
                    StatusSolicitacao.COM_PENDENCIAS),
            StatusSolicitacao.COM_PENDENCIAS, EnumSet.of(
                    StatusSolicitacao.SUBMETIDA),         // re-submissao pelo estudante (US futura)
            StatusSolicitacao.APROVADA,   EnumSet.noneOf(StatusSolicitacao.class),
            StatusSolicitacao.REJEITADA,  EnumSet.noneOf(StatusSolicitacao.class)
    );

    private MaquinaEstadosSolicitacao() {}

    /**
     * Valida se a transicao de {@code estadoAtual} para {@code novoEstado} e permitida.
     * Lanca {@link TransicaoEstadoInvalidaException} (HTTP 409) caso contrario.
     */
    public static void validar(StatusSolicitacao estadoAtual, StatusSolicitacao novoEstado) {
        Set<StatusSolicitacao> permitidos = TRANSICOES_VALIDAS.getOrDefault(estadoAtual, Set.of());
        if (!permitidos.contains(novoEstado)) {
            throw new TransicaoEstadoInvalidaException(estadoAtual, novoEstado);
        }
    }
}