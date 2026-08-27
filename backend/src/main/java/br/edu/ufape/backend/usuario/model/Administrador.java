package br.edu.ufape.backend.usuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
public class Administrador extends Usuario {

	private String nivelAcesso;
	private String setor;

	public Administrador() {
		super();
	}

	public Administrador(String nome, String email, String senhaHash, String nivelAcesso, String setor) {
		super(nome, email, senhaHash, Role.ADMINISTRADOR);
		this.nivelAcesso = nivelAcesso;
		this.setor = setor;
	}

	public String getNivelAcesso() {
		return nivelAcesso;
	}

	public void setNivelAcesso(String nivelAcesso) {
		this.nivelAcesso = nivelAcesso;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}
}
