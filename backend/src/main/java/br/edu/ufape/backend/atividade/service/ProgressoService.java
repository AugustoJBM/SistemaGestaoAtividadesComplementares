package br.edu.ufape.backend.atividade.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponse;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
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
    private final AtividadeComplementarRepository atividadeRepository;
    private final int horasExigidasAcc;
    private final int horasExigidasAcex;

    public ProgressoService(
            UsuarioContrato usuarioContrato,
            AtividadeComplementarRepository atividadeRepository,
            @Value("${sgac.progresso.acc.horas-exigidas:200}") int horasExigidasAcc,
            @Value("${sgac.progresso.acex.horas-exigidas:100}") int horasExigidasAcex) {
        this.usuarioContrato = usuarioContrato;
        this.atividadeRepository = atividadeRepository;
        this.horasExigidasAcc = horasExigidasAcc;
        this.horasExigidasAcex = horasExigidasAcex;
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

    private ProgressoModalidadeResponse criarProgresso(
            List<AtividadeComplementar> atividades,
            Natureza natureza,
            int horasExigidas) {
        // Soma as horas reais das atividades cadastradas pelo aluno no banco de dados
        int horasCadastradas = calcularHoras(atividades, natureza);

        // Enquanto não houver fluxo de aprovação/deferimento por um avaliador,
        // as horas cadastradas ficam em "horasPendentes" (Em Análise)
        int horasAcumuladas = 0; // Horas efetivamente homologadas/aprovadas
        int horasPendentes = horasCadastradas; // Horas reais enviadas pelo aluno

        return new ProgressoModalidadeResponse(
                horasAcumuladas,
                horasPendentes,
                horasExigidas
        );
    }

    private int calcularHoras(
            List<AtividadeComplementar> atividades,
            Natureza natureza) {
        if (atividades == null || atividades.isEmpty()) {
            return 0;
        }
        return atividades.stream()
                .filter(atividade -> atividade.getNatureza() == natureza)
                .mapToInt(atividade -> atividade != null ? atividade.getCargaHorariaEmHoras() : 0)
                .sum();
    }
}