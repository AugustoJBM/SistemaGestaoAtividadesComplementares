package br.edu.ufape.backend.atividade.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.usuario.model.Usuario;

public interface AtividadeComplementarRepository extends JpaRepository<AtividadeComplementar, Long> {

        List<AtividadeComplementar> findByEstudante(Usuario estudante);

        List<AtividadeComplementar> findByEstudante_Id(Long estudanteId);

        List<AtividadeComplementar> findByEstudanteAndNatureza(Usuario estudante, Natureza natureza);

        List<AtividadeComplementar> findByEstudanteAndCategoria(Usuario estudante, Categoria categoria);

        List<AtividadeComplementar> findByEstudanteAndNaturezaAndCategoria(
                        Usuario estudante, Natureza natureza, Categoria categoria);

        Optional<AtividadeComplementar> findByIdAndEstudante(Long id, Usuario estudante);

        default List<AtividadeComplementar> findByEstudanteComFiltros(
                        Usuario estudante, Natureza natureza, Categoria categoria) {
                if (natureza != null && categoria != null) {
                        return findByEstudanteAndNaturezaAndCategoria(estudante, natureza, categoria);
                } else if (natureza != null) {
                        return findByEstudanteAndNatureza(estudante, natureza);
                } else if (categoria != null) {
                        return findByEstudanteAndCategoria(estudante, categoria);
                } else {
                        return findByEstudante(estudante);
                }
        }
}