package br.edu.ufape.backend.ia.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;

@RestController
@RequestMapping("/api/v1/atividades/extrair-certificado")
public class ExtracaoCertificadoController {

    private final IaCertificadoFacade iaCertificadoFacade;

    public ExtracaoCertificadoController(IaCertificadoFacade iaCertificadoFacade) {
        this.iaCertificadoFacade = iaCertificadoFacade;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtracaoCertificadoResponseDTO> extrairCertificado(
            @RequestPart("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(iaCertificadoFacade.extrairDadosCertificado(arquivo));
    }
}