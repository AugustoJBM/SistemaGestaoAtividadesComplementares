package br.edu.ufape.backend.autenticacao.service;

import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Usuario;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UsuarioContrato usuarioContrato;

	public CustomUserDetailsService(UsuarioContrato usuarioContrato) {
		this.usuarioContrato = usuarioContrato;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuario = usuarioContrato.buscarPorEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

		return User.withUsername(usuario.getEmail()).password(usuario.getSenhaHash())
				.authorities(Collections.singletonList(() -> "ROLE_" + usuario.getRole().name())).build();
	}
}
