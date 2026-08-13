package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.config.ProgressoProperties;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.service.ProgressoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class ProgressoServiceTest {

    private static final String EMAIL = "estudante@ufape.edu.br";

    @Mock
    private UsuarioContrato usuarioContrato;

    @Mock
    private AtividadeComplementarRepository atividadeComplementarRepository;

    private ProgressoProperties criarProperties(int horasAcc, int horasAcex) {
        ProgressoProperties properties = new ProgressoProperties();
        properties.getAcc().setHorasExigidas(horasAcc);
        properties.getAcex().setHorasExigidas(horasAcex);
        return properties;
    }

    private ProgressoService criarService(int horasAcc, int horasAcex) {
        return new ProgressoService(usuarioContrato, atividadeComplementarRepository,
                criarProperties(horasAcc, horasAcex));
    }

    private AtividadeComplementar criarAtividade(Natureza natureza, int cargaHoraria, Estudante estudante) {
        return new AtividadeComplementar(
                "Atividade de teste",
                "Instituicao",
                LocalDate.now(),
                cargaHoraria,
                natureza,
                Categoria.EXTENSAO,
                null,
                estudante);
    }

    @Test
    @DisplayName("Estudante sem atividades deve ter 0h e 0% em ambas as modalidades")
    void deveRetornarZeroQuandoEstudanteNaoTemAtividades() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(any(), any()))
                .thenReturn(List.of());

        ProgressoResponse progresso = criarService(200, 100).obterProgresso(EMAIL);

        assertEquals(0, progresso.getAcc().getHorasAcumuladas());
        assertEquals(0, progresso.getAcc().getPercentualConcluido());
        assertEquals(0, progresso.getAcex().getHorasAcumuladas());
        assertEquals(0, progresso.getAcex().getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve somar as horas das atividades e calcular percentual parcial")
    void deveCalcularProgressoParcial() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACC))
                .thenReturn(List.of(
                        criarAtividade(Natureza.ACC, 30, estudante),
                        criarAtividade(Natureza.ACC, 20, estudante)));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACEX))
                .thenReturn(List.of(criarAtividade(Natureza.ACEX, 10, estudante)));

        ProgressoResponse progresso = criarService(200, 100).obterProgresso(EMAIL);

        assertEquals(50, progresso.getAcc().getHorasAcumuladas());
        assertEquals(25, progresso.getAcc().getPercentualConcluido());
        assertEquals(10, progresso.getAcex().getHorasAcumuladas());
        assertEquals(10, progresso.getAcex().getPercentualConcluido());
    }

    @Test
    @DisplayName("Percentual deve ficar limitado a 100% quando o estudante ultrapassa o exigido")
    void deveLimitarPercentualA100QuandoUltrapassarExigido() {
        Estudante estudante = new Estudante("Estudante", EMAIL, "hash");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(estudante));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACC))
                .thenReturn(List.of(criarAtividade(Natureza.ACC, 250, estudante)));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACEX))
                .thenReturn(List.of());

        ProgressoResponse progresso = criarService(200, 100).obterProgresso(EMAIL);

        assertEquals(250, progresso.getAcc().getHorasAcumuladas());
        assertEquals(100, progresso.getAcc().getPercentualConcluido());
    }

    @Test
    @DisplayName("Deve negar acesso com mensagem propria do contexto de atividades quando o usuario nao for estudante")
    void deveNegarAcessoQuandoUsuarioNaoForEstudante() {
        Avaliador avaliador = new Avaliador("Avaliador", EMAIL, "hash", "REG-1", "Extensao");
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.of(avaliador));

        AcessoNegadoAtividadeException excecao = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> criarService(200, 100).obterProgresso(EMAIL));

        assertEquals("Apenas estudantes podem consultar o progresso de atividades.", excecao.getMessage());
        assertFalse(excecao.getMessage().contains("cadastro público"));
    }

    @Test
    @DisplayName("Deve negar acesso quando o usuario do token nao existir mais")
    void deveNegarAcessoQuandoUsuarioNaoExistir() {
        when(usuarioContrato.buscarPorEmail(EMAIL)).thenReturn(Optional.empty());

        AcessoNegadoAtividadeException excecao = assertThrows(
                AcessoNegadoAtividadeException.class,
                () -> criarService(200, 100).obterProgresso(EMAIL));

        assertEquals("Apenas estudantes podem consultar o progresso de atividades.", excecao.getMessage());
    }
}