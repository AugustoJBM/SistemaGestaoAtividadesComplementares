package br.edu.ufape.backend.autenticacaoTest.unidade.service;

import br.edu.ufape.backend.autenticacao.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "c2VjdXJlLXNlY3JldC1rZXktZm9yLWp3dC1zZWN1cml0eQ==");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3600000L);
    }

    @Test
    @DisplayName("Deve gerar token valido e extrair o username corretamente")
    void deveGerarEValidarToken() {
        String email = "usuario@ufape.edu.br";
        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals(email, jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Deve retornar false para token corrompido")
    void deveRetornarFalseParaTokenInvalido() {
        assertFalse(jwtService.isTokenValid("token.invalido.corrompido"));
    }
}