package br.edu.ufape.backend.ia.dto;

import br.edu.ufape.backend.atividade.model.ParecerConformidade;

public record ParecerResponseDTO(
        Long id,
        Long atividadeId,
        String naturezaSugerida,
        String categoriaSugerida,
        Integer cargaHorariaAproveitavel,
        String artigoRegulamento,
        String justificativaTecnica,
        Double scoreConfianca,
        String decisaoIA,
        Long tempoProcessamentoMs) {
    public static ParecerResponseDTO fromEntity(ParecerConformidade p) {
        return new ParecerResponseDTO(
                p.getId(),
                p.getAtividade().getId(),
                p.getNaturezaSugerida(),
                p.getCategoriaSugerida(),
                p.getCargaHorariaAproveitavel(),
                p.getArtigoRegulamento(),
                p.getJustificativaTecnica(),
                p.getScoreConfianca(),
                p.getDecisaoIA().name(),
                p.getTempoProcessamentoMs());
    }
}