package br.edu.ufape.backend.usuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliadores")
public class Avaliador extends Usuario {

	private String registro;
	private String areaAtuacao;
	private Integer solicitacoesPendentes = 0;

	public Avaliador() {
		super();
	}

	public Avaliador(String nome, String email, String senhaHash, String registro, String areaAtuacao) {
		super(nome, email, senhaHash, Role.AVALIADOR);
		this.registro = registro;
		this.areaAtuacao = areaAtuacao;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getAreaAtuacao() {
		return areaAtuacao;
	}

	public void setAreaAtuacao(String areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}

	public Integer getSolicitacoesPendentes() {
		return solicitacoesPendentes;
	}

	public void setSolicitacoesPendentes(Integer solicitacoesPendentes) {
		this.solicitacoesPendentes = solicitacoesPendentes;
	}
}
