package br.edu.ufape.backend.atividade.contrato;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<AtividadeResponse> buscarPorEstudante(String emailEstudante) {
        return atividadeComplementarService.listarAtividadesDoEstudante(emailEstudante, null, null)
                .stream()
                .map(AtividadeResponse::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtividadeResponse> buscarPorEstudanteENatureza(String emailEstudante, Natureza natureza) {
        return atividadeComplementarService.listarAtividadesDoEstudante(emailEstudante, natureza, null)
                .stream()
                .map(AtividadeResponse::new)
                .toList();
    }
}
