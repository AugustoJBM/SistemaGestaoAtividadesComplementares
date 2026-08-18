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
import java.util.Objects;

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
                Estudante estudante = obterEstudante(emailEstudante);

                // Enquanto não houver fluxo de aprovação/deferimento por um avaliador,
                // as horas cadastradas ficam em "horasPendentes" (Em Análise) e horasAcumuladas
                // permanece 0.
                int horasCadastradasAcc = calcularHorasCadastradas(estudante, Natureza.ACC);
                int horasCadastradasAcex = calcularHorasCadastradas(estudante, Natureza.ACEX);

                ProgressoModalidadeResponse acc = new ProgressoModalidadeResponse(
                                0,
                                horasCadastradasAcc,
                                progressoProperties.getAcc().getHorasExigidas());

                ProgressoModalidadeResponse acex = new ProgressoModalidadeResponse(
                                0,
                                horasCadastradasAcex,
                                progressoProperties.getAcex().getHorasExigidas());

                return new ProgressoResponse(acc, acex);
        }

        private Estudante obterEstudante(String email) {
                Usuario usuario = usuarioContrato.buscarPorEmail(email)
                                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));

                if (!(usuario instanceof Estudante estudante)) {
                        throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
                }

                return estudante;
        }

        private int calcularHorasCadastradas(Estudante estudante, Natureza natureza) {
                return atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, natureza).stream()
                                .filter(RegraAtividadeValida::isValida)
                                .filter(Objects::nonNull)
                                .mapToInt(a -> a.getCargaHorariaEmHoras())
                                .sum();
        }
}