package br.edu.ufape.backend.notificacao.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "destinatario_id", nullable = false)
	private Long destinatarioId;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false)
	private TipoNotificacao tipo;

	@Column(name = "titulo", nullable = false)
	private String titulo;

	@Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
	private String mensagem;

	@Column(name = "solicitacao_id")
	private Long solicitacaoId;

	@Column(name = "lida", nullable = false)
	private boolean lida = false;

	@CreationTimestamp
	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	public Notificacao() {
	}

	public Notificacao(Long destinatarioId, TipoNotificacao tipo, String titulo, String mensagem, Long solicitacaoId) {
		this.destinatarioId = destinatarioId;
		this.tipo = tipo;
		this.titulo = titulo;
		this.mensagem = mensagem;
		this.solicitacaoId = solicitacaoId;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getDestinatarioId() {
		return destinatarioId;
	}
	public void setDestinatarioId(Long destinatarioId) {
		this.destinatarioId = destinatarioId;
	}

	public TipoNotificacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoNotificacao tipo) {
		this.tipo = tipo;
	}

	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Long getSolicitacaoId() {
		return solicitacaoId;
	}
	public void setSolicitacaoId(Long solicitacaoId) {
		this.solicitacaoId = solicitacaoId;
	}

	public boolean isLida() {
		return lida;
	}
	public void setLida(boolean lida) {
		this.lida = lida;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
}
