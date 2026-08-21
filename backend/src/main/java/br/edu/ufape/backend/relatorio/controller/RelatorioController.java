package br.edu.ufape.backend.relatorio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.relatorio.dto.RelatorioAtividadesResponse;
import br.edu.ufape.backend.relatorio.facade.RelatorioFacade;

@RestController
@RequestMapping("/api/v1/relatorios")
public class RelatorioController {

    private final RelatorioFacade relatorioFacade;

    public RelatorioController(RelatorioFacade relatorioFacade) {
        this.relatorioFacade = relatorioFacade;
    }

    @GetMapping("/atividades")
    public ResponseEntity<RelatorioAtividadesResponse> atividades(Authentication authentication) {
        String emailEstudante = authentication.getName();
        RelatorioAtividadesResponse response = relatorioFacade.gerarRelatorio(emailEstudante);
        return ResponseEntity.ok(response);
    }
}
