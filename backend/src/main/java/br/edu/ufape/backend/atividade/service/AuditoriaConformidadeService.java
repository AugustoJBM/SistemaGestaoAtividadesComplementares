package br.edu.ufape.backend.atividade.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.ia.contrato.IaContrato;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

@Service
public class AuditoriaConformidadeService {

    private final IaContrato iaContrato;
    private final ParecerConformidadeRepository parecerRepository;

    public AuditoriaConformidadeService(
            IaContrato iaContrato,
            ParecerConformidadeRepository parecerRepository) {
        this.iaContrato = iaContrato;
        this.parecerRepository = parecerRepository;
    }

    @Transactional
    public ParecerConformidade auditarOuObterParecer(AtividadeComplementar atividade) {
        return parecerRepository.findByAtividadeId(atividade.getId()).orElseGet(() -> {
            long inicio = System.currentTimeMillis();

            ParecerResponseDTO dto = iaContrato.gerarParecerConformidade(
                    atividade.getTitulo(),
                    atividade.getInstituicaoResponsavel(),
                    atividade.getNatureza().name(),
                    atividade.getCategoria().name(),
                    atividade.getCargaHorariaEmHoras());

            long tempo = System.currentTimeMillis() - inicio;

            ParecerConformidade p = new ParecerConformidade();
            p.setAtividade(atividade);
            p.setNaturezaSugerida(dto.naturezaSugerida());
            p.setCategoriaSugerida(dto.categoriaSugerida());
            p.setCargaHorariaAproveitavel(dto.cargaHorariaAproveitavel());
            p.setArtigoRegulamento(dto.artigoRegulamento());
            p.setJustificativaTecnica(dto.justificativaTecnica());
            p.setScoreConfianca(dto.scoreConfianca());
            p.setDecisaoIA(converterDecisaoSegura(dto.decisaoIA()));
            p.setTempoProcessamentoMs(tempo);

            return parecerRepository.save(p);
        });
    }

    public ExtracaoCertificadoResponseDTO extrairDadosArquivo(MultipartFile arquivo) {
        return iaContrato.extrairDadosArquivo(arquivo);
    }

    @Transactional
    public ParecerConformidade auditarAtividade(AtividadeComplementar atividade) {
        return auditarOuObterParecer(atividade);
    }

    public long contarAvaliadas() {
        return parecerRepository.countByDecisaoFinalAvaliadorIsNotNull();
    }

    public long contarConcordancias() {
        return parecerRepository.countByAvaliadorConcordouComIATrue();
    }

    public Double calcularTempoMedioMs() {
        return parecerRepository.calcularTempoMedioMs();
    }

    private ParecerConformidade.DecisaoIA converterDecisaoSegura(String decisao) {
        if (decisao == null) {
            return ParecerConformidade.DecisaoIA.DEFERIDO;
        }
        try {
            return ParecerConformidade.DecisaoIA.valueOf(decisao.toUpperCase().trim());
        } catch (Exception e) {
            return ParecerConformidade.DecisaoIA.AMBIGUO;
        }
    }
}