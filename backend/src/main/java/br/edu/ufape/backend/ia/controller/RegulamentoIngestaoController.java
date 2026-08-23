package br.edu.ufape.backend.ia.controller;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.facade.RegulamentoFacade;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/regulamentos")
public class RegulamentoIngestaoController {

    private final RegulamentoFacade regulamentoFacade;

    public RegulamentoIngestaoController(RegulamentoFacade regulamentoFacade) {
        this.regulamentoFacade = regulamentoFacade;
    }

    @PostMapping(value = "/ingerir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IngestaoNormativaResponseDTO> submeterDocumentoNormativo(
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestParam(value = "substituirExistentes", defaultValue = "false") boolean substituirExistentes) {
        return ResponseEntity.ok(regulamentoFacade.ingerirDocumentoNormativo(arquivo, substituirExistentes));
    }

    @GetMapping
    public ResponseEntity<List<br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO>> listarRegulamentos() {
        return ResponseEntity.ok(regulamentoFacade.listarChunks());
    }
}