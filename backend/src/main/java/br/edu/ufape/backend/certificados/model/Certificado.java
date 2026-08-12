package br.edu.ufape.backend.certificados.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Certificado {

    @Column(name = "certificado_nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "certificado_tipo_conteudo", nullable = false)
    private String tipoConteudo;

    @Column(name = "certificado_tamanho_em_bytes", nullable = false)
    private Long tamanhoEmBytes;

    @Column(name = "certificado_referencia", nullable = false)
    private String referencia;

    public Certificado() {
    }

    public Certificado(String nomeArquivo, String tipoConteudo, Long tamanhoEmBytes, String referencia) {
        this.nomeArquivo = nomeArquivo;
        this.tipoConteudo = tipoConteudo;
        this.tamanhoEmBytes = tamanhoEmBytes;
        this.referencia = referencia;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public Long getTamanhoEmBytes() {
        return tamanhoEmBytes;
    }

    public void setTamanhoEmBytes(Long tamanhoEmBytes) {
        this.tamanhoEmBytes = tamanhoEmBytes;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
