package br.edu.ufape.backend.atividade.service;

import org.springframework.stereotype.Service;
import br.edu.ufape.backend.atividade.config.ProgressoProperties;
import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;

import java.util.List;
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

        public ProgressoResponseDTO obterProgresso(String emailEstudante) {
                Estudante estudante = obterEstudante(emailEstudante);

                int horasAprovadasAcc = calcularHoras(estudante, Natureza.ACC, true);
                int horasPendentesAcc = calcularHoras(estudante, Natureza.ACC, false);

                int horasAprovadasAcex = calcularHoras(estudante, Natureza.ACEX, true);
                int horasPendentesAcex = calcularHoras(estudante, Natureza.ACEX, false);

                ProgressoModalidadeResponseDTO acc = new ProgressoModalidadeResponseDTO(
                                horasAprovadasAcc,
                                horasPendentesAcc,
                                progressoProperties.getAcc().getHorasExigidas());
                ProgressoModalidadeResponseDTO acex = new ProgressoModalidadeResponseDTO(
                                horasAprovadasAcex,
                                horasPendentesAcex,
                                progressoProperties.getAcex().getHorasExigidas());

                return new ProgressoResponseDTO(acc, acex);
        }

        private Estudante obterEstudante(String email) {
                Usuario usuario = usuarioContrato.buscarPorEmail(email)
                                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));
                if (!(usuario instanceof Estudante estudante)) {
                        throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
                }
                return estudante;
        }

        private int calcularHoras(Estudante estudante, Natureza natureza, boolean aprovadas) {
                List<AtividadeComplementar> atividades = atividadeComplementarRepository
                                .findByEstudanteAndNatureza(estudante, natureza);
                return atividades.stream()
                                .filter(Objects::nonNull)
                                .filter(a -> aprovadas ? RegraAtividadeValida.isAprovada(a)
                                                : RegraAtividadeValida.isPendente(a))
                                .mapToInt(a -> a.getCargaHorariaEmHoras() != null ? a.getCargaHorariaEmHoras() : 0)
                                .sum();
        }
}