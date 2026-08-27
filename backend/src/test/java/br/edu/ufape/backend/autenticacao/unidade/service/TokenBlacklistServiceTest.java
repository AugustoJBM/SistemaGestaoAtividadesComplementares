package br.edu.ufape.backend.autenticacao.unidade.service;

import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

	private TokenBlacklistService tokenBlacklistService;

	@BeforeEach
	void setUp() {
		tokenBlacklistService = new TokenBlacklistService();
	}

	@Test
	@DisplayName("Deve adicionar token na blacklist e confirmar revogacao")
	void deveAdicionarTokenNaBlacklist() {
		String token = "token-revogado-123";

		assertFalse(tokenBlacklistService.isTokenBlacklisted(token));

		tokenBlacklistService.blacklistToken(token);

		assertTrue(tokenBlacklistService.isTokenBlacklisted(token));
	}
}
