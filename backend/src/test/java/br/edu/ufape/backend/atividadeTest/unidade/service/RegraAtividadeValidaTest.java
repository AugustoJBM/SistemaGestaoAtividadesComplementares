package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.service.RegraAtividadeValida;
import br.edu.ufape.backend.usuario.model.Estudante;

class RegraAtividadeValidaTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    private AtividadeComplementar criarAtividade() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        return new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                10,
                Natureza.ACC,
                Categoria.PESQUISA,
                null,
                estudante);
    }

    @Test
    @DisplayName("Atividade existente e considerada valida")
    void atividadeExistenteEConsideradaValida() {
        AtividadeComplementar atividade = criarAtividade();

        assertTrue(RegraAtividadeValida.isValida(atividade));
    }

    @Test
    @DisplayName("Atividade nula e considerada invalida")
    void atividadeNulaEConsideradaInvalida() {
        assertFalse(RegraAtividadeValida.isValida(null));
    }
}
