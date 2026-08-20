package br.edu.ufape.backend.atividade.contrato;

import java.util.List;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.Natureza;

public interface AtividadeContrato {
    /**
     * @throws AcessoNegadoAtividadeException se o email não existir
     * ou não pertencer a um Estudante
     */
    List<AtividadeResponse> buscarPorEstudante(String emailEstudante);

    /**
     * @throws AcessoNegadoAtividadeException se o email não existir
     * ou não pertencer a um Estudante
     */
    List<AtividadeResponse> buscarPorEstudanteENatureza(String emailEstudante, Natureza natureza);
}
