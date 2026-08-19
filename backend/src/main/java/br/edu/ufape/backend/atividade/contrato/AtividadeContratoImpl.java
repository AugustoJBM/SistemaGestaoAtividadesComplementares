package br.edu.ufape.backend.atividade.contrato;

import java.util.List;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;

@Component
public class AtividadeContratoImpl implements AtividadeContrato {

    private final AtividadeComplementarService atividadeComplementarService;

    public AtividadeContratoImpl(AtividadeComplementarService atividadeComplementarService) {
        this.atividadeComplementarService = atividadeComplementarService;
    }

    @Override
    public List<AtividadeResponse> buscarPorEstudante(String emailEstudante) {
        return atividadeComplementarService.listarAtividadesDoEstudante(emailEstudante, null, null)
                .stream()
                .map(AtividadeResponse::new)
                .toList();
    }

    @Override
    public List<AtividadeResponse> buscarPorEstudanteENatureza(String emailEstudante, Natureza natureza) {
        return atividadeComplementarService.listarAtividadesDoEstudante(emailEstudante, natureza, null)
                .stream()
                .map(AtividadeResponse::new)
                .toList();
    }
}
