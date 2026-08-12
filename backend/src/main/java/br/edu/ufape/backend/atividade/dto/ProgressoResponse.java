package br.edu.ufape.backend.atividade.dto;

public class ProgressoResponse {

    private final ProgressoModalidadeResponse acc;
    private final ProgressoModalidadeResponse acex;

    public ProgressoResponse(ProgressoModalidadeResponse acc, ProgressoModalidadeResponse acex) {
        this.acc = acc;
        this.acex = acex;
    }

    public ProgressoModalidadeResponse getAcc() {
        return acc;
    }

    public ProgressoModalidadeResponse getAcex() {
        return acex;
    }
}
