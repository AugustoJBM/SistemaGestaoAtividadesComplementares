package br.edu.ufape.backend.atividade.dto;

public class ProgressoModalidadeResponseDTO {

    private final int horasAcumuladas;
    private final int horasPendentes;
    private final int horasExigidas;
    private final int percentualConcluido;

    public ProgressoModalidadeResponseDTO(int horasAcumuladas, int horasPendentes, int horasExigidas) {
        this.horasAcumuladas = horasAcumuladas;
        this.horasPendentes = horasPendentes;
        this.horasExigidas = horasExigidas;
        this.percentualConcluido = calcularPercentual(horasAcumuladas, horasExigidas);
    }

    private static int calcularPercentual(int horasAcumuladas, int horasExigidas) {
        if (horasExigidas <= 0) {
            return 0;
        }
        int percentual = (horasAcumuladas * 100) / horasExigidas;
        return Math.min(percentual, 100);
    }

    public int getHorasAcumuladas() {
        return horasAcumuladas;
    }

    public int getHorasPendentes() {
        return horasPendentes;
    }

    public int getHorasExigidas() {
        return horasExigidas;
    }

    public int getPercentualConcluido() {
        return percentualConcluido;
    }
}
