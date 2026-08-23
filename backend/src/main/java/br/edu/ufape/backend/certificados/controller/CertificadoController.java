package br.edu.ufape.backend.certificados.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ufape.backend.certificados.facade.CertificadoFacade;

@RestController
@RequestMapping("/api/v1/atividades")
public class CertificadoController {

    private final CertificadoFacade certificadoFacade;

    public CertificadoController(CertificadoFacade certificadoFacade) {
        this.certificadoFacade = certificadoFacade;
    }

    @GetMapping("/{id}/certificado")
    public ResponseEntity<Resource> obterCertificado(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailEstudante = authentication.getName();
        Resource resource = certificadoFacade.obterCertificado(id, emailEstudante);
        String contentType = "application/pdf";
        try {
            Path path = resource.getFile().toPath();
            String probedType = Files.probeContentType(path);
            if (probedType != null) {
                contentType = probedType;
            }
        } catch (IOException ignored) {
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}