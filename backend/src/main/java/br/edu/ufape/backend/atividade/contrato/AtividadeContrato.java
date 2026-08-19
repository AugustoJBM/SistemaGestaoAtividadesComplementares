package br.edu.ufape.backend.atividade.contrato;

import java.util.List;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.model.Natureza;

public interface AtividadeContrato {
    List<AtividadeResponse> buscarPorEstudante(String emailEstudante);
    List<AtividadeResponse> buscarPorEstudanteENatureza(String emailEstudante, Natureza natureza);
}
