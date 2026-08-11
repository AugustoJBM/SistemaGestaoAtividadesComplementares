package br.edu.ufape.backend.autenticacaoTest.integracao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar estudante com sucesso e retornar 201 Created")
    void deveCadastrarEstudanteComSucesso() throws Exception {
        CadastroUsuarioRequest request = new CadastroUsuarioRequest();
        request.setNome("Estudante Teste");
        request.setEmail("estudante.novo@ufape.edu.br");
        request.setSenha("senha1234");
        request.setRole(Role.ESTUDANTE);

        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Estudante Teste"))
                .andExpect(jsonPath("$.email").value("estudante.novo@ufape.edu.br"));
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar Token Bearer")
    void deveRealizarLoginComSucesso() throws Exception {
        CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
        cadastro.setNome("Usuario Login");
        cadastro.setEmail("login.teste@ufape.edu.br");
        cadastro.setSenha("senha1234");
        cadastro.setRole(Role.ESTUDANTE);

        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)));

        LoginRequest login = new LoginRequest();
        login.setUsuario("login.teste@ufape.edu.br");
        login.setSenha("senha1234");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("Deve realizar logout com sucesso quando token for valido")
    void deveRealizarLogoutComSucesso() throws Exception {
        // 1. Cadastra o usuario no banco de dados primeiro
        CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
        cadastro.setNome("Usuario Logout");
        cadastro.setEmail("logout.teste@ufape.edu.br");
        cadastro.setSenha("senha1234");
        cadastro.setRole(Role.ESTUDANTE);

        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)))
                .andExpect(status().isCreated());

        // 2. Gera o token e realiza o logout
        String token = jwtService.generateToken("logout.teste@ufape.edu.br");

        mockMvc.perform(post("/api/v1/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }
}