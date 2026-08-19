package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class AtividadeComplementarServiceTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private UsuarioContrato usuarioContrato;

    @Mock
    private AtividadeComplementarRepository atividadeRepository;

    @Mock
    private ArmazenamentoCertificadoService armazenamentoCertificadoService;

    @InjectMocks
    private AtividadeComplementarService service;

    private AtividadeComplementar criarAtividade(Natureza natureza, Categoria categoria, Estudante estudante) {
        return new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                natureza,
                categoria,
                null,
                estudante);
    }

    @Test
    @DisplayName("Estudante sem atividades retorna lista vazia")
    void estudanteSemAtividadesRetornaListaVazia() {
        // Arrange
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Pageable pageable = PageRequest.of(0, 20);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of()));

        // Act
        Page<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(EMAIL, null, null, pageable);

        // Assert
        assertTrue(resultado.isEmpty());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, null, null, pageable);
    }

    @Test
    @DisplayName("Estudante com atividades retorna apenas as atividades dele")
    void estudanteComAtividadesRetornaApenasAtividadesDele() {
        // Arrange
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Pageable pageable = PageRequest.of(0, 20);
        AtividadeComplementar atividade1 = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        AtividadeComplementar atividade2 = criarAtividade(Natureza.ACEX, Categoria.EXTENSAO, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(atividade1, atividade2)));

        // Act
        Page<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(EMAIL, null, null, pageable);

        // Assert
        assertEquals(2, resultado.getContent().size());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, null, null, pageable);
    }

    @Test
    @DisplayName("Filtro apenas por Natureza funciona corretamente")
    void filtroApenasPorNaturezaFuncionaCorretamente() {
        // Arrange
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Pageable pageable = PageRequest.of(0, 20);
        AtividadeComplementar atividadeAcc = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, Natureza.ACC, null, pageable))
                .thenReturn(new PageImpl<>(List.of(atividadeAcc)));

        // Act
        Page<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(
                EMAIL, Natureza.ACC, null, pageable);

        // Assert
        assertEquals(1, resultado.getContent().size());
        assertEquals(Natureza.ACC, resultado.getContent().get(0).getNatureza());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, Natureza.ACC, null, pageable);
    }

    @Test
    @DisplayName("Filtro por Natureza e Categoria funciona corretamente")
    void filtroPorNaturezaECategoriaFuncionaCorretamente() {
        // Arrange
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        Pageable pageable = PageRequest.of(0, 20);
        AtividadeComplementar atividade = criarAtividade(Natureza.ACC, Categoria.PESQUISA, estudante);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeRepository.findByEstudanteComFiltros(estudante, Natureza.ACC, Categoria.PESQUISA, pageable))
                .thenReturn(new PageImpl<>(List.of(atividade)));

        // Act
        Page<AtividadeComplementar> resultado = service.listarAtividadesDoEstudante(
                EMAIL, Natureza.ACC, Categoria.PESQUISA, pageable);

        // Assert
        assertEquals(1, resultado.getContent().size());
        assertEquals(Natureza.ACC, resultado.getContent().get(0).getNatureza());
        assertEquals(Categoria.PESQUISA, resultado.getContent().get(0).getCategoria());
        verify(atividadeRepository).findByEstudanteComFiltros(estudante, Natureza.ACC, Categoria.PESQUISA, pageable);
    }

    @Test
    @DisplayName("Usuario avaliador lanca AcessoNegadoAtividadeException")
    void usuarioAvaliadorLancaAcessoNegadoAtividadeException() {
        // Arrange
        Avaliador avaliador = new Avaliador("Avaliador", EMAIL, "hash", "REG-1", "Extensao");
        Pageable pageable = PageRequest.of(0, 20);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(avaliador));

        // Act & Assert
        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.listarAtividadesDoEstudante(EMAIL, null, null, pageable));
    }

    @Test
    @DisplayName("E-mail inexistente lanca AcessoNegadoAtividadeException")
    void emailInexistenteLancaAcessoNegadoAtividadeException() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> service.listarAtividadesDoEstudante(EMAIL, null, null, pageable));
    }
}
