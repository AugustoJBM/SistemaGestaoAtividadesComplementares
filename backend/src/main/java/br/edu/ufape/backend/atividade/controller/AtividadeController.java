package br.edu.ufape.backend.atividade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;

@RestController
@RequestMapping("/api/v1/atividades")
public class AtividadeController {

    private final AtividadeFacade atividadeFacade;

    public AtividadeController(AtividadeFacade atividadeFacade) {
        this.atividadeFacade = atividadeFacade;
    }

    @GetMapping("/progresso")
    public ResponseEntity<ProgressoResponse> progresso(Authentication authentication) {
        String emailEstudante = authentication.getName();
        ProgressoResponse progressoResponse = atividadeFacade.obterProgresso(emailEstudante);
        return ResponseEntity.ok(progressoResponse);
    }
}
