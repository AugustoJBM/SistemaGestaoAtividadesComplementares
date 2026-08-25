package br.edu.ufape.backend.usuario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;

@Component
@Profile("dev")
@Order(1)
public class UsuarioDataInitializerService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDataInitializerService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDataInitializerService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Inicializando contas de teste no ambiente de desenvolvimento...");
        String senhaHashPadrao = passwordEncoder.encode("senha1234");

        // 1. Avaliador
        criarAvaliadorSeNaoExistir("avaliador@ufape.edu.br", senhaHashPadrao);

        // 2. Administrador
        criarAdminSeNaoExistir("admin@ufape.edu.br", senhaHashPadrao);

        // 3. Estudantes
        criarEstudanteSeNaoExistir("aluno1@ufape.edu.br", "Lucas Gabriel Silva", "2026000001", senhaHashPadrao);
        criarEstudanteSeNaoExistir("aluno2@ufape.edu.br", "Beatriz Lima Santos", "2026000002", senhaHashPadrao);
    }

    private void criarAvaliadorSeNaoExistir(String email, String senhaHash) {
        if (!usuarioRepository.existsByEmailIgnoreCase(email)) {
            Avaliador avaliador = new Avaliador(
                    "Professor Avaliador", email.toLowerCase(), senhaHash, "REG-UFAPE-01", "Ciência da Computação");
            usuarioRepository.save(avaliador);
            log.info("Conta de teste (Avaliador) criada: {}", email);
        }
    }

    private void criarAdminSeNaoExistir(String email, String senhaHash) {
        if (!usuarioRepository.existsByEmailIgnoreCase(email)) {
            Administrador admin = new Administrador(
                    "Administrador Geral", email.toLowerCase(), senhaHash, "TOTALPODER", "Coordenação Acadêmica");
            usuarioRepository.save(admin);
            log.info("Conta de teste (Administrador) criada: {}", email);
        }
    }

    private void criarEstudanteSeNaoExistir(String email, String nome, String matricula, String senhaHash) {
        if (!usuarioRepository.existsByEmailIgnoreCase(email)) {
            Estudante estudante = new Estudante(
                    nome, email.toLowerCase(), senhaHash, matricula, "Bacharelado em Ciência da Computação");
            usuarioRepository.save(estudante);
            log.info("Conta de teste (Estudante) criada: {}", email);
        }
    }
}