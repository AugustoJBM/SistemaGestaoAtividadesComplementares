package br.edu.ufape.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.dto.UsuarioResponse;
import br.edu.ufape.backend.model.Usuario;
import br.edu.ufape.backend.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        Usuario usuario = usuarioService.cadastrar(
            request.getNome(),
            request.getEmail(),
            request.getSenha(),
            request.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponse(usuario));
    }
}
