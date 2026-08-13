package br.edu.ufape.backend.atividade.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;

public class AtividadeResponse {

    private final Long id;
    private final String titulo;
    private final String instituicaoResponsavel;
    private final LocalDate dataRealizacao;
    private final Integer cargaHorariaEmHoras;
    private final Natureza natureza;
    private final Categoria categoria;
    private final LocalDateTime dataCadastro;
    private final String estudanteEmail;

    public AtividadeResponse(AtividadeComplementar atividade) {
        this.id = atividade.getId();
        this.titulo = atividade.getTitulo();
        this.instituicaoResponsavel = atividade.getInstituicaoResponsavel();
        this.dataRealizacao = atividade.getDataRealizacao();
        this.cargaHorariaEmHoras = atividade.getCargaHorariaEmHoras();
        this.natureza = atividade.getNatureza();
        this.categoria = atividade.getCategoria();
        this.dataCadastro = atividade.getDataCadastro();
        this.estudanteEmail = atividade.getEstudante().getEmail();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getInstituicaoResponsavel() {
        return instituicaoResponsavel;
    }

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public Integer getCargaHorariaEmHoras() {
        return cargaHorariaEmHoras;
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public String getEstudanteEmail() {
        return estudanteEmail;
    }
}
