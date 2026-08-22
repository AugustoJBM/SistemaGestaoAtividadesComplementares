package br.edu.ufape.backend.atividade.dto;

public class ProgressoResponseDTO {

    private final ProgressoModalidadeResponseDTO acc;
    private final ProgressoModalidadeResponseDTO acex;

    public ProgressoResponseDTO(ProgressoModalidadeResponseDTO acc, ProgressoModalidadeResponseDTO acex) {
        this.acc = acc;
        this.acex = acex;
    }

    public ProgressoModalidadeResponseDTO getAcc() {
        return acc;
    }

    public ProgressoModalidadeResponseDTO getAcex() {
        return acex;
    }
}
