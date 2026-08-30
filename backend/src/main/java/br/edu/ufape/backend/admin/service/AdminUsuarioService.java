package br.edu.ufape.backend.admin.service;

import br.edu.ufape.backend.admin.dto.CadastroInstitucionalRequestDTO;
import br.edu.ufape.backend.admin.dto.UsuarioAdminResponseDTO;
import br.edu.ufape.backend.autenticacao.exception.EmailJaCadastradoException;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.usuario.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUsuarioService {

	private final UsuarioContrato usuarioContrato;
	private final PasswordEncoder passwordEncoder;

	public AdminUsuarioService(UsuarioContrato usuarioContrato, PasswordEncoder passwordEncoder) {
		this.usuarioContrato = usuarioContrato;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UsuarioAdminResponseDTO cadastrarUsuarioInstitucional(CadastroInstitucionalRequestDTO request) {
		String emailTratado = request.email().trim().toLowerCase();
		if (usuarioContrato.existePorEmail(emailTratado)) {
			throw new EmailJaCadastradoException(emailTratado);
		}

		String senhaHash = passwordEncoder.encode(request.senha());
		Usuario novoUsuario;

		if (request.role() == Role.AVALIADOR) {
			novoUsuario = new Avaliador(request.nome(), emailTratado, senhaHash,
					request.registro() != null ? request.registro() : "REG-" + System.currentTimeMillis() % 10000,
					request.areaAtuacao() != null ? request.areaAtuacao() : "Ciência da Computação");
		} else if (request.role() == Role.ADMINISTRADOR) {
			novoUsuario = new Administrador(request.nome(), emailTratado, senhaHash,
					request.nivelAcesso() != null ? request.nivelAcesso() : "TOTAL",
					request.setor() != null ? request.setor() : "Coordenação Geral");
		} else {
			throw new IllegalArgumentException(
					"Apenas perfis de AVALIADOR ou ADMINISTRADOR podem ser cadastrados neste painel.");
		}

		Usuario salvo = usuarioContrato.salvar(novoUsuario);
		return UsuarioAdminResponseDTO.fromEntity(salvo);
	}

	@Transactional(readOnly = true)
	public List<UsuarioAdminResponseDTO> listarUsuarios(Role role, Boolean ativo) {
		return usuarioContrato.listarTodos().stream().filter(u -> role == null || u.getRole() == role)
				.filter(u -> ativo == null || u.getIsActive() == ativo).map(UsuarioAdminResponseDTO::fromEntity)
				.toList();
	}

	@Transactional
	public UsuarioAdminResponseDTO alternarStatusUsuario(Long id) {
		Usuario usuario = usuarioContrato.buscarPorId(id)
				.orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));

		usuario.setIsActive(!usuario.getIsActive());
		Usuario atualizado = usuarioContrato.salvar(usuario);
		return UsuarioAdminResponseDTO.fromEntity(atualizado);
	}
}
