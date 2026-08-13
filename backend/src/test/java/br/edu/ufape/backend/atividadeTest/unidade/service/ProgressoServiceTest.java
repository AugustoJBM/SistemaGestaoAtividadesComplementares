package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.service.ProgressoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class ProgressoServiceTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private UsuarioContrato usuarioContrato;

    private ProgressoService criarService() {
        return new ProgressoService(usuarioContrato, null, 200, 100);
    }

    @Test
    @DisplayName("Deve montar o progresso de ACC e ACEX com as horas exigidas recebidas por construtor")
    void deveMontarProgressoParaEstudante() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));

        ProgressoResponse progresso = criarService().obterProgresso(EMAIL);

        assertEquals(200, progresso.getAcc().getHorasExigidas());
        assertEquals(100, progresso.getAcex().getHorasExigidas());
        assertEquals(0, progresso.getAcc().getHorasAcumuladas());
        assertEquals(0, progresso.getAcex().getHorasAcumuladas());
        assertEquals(0, progresso.getAcc().getPercentualConcluido());
        assertEquals(0, progresso.getAcex().getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve negar acesso com mensagem propria do contexto de atividades quando o usuario nao for estudante")
    void deveNegarAcessoQuandoUsuarioNaoForEstudante() {
        Avaliador avaliador = new Avaliador("Avaliador", EMAIL, "hash", "REG-1", "Extensao");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(avaliador));

        AcessoNegadoAtividadeException excecao = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> criarService().obterProgresso(EMAIL));

        assertEquals("Apenas estudantes podem consultar o progresso de atividades.", excecao.getMessage());
        // Regressao: a mensagem do cadastro publico nao pode vazar por este fluxo.
        assertFalse(excecao.getMessage().contains("cadastro público"));
    }

    @Test
    @DisplayName("Deve negar acesso quando o usuario do token nao existir mais")
    void deveNegarAcessoQuandoUsuarioNaoExistir() {
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.empty());

        AcessoNegadoAtividadeException excecao = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> criarService().obterProgresso(EMAIL));

        assertEquals("Apenas estudantes podem consultar o progresso de atividades.", excecao.getMessage());
    }
}
