package br.edu.ufape.backend.admin.integracao.controller;

import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AdminSecurityTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UsuarioContrato usuarioContrato;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@DisplayName("ESTUDANTE deve receber 403 Forbidden ao acessar /api/v1/admin/usuarios")
	void estudanteNaoPodeAcessarAdmin() throws Exception {
		usuarioContrato.salvar(new Estudante("Estudante", "estudante.adm@ufape.edu.br", "pwd"));
		String token = jwtService.generateToken("estudante.adm@ufape.edu.br", "ESTUDANTE");

		mockMvc.perform(get("/api/v1/admin/usuarios").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("AVALIADOR deve receber 403 Forbidden ao acessar /api/v1/admin/usuarios")
	void avaliadorNaoPodeAcessarAdmin() throws Exception {
		usuarioContrato.salvar(new Avaliador("Avaliador", "avaliador.adm@ufape.edu.br", "pwd", "REG-01", "BCC"));
		String token = jwtService.generateToken("avaliador.adm@ufape.edu.br", "AVALIADOR");

		mockMvc.perform(get("/api/v1/admin/usuarios").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("ADMINISTRADOR deve ter acesso permitido (200 OK) em /api/v1/admin/usuarios")
	void adminPodeAcessarAdmin() throws Exception {
		usuarioContrato.salvar(new Administrador("Admin", "admin.adm@ufape.edu.br", "pwd", "TOTAL", "Coord"));
		String token = jwtService.generateToken("admin.adm@ufape.edu.br", "ADMINISTRADOR");

		mockMvc.perform(get("/api/v1/admin/usuarios").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isOk());
	}
}
