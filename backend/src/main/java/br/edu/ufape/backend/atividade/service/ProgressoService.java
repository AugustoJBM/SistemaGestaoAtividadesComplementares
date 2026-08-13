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
    private static final String MENSAGEM_ACESSO_NEGADO =
            "Apenas estudantes podem consultar o progresso de atividades.";

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
        Estudante estudante = obterEstudante(emailEstudante);
        List<AtividadeComplementar> atividades =
                atividadeRepository.findByEstudante(estudante);

        ProgressoModalidadeResponse acc =
                criarProgresso(atividades, Natureza.ACC, horasExigidasAcc);
        ProgressoModalidadeResponse acex =
                criarProgresso(atividades, Natureza.ACEX, horasExigidasAcex);

        return new ProgressoResponse(acc, acex);
    }

    private Estudante obterEstudante(String email) {
        Usuario usuario = usuarioContrato.buscarPorEmail(email)
                .orElseThrow(() ->
                        new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));
        if (!(usuario instanceof Estudante estudante)) {
            throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
        }
        return estudante;
    }

        ProgressoModalidadeResponse acc = new ProgressoModalidadeResponse(
                calcularHorasAcumuladas(estudante, Natureza.ACC),
                progressoProperties.getAcc().getHorasExigidas());

        ProgressoModalidadeResponse acex = new ProgressoModalidadeResponse(
                calcularHorasAcumuladas(estudante, Natureza.ACEX),
                progressoProperties.getAcex().getHorasExigidas());

        // Enquanto não houver fluxo de aprovação/deferimento por um avaliador,
        // as horas cadastradas ficam em "horasPendentes" (Em Análise)
        int horasAcumuladas = 0; // Horas efetivamente homologadas/aprovadas
        int horasPendentes = horasCadastradas; // Horas reais enviadas pelo aluno

    private int calcularHorasAcumuladas(Estudante estudante, Natureza natureza) {
        return atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, natureza).stream()
                .filter(RegraAtividadeValida::isValida)
                .mapToInt(atividade -> atividade.getCargaHorariaEmHoras())
                .sum();
    }
}