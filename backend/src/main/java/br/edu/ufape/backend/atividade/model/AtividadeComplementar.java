package br.edu.ufape.backend.atividade.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.usuario.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "atividades_complementares")
public class AtividadeComplementar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "instituicao_responsavel", nullable = false)
    private String instituicaoResponsavel;

    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    @Column(name = "carga_horaria_em_horas", nullable = false)
    private Integer cargaHorariaEmHoras;

    @Enumerated(EnumType.STRING)
    @Column(name = "natureza", nullable = false)
    private Natureza natureza;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAtividade status = StatusAtividade.PENDENTE;

    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Embedded
    private Certificado certificado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario estudante;

    public AtividadeComplementar() {
    }

    public AtividadeComplementar(String titulo, String instituicaoResponsavel, LocalDate dataRealizacao,
            Integer cargaHorariaEmHoras, Natureza natureza, Categoria categoria, Certificado certificado,
            Usuario estudante) {
        this.titulo = titulo;
        this.instituicaoResponsavel = instituicaoResponsavel;
        this.dataRealizacao = dataRealizacao;
        this.cargaHorariaEmHoras = cargaHorariaEmHoras;
        this.natureza = natureza;
        this.categoria = categoria;
        this.status = StatusAtividade.PENDENTE;
        this.certificado = certificado;
        this.estudante = estudante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getInstituicaoResponsavel() {
        return instituicaoResponsavel;
    }

    public void setInstituicaoResponsavel(String instituicaoResponsavel) {
        this.instituicaoResponsavel = instituicaoResponsavel;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public Integer getCargaHorariaEmHoras() {
        return cargaHorariaEmHoras;
    }

    public void setCargaHorariaEmHoras(Integer cargaHorariaEmHoras) {
        this.cargaHorariaEmHoras = cargaHorariaEmHoras;
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public StatusAtividade getStatus() {
        return status;
    }

    public void setStatus(StatusAtividade status) {
        this.status = status;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public Usuario getEstudante() {
        return estudante;
    }

    public void setEstudante(Usuario estudante) {
        this.estudante = estudante;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    public void setCertificado(Certificado certificado) {
        this.certificado = certificado;
    }
}