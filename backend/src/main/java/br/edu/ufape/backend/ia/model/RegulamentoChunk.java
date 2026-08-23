package br.edu.ufape.backend.ia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "regulamento_chunks")
public class RegulamentoChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String artigo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudoTexto;

    @Column(columnDefinition = "TEXT")
    private String embeddingVetor;

    public RegulamentoChunk() {}

    public RegulamentoChunk(String artigo, String conteudoTexto, String embeddingVetor) {
        this.artigo = artigo;
        this.conteudoTexto = conteudoTexto;
        this.embeddingVetor = embeddingVetor;
    }

    public Long getId() { return id; }
    public String getArtigo() { return artigo; }
    public String getConteudoTexto() { return conteudoTexto; }
    public String getEmbeddingVetor() { return embeddingVetor; }
    public void setEmbeddingVetor(String embeddingVetor) { this.embeddingVetor = embeddingVetor; }
}