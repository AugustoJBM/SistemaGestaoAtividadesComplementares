package br.edu.ufape.backend.atividade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface AtividadeComplementarRepository extends JpaRepository<AtividadeComplementar, Long> {

    List<AtividadeComplementar> findByEstudante(Usuario estudante);
}
