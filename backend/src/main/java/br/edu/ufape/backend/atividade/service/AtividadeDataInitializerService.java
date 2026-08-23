package br.edu.ufape.backend.atividade.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;

@Component
@Profile("dev")
@Order(2)
public class AtividadeDataInitializerService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AtividadeDataInitializerService.class);

    private final AtividadeComplementarRepository atividadeRepository;
    private final UsuarioContrato usuarioContrato;
    private final Random random = new Random();

    private final List<String> cursos = List.of(
            "Minicurso de Python para Análise de Dados",
            "Monitoria Acadêmica de Algoritmos e Programação",
            "Workshop de Desenvolvimento Web com Angular",
            "Iniciação Científica em Inteligência Artificial (PIBIC)",
            "Ação Comunitária de Extensão Tecnológica",
            "Seminário Regional de Engenharia de Software"
    );

    private final List<String> instituicoes = List.of(
            "UFAPE", "UFRPE", "Sebrae Pernambuco", "Coursera", "Alura", "Fundação Bradesco"
    );

    public AtividadeDataInitializerService(
            AtividadeComplementarRepository atividadeRepository,
            UsuarioContrato usuarioContrato) {
        this.atividadeRepository = atividadeRepository;
        this.usuarioContrato = usuarioContrato;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (atividadeRepository.count() > 0) {
            return;
        }

        log.info("Inicializando atividades de desenvolvimento...");

        for (int i = 1; i <= 5; i++) {
            String email = "aluno" + i + "@ufape.edu.br";
            var usuarioOpt = usuarioContrato.buscarPorEmail(email);

            if (usuarioOpt.isPresent() && usuarioOpt.get() instanceof Estudante estudante) {
                int totalAtividades = 2 + random.nextInt(3);
                for (int j = 0; j < totalAtividades; j++) {
                    Natureza natureza = (j % 2 == 0) ? Natureza.ACC : Natureza.ACEX;
                    Categoria categoria = Categoria.values()[random.nextInt(Categoria.values().length)];
                    int horas = (random.nextInt(8) + 1) * 5;

                    Certificado cert = new Certificado(
                            "certificado_" + (j + 1) + ".pdf",
                            "application/pdf",
                            1024L * 150,
                            "/uploads/fake_cert.pdf"
                    );

                    String titulo = cursos.get((i + j) % cursos.size());
                    String instituicao = instituicoes.get(random.nextInt(instituicoes.size()));

                    AtividadeComplementar atividade = new AtividadeComplementar(
                            titulo,
                            instituicao,
                            LocalDate.now().minusDays(random.nextInt(180) + 1),
                            horas,
                            natureza,
                            categoria,
                            cert,
                            estudante
                    );
                    atividade.setStatus(j == 0 ? StatusAtividade.APROVADA : StatusAtividade.PENDENTE);
                    atividadeRepository.save(atividade);
                }
            }
        }

        log.info("Atividades iniciais criadas com sucesso.");
    }
}