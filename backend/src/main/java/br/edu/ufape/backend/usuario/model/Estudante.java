package br.edu.ufape.backend.usuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "estudantes")
public class Estudante extends Usuario {

    private String matricula;
    private String curso;
    private Integer cargaHorariaObrigatoria;
    private Integer cargaHorariaCumprida;
    private String situacao;

    public Estudante() {
        super();
    }

    public Estudante(String nome, String email, String senhaHash) {
        super(nome, email, senhaHash, Role.ESTUDANTE);
    }

    public Estudante(String nome, String email, String senhaHash, String matricula, String curso) {
        super(nome, email, senhaHash, Role.ESTUDANTE);
        this.matricula = matricula;
        this.curso = curso;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getCargaHorariaObrigatoria() {
        return cargaHorariaObrigatoria;
    }

    public void setCargaHorariaObrigatoria(Integer cargaHorariaObrigatoria) {
        this.cargaHorariaObrigatoria = cargaHorariaObrigatoria;
    }

    public Integer getCargaHorariaCumprida() {
        return cargaHorariaCumprida;
    }

    public void setCargaHorariaCumprida(Integer cargaHorariaCumprida) {
        this.cargaHorariaCumprida = cargaHorariaCumprida;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}