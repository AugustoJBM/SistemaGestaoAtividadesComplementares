package br.edu.ufape.backend.curso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cursos")
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 150)
	private String nome;

	@Column(nullable = false, unique = true, length = 30)
	private String codigo;

	@Column(name = "horas_acc_exigidas", nullable = false)
	private Integer horasAccExigidas = 90;

	@Column(name = "horas_acex_exigidas", nullable = false)
	private Integer horasAcexExigidas = 320;

	@Column(nullable = false)
	private boolean ativo = true;

	public Curso() {
	}

	public Curso(String nome, String codigo, Integer horasAccExigidas, Integer horasAcexExigidas) {
		this.nome = nome;
		this.codigo = codigo;
		this.horasAccExigidas = horasAccExigidas != null ? horasAccExigidas : 90;
		this.horasAcexExigidas = horasAcexExigidas != null ? horasAcexExigidas : 320;
		this.ativo = true;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Integer getHorasAccExigidas() {
		return horasAccExigidas;
	}
	public void setHorasAccExigidas(Integer horasAccExigidas) {
		this.horasAccExigidas = horasAccExigidas;
	}
	public Integer getHorasAcexExigidas() {
		return horasAcexExigidas;
	}
	public void setHorasAcexExigidas(Integer horasAcexExigidas) {
		this.horasAcexExigidas = horasAcexExigidas;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
