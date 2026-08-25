package br.edu.ufape.backend.usuario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;

@Component
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
        String senhaHash = passwordEncoder.encode("senha1234");

        // 1. Avaliador
        salvarOuAtualizarAvaliador("avaliador@ufape.edu.br", senhaHash);

        // 2. Administrador
        salvarOuAtualizarAdmin("admin@ufape.edu.br", senhaHash);

        // 3. Estudantes
        salvarOuAtualizarEstudante("aluno1@ufape.edu.br", "Lucas Gabriel Silva", "2026000001", senhaHash);
        salvarOuAtualizarEstudante("aluno2@ufape.edu.br", "Beatriz Lima Santos", "2026000002", senhaHash);
    }

    private void salvarOuAtualizarAvaliador(String email, String senhaHash) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (usuario == null) {
            usuario = new Avaliador("Professor Avaliador", email.toLowerCase(), senhaHash, "REG-UFAPE-01", "Ciência da Computação");
        } else {
            usuario.setSenhaHash(senhaHash);
            usuario.setIsActive(true);
        }
        usuarioRepository.save(usuario);
        log.info(">>> CONTA PRONTA: {} | Senha: senha1234", email);
    }

    private void salvarOuAtualizarAdmin(String email, String senhaHash) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (usuario == null) {
            usuario = new Administrador("Administrador Geral", email.toLowerCase(), senhaHash, "TOTALPODER", "Coordenação Acadêmica");
        } else {
            usuario.setSenhaHash(senhaHash);
            usuario.setIsActive(true);
        }
        usuarioRepository.save(usuario);
        log.info(">>> CONTA PRONTA: {} | Senha: senha1234", email);
    }

    private void salvarOuAtualizarEstudante(String email, String nome, String matricula, String senhaHash) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email).orElse(null);
        if (usuario == null) {
            usuario = new Estudante(nome, email.toLowerCase(), senhaHash, matricula, "Bacharelado em Ciência da Computação");
        } else {
            usuario.setSenhaHash(senhaHash);
            usuario.setIsActive(true);
        }
        usuarioRepository.save(usuario);
        log.info(">>> CONTA PRONTA: {} | Senha: senha1234", email);
    }
}