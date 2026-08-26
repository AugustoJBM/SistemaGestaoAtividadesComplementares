package br.edu.ufape.backend.solicitacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResponseDTO;
import br.edu.ufape.backend.solicitacao.facade.SolicitacaoFacade;

@RestController
@RequestMapping("/api/v1/solicitacoes")
public class SolicitacaoEstudanteController {

    private final SolicitacaoFacade solicitacaoFacade;

    public SolicitacaoEstudanteController(SolicitacaoFacade solicitacaoFacade) {
        this.solicitacaoFacade = solicitacaoFacade;
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> submeter(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailEstudante = authentication.getName();
        SolicitacaoResponseDTO response = solicitacaoFacade.submeter(emailEstudante);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

