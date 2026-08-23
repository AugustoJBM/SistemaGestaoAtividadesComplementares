package br.edu.ufape.backend.autenticacao.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.autenticacao.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.autenticacao.exception.UnauthorizedException;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.usuario.model.Usuario;

@Service
public class AuthService {

    private final UsuarioContrato usuarioContrato;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UsuarioContrato usuarioContrato,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            TokenBlacklistService tokenBlacklistService) {
        this.usuarioContrato = usuarioContrato;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public Usuario cadastrarUsuario(CadastroUsuarioRequest request) {
        if (usuarioContrato.existePorEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        if (request.getRole() != null && request.getRole() != Role.ESTUDANTE) {
            throw new PerfilNaoPermitidoException();
        }

        String senhaHash = passwordEncoder.encode(request.getSenha());

        Estudante estudante = new Estudante(request.getNome(), request.getEmail(), senhaHash);

        return usuarioContrato.salvar(estudante);
    }

    public LoginResponse login(LoginRequest request) {
        String emailTratado = request.getUsuario() != null ? request.getUsuario().trim().toLowerCase() : "";

        Usuario usuario = usuarioContrato.buscarPorEmail(emailTratado)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(usuario.getEmail(), usuario.getRole().name());
        return new LoginResponse(token, "Bearer");
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token ausente ou inválido");
        }
        String token = authorizationHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            throw new UnauthorizedException("Token inválido ou expirado");
        }
        tokenBlacklistService.blacklistToken(token);
    }
}
