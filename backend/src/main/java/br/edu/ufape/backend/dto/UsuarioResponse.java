package br.edu.ufape.backend.dto;

import br.edu.ufape.backend.model.Usuario;
import br.edu.ufape.backend.model.Role;

public class UsuarioResponse {
    
    private final Long id;
    private final String nome;
    private final String email;
    private final Role role;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
