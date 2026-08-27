package br.edu.ufape.backend.solicitacao.controller;

import br.edu.ufape.backend.solicitacao.dto.AvaliacaoSolicitacaoRequestDTO;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoDetalheResponseDTO;
import br.edu.ufape.backend.solicitacao.facade.SolicitacaoFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitacoes")
public class SolicitacaoAvaliacaoController {

    private final SolicitacaoFacade facade;

    public SolicitacaoAvaliacaoController(SolicitacaoFacade facade) {
        this.facade = facade;
    }

    @PatchMapping("/{id}/avaliacao")
    public ResponseEntity<SolicitacaoDetalheResponseDTO> avaliar(
            @PathVariable Long id,
            @RequestBody @Valid AvaliacaoSolicitacaoRequestDTO request,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SolicitacaoDetalheResponseDTO response = facade.avaliar(
                id, authentication.getName(), request.decisao(), request.justificativa());
        return ResponseEntity.ok(response);
    }
}
