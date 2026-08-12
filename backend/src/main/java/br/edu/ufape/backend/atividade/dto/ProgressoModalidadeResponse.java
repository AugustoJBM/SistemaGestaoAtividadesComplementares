package br.edu.ufape.backend.atividade.dto;

public class ProgressoModalidadeResponse {

    private final int horasAcumuladas;
    private final int horasExigidas;
    private final int percentualConcluido;

    public ProgressoModalidadeResponse(int horasAcumuladas, int horasExigidas) {
        this.horasAcumuladas = horasAcumuladas;
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

    public int getHorasExigidas() {
        return horasExigidas;
    }

    public int getPercentualConcluido() {
        return percentualConcluido;
    }
}
