package br.edu.ufape.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ufape.backend.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.dto.LoginRequest;
import br.edu.ufape.backend.dto.LoginResponse;
import br.edu.ufape.backend.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.exception.PerfilNaoPermitidoException;
import br.edu.ufape.backend.exception.UnauthorizedException;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.service.UsuarioService;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(UsuarioService usuarioService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            TokenBlacklistService tokenBlacklistService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public Usuario cadastrarUsuario(CadastroUsuarioRequest request) {
        if (usuarioService.existePorEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        if (request.getRole() != null && request.getRole() != Role.ESTUDANTE) {
            throw new PerfilNaoPermitidoException();
        }

        String senhaHash = passwordEncoder.encode(request.getSenha());

        Estudante estudante = new Estudante(request.getNome(), request.getEmail(), senhaHash);

        return usuarioService.salvar(estudante);
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getUsuario())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(usuario.getEmail());
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
