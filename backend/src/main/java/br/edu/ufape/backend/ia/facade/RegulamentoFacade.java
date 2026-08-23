package br.edu.ufape.backend.ia.facade;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.service.IngestaoDocumentoNormativoService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class RegulamentoFacade {

    private final IngestaoDocumentoNormativoService ingestaoService;

    public RegulamentoFacade(IngestaoDocumentoNormativoService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }

    public IngestaoNormativaResponseDTO ingerirDocumentoNormativo(MultipartFile arquivo, boolean substituirExistentes) {
        return ingestaoService.ingerirDocumentoNormativo(arquivo, substituirExistentes);
    }

    public List<RegulamentoChunkResponseDTO> listarChunks() {
        return ingestaoService.listarChunks();
    }
}