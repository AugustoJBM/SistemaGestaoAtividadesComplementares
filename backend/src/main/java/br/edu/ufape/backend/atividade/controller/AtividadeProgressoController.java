package br.edu.ufape.backend.atividade.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;

@RestController
@RequestMapping("/api/v1/atividades/progresso")
public class AtividadeProgressoController {

    private final AtividadeFacade atividadeFacade;

    public AtividadeProgressoController(AtividadeFacade atividadeFacade) {
        this.atividadeFacade = atividadeFacade;
    }

    @GetMapping
    public ResponseEntity<ProgressoResponseDTO> progresso(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailEstudante = authentication.getName();
        ProgressoResponseDTO progressoResponse = atividadeFacade.obterProgresso(emailEstudante);
        return ResponseEntity.ok(progressoResponse);
    }
}