package br.edu.ufape.backend.notificacao.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.backend.notificacao.dto.ContagemNaoLidasResponseDTO;
import br.edu.ufape.backend.notificacao.dto.NotificacaoResponseDTO;
import br.edu.ufape.backend.notificacao.facade.NotificacaoFacade;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

	private final NotificacaoFacade notificacaoFacade;

	public NotificacaoController(NotificacaoFacade notificacaoFacade) {
		this.notificacaoFacade = notificacaoFacade;
	}

	@GetMapping
	public ResponseEntity<List<NotificacaoResponseDTO>> listar(@RequestParam(required = false) Boolean apenasNaoLidas,
			Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String email = authentication.getName();
		return ResponseEntity.ok(notificacaoFacade.listar(email, apenasNaoLidas));
	}

	@GetMapping("/contagem-nao-lidas")
	public ResponseEntity<ContagemNaoLidasResponseDTO> contarNaoLidas(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String email = authentication.getName();
		return ResponseEntity.ok(notificacaoFacade.contarNaoLidas(email));
	}

	@PatchMapping("/{id}/leitura")
	public ResponseEntity<NotificacaoResponseDTO> marcarComoLida(@PathVariable Long id, Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String email = authentication.getName();
		return ResponseEntity.ok(notificacaoFacade.marcarComoLida(email, id));
	}

	@PatchMapping("/leitura")
	public ResponseEntity<Void> marcarTodasComoLidas(Authentication authentication) {
		if (authentication == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String email = authentication.getName();
		notificacaoFacade.marcarTodasComoLidas(email);
		return ResponseEntity.noContent().build();
	}
}
