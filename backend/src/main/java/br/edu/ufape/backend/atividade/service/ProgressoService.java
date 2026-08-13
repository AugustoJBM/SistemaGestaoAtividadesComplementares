package br.edu.ufape.backend.atividade.service;

import org.springframework.stereotype.Service;

import br.edu.ufape.backend.atividade.config.ProgressoProperties;
import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponse;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;

@Service
public class ProgressoService {

    private static final String MENSAGEM_ACESSO_NEGADO = "Apenas estudantes podem consultar o progresso de atividades.";

    private final UsuarioContrato usuarioContrato;
    private final AtividadeComplementarRepository atividadeComplementarRepository;
    private final ProgressoProperties progressoProperties;

    public ProgressoService(
            UsuarioContrato usuarioContrato,
            AtividadeComplementarRepository atividadeComplementarRepository,
            ProgressoProperties progressoProperties) {
        this.usuarioContrato = usuarioContrato;
        this.atividadeComplementarRepository = atividadeComplementarRepository;
        this.progressoProperties = progressoProperties;
    }

    public ProgressoResponse obterProgresso(String emailEstudante) {
        Usuario usuario = usuarioContrato.buscarPorEmail(emailEstudante)
                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));

        if (!(usuario instanceof Estudante estudante)) {
            throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
        }

        ProgressoModalidadeResponse acc = new ProgressoModalidadeResponse(
                calcularHorasAcumuladas(estudante, Natureza.ACC),
                progressoProperties.getAcc().getHorasExigidas());

        ProgressoModalidadeResponse acex = new ProgressoModalidadeResponse(
                calcularHorasAcumuladas(estudante, Natureza.ACEX),
                progressoProperties.getAcex().getHorasExigidas());

        return new ProgressoResponse(acc, acex);
    }

    private int calcularHorasAcumuladas(Estudante estudante, Natureza natureza) {
        return atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, natureza).stream()
                .filter(RegraAtividadeValida::isValida)
                .mapToInt(atividade -> atividade.getCargaHorariaEmHoras())
                .sum();
    }
}