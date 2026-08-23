package br.edu.ufape.backend.ia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;

@RestController
@RequestMapping("/api/v1/atividades")
public class ParecerConformidadeController {

    private final IaCertificadoFacade iaCertificadoFacade;

    public ParecerConformidadeController(IaCertificadoFacade iaCertificadoFacade) {
        this.iaCertificadoFacade = iaCertificadoFacade;
    }

    @GetMapping("/{id}/parecer")
    public ResponseEntity<ParecerResponseDTO> obterParecer(@PathVariable Long id) {
        return ResponseEntity.ok(iaCertificadoFacade.obterOuGerarParecer(id));
    }
}