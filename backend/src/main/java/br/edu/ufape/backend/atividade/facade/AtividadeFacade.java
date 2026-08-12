package br.edu.ufape.backend.atividade.facade;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.service.ProgressoService;

@Component
public class AtividadeFacade {

    private final ProgressoService progressoService;

    public AtividadeFacade(ProgressoService progressoService) {
        this.progressoService = progressoService;
    }

    public ProgressoResponse obterProgresso(String emailEstudante) {
        return progressoService.obterProgresso(emailEstudante);
    }
}
