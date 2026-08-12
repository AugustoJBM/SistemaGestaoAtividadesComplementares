package br.edu.ufape.backend.atividade.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponse;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;

@Service
public class ProgressoService {

    private static final String MENSAGEM_ACESSO_NEGADO = "Apenas estudantes podem consultar o progresso de atividades.";

    private final UsuarioContrato usuarioContrato;

    private final int horasExigidasAcc;
    private final int horasExigidasAcex;

    public ProgressoService(
            UsuarioContrato usuarioContrato,
            @Value("${sgac.progresso.acc.horas-exigidas:200}") int horasExigidasAcc,
            @Value("${sgac.progresso.acex.horas-exigidas:100}") int horasExigidasAcex) {
        this.usuarioContrato = usuarioContrato;
        this.horasExigidasAcc = horasExigidasAcc;
        this.horasExigidasAcex = horasExigidasAcex;
    }

    public ProgressoResponse obterProgresso(String emailEstudante) {
        Usuario usuario = usuarioContrato.buscarPorEmail(emailEstudante)
                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));

        if (!(usuario instanceof Estudante estudante)) {
            throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
        }

        ProgressoModalidadeResponse acc = new ProgressoModalidadeResponse(
                calcularHorasAcumuladasAcc(estudante), horasExigidasAcc);
        ProgressoModalidadeResponse acex = new ProgressoModalidadeResponse(
                calcularHorasAcumuladasAcex(estudante), horasExigidasAcex);

        return new ProgressoResponse(acc, acex);
    }

    private int calcularHorasAcumuladasAcc(Estudante estudante) {
        return 0;
    }

    private int calcularHorasAcumuladasAcex(Estudante estudante) {
        return 0;
    }
}
