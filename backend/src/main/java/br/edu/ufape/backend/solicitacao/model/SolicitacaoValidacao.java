package br.edu.ufape.backend.solicitacao.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitacoes_validacao")
public class SolicitacaoValidacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudante_id", nullable = false)
    private Long estudanteId;

    @Column(name = "data_submissao", nullable = false)
    private LocalDateTime dataSubmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusSolicitacao status = StatusSolicitacao.SUBMETIDA;

    @Column(name = "justificativa")
    private String justificativa;

    @Column(name = "avaliador_id")
    private Long avaliadorId;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private List<SolicitacaoAtividade> itens = new ArrayList<>();

    public SolicitacaoValidacao() {
    }

    public SolicitacaoValidacao(Long estudanteId, LocalDateTime dataSubmissao, StatusSolicitacao status, List<SolicitacaoAtividade> itens) {
        this.estudanteId = estudanteId;
        this.dataSubmissao = dataSubmissao;
        this.status = status != null ? status : StatusSolicitacao.SUBMETIDA;
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEstudanteId() {
        return estudanteId;
    }

    public void setEstudanteId(Long estudanteId) {
        this.estudanteId = estudanteId;
    }

    public LocalDateTime getDataSubmissao() {
        return dataSubmissao;
    }

    public void setDataSubmissao(LocalDateTime dataSubmissao) {
        this.dataSubmissao = dataSubmissao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Long getAvaliadorId() {
        return avaliadorId;
    }

    public void setAvaliadorId(Long avaliadorId) {
        this.avaliadorId = avaliadorId;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public List<SolicitacaoAtividade> getItens() {
        return itens;
    }

    public void setItens(List<SolicitacaoAtividade> itens) {
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
    }
}

