package br.edu.ufape.backend.atividade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sgac.progresso")
public class ProgressoProperties {

	private Acc acc = new Acc();
	private Acex acex = new Acex();

	public Acc getAcc() {
		return acc;
	}

	public void setAcc(Acc acc) {
		this.acc = acc;
	}

	public Acex getAcex() {
		return acex;
	}

	public void setAcex(Acex acex) {
		this.acex = acex;
	}

	public static class Acc {
		private int horasExigidas = 90;

		public int getHorasExigidas() {
			return horasExigidas;
		}

		public void setHorasExigidas(int horasExigidas) {
			this.horasExigidas = horasExigidas;
		}
	}

	public static class Acex {
		private int horasExigidas = 320;

		public int getHorasExigidas() {
			return horasExigidas;
		}

		public void setHorasExigidas(int horasExigidas) {
			this.horasExigidas = horasExigidas;
		}
	}
}
