package br.edu.ufape.backend.atividadeTest.integracao.repository;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AtividadeComplementarRepositoryTest {

    @Autowired
    private AtividadeComplementarRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Certificado certificadoPadrao() {
        return new Certificado("certificado.pdf", "application/pdf", 1024L, "/uploads/certificado.pdf");
    }

    private Estudante salvarEstudante(String email) {
        return (Estudante) usuarioRepository.save(new Estudante("Estudante Teste", email, "hash"));
    }

    private AtividadeComplementar salvarAtividade(Estudante estudante, Natureza natureza, Categoria categoria) {
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Atividade de teste",
                "UFAPE",
                LocalDate.now(),
                10,
                natureza,
                categoria,
                certificadoPadrao(),
                estudante);
        return repository.save(atividade);
    }

    @Test
    @DisplayName("Estudante sem atividades retorna lista vazia")
    void estudanteSemAtividadesRetornaListaVazia() {
        // Arrange
        Estudante estudante = salvarEstudante("sem.atividades@ufape.edu.br");

        // Act
        List<AtividadeComplementar> resultado = repository.findByEstudanteComFiltros(estudante, null, null);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Retorna todas as atividades do estudante quando nenhum filtro for passado")
    void retornaTodasAtividadesQuandoNenhumFiltroForPassado() {
        // Arrange
        Estudante estudante = salvarEstudante("todas.atividades@ufape.edu.br");
        salvarAtividade(estudante, Natureza.ACC, Categoria.PESQUISA);
        salvarAtividade(estudante, Natureza.ACEX, Categoria.EXTENSAO);
        salvarAtividade(estudante, Natureza.ACC, Categoria.EVENTOS);

        // Act
        List<AtividadeComplementar> resultado = repository.findByEstudanteComFiltros(estudante, null, null);

        // Assert
        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("Filtra corretamente passando apenas a Natureza")
    void filtraCorretamentePassandoApenasNatureza() {
        // Arrange
        Estudante estudante = salvarEstudante("filtro.natureza@ufape.edu.br");
        salvarAtividade(estudante, Natureza.ACC, Categoria.PESQUISA);
        salvarAtividade(estudante, Natureza.ACC, Categoria.EXTENSAO);
        salvarAtividade(estudante, Natureza.ACEX, Categoria.ENSINO);

        // Act
        List<AtividadeComplementar> resultado = repository.findByEstudanteComFiltros(estudante, Natureza.ACC, null);

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> a.getNatureza() == Natureza.ACC));
    }

    @Test
    @DisplayName("Filtra corretamente passando Natureza e Categoria")
    void filtraCorretamentePassandoNaturezaECategoria() {
        // Arrange
        Estudante estudante = salvarEstudante("filtro.natureza.categoria@ufape.edu.br");
        salvarAtividade(estudante, Natureza.ACC, Categoria.PESQUISA);
        salvarAtividade(estudante, Natureza.ACC, Categoria.EXTENSAO);
        salvarAtividade(estudante, Natureza.ACEX, Categoria.PESQUISA);

        // Act
        List<AtividadeComplementar> resultado = repository.findByEstudanteComFiltros(
                estudante, Natureza.ACC, Categoria.PESQUISA);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(Natureza.ACC, resultado.get(0).getNatureza());
        assertEquals(Categoria.PESQUISA, resultado.get(0).getCategoria());
    }

    @Test
    @DisplayName("Busca por ID de atividade pertencente a outro estudante retorna vazio")
    void buscaPorIdDeAtividadeDeOutroEstudanteRetornaVazio() {
        // Arrange
        Estudante estudanteDono = salvarEstudante("dono.atividade@ufape.edu.br");
        Estudante outroEstudante = salvarEstudante("outro.estudante@ufape.edu.br");
        AtividadeComplementar atividade = salvarAtividade(estudanteDono, Natureza.ACC, Categoria.PESQUISA);

        // Act
        Optional<AtividadeComplementar> resultado = repository.findByIdAndEstudante(
                atividade.getId(), outroEstudante);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
