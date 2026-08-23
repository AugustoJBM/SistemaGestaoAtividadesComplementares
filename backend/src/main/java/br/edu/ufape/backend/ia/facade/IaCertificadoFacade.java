package br.edu.ufape.backend.ia.facade;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

@Component
public class IaCertificadoFacade {

    private final AuditoriaConformidadeService auditoriaService;
    private final AtividadeComplementarService atividadeComplementarService;

    public IaCertificadoFacade(
            AuditoriaConformidadeService auditoriaService,
            AtividadeComplementarService atividadeComplementarService) {
        this.auditoriaService = auditoriaService;
        this.atividadeComplementarService = atividadeComplementarService;
    }

    public ExtracaoCertificadoResponseDTO extrairDadosCertificado(MultipartFile arquivo) {
        return auditoriaService.extrairDadosArquivo(arquivo);
    }

    public ParecerResponseDTO obterOuGerarParecer(Long atividadeId) {
        AtividadeComplementar atividade = atividadeComplementarService.buscarPorId(atividadeId);
        ParecerConformidade parecer = auditoriaService.auditarOuObterParecer(atividade);
        return ParecerResponseDTO.fromEntity(parecer);
    }

    public long contarAvaliadas() {
        return auditoriaService.contarAvaliadas();
    }

    public long contarConcordancias() {
        return auditoriaService.contarConcordancias();
    }

    public Double calcularTempoMedioMs() {
        return auditoriaService.calcularTempoMedioMs();
    }
}