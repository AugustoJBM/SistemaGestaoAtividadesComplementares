package br.edu.ufape.backend.autenticacaoTest.integracao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.dto.LoginRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.autenticacao.service.TokenBlacklistService;
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

        @Autowired
        private TokenBlacklistService tokenBlacklistService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                .apply(SecurityMockMvcConfigurers.springSecurity())
                                .build();
        }

        // TESTES DE CADASTRO
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
        @DisplayName("🔴 Negativo: Deve retornar 400 Bad Request quando senha tiver menos de 8 caracteres")
        void deveRetornarBadRequestQuandoSenhaForInvalida() throws Exception {
                CadastroUsuarioRequest request = new CadastroUsuarioRequest();
                request.setNome("Estudante Teste");
                request.setEmail("estudante.invalido@ufape.edu.br");
                request.setSenha("123"); // Senha com menos de 8 caracteres
                request.setRole(Role.ESTUDANTE);

                mockMvc.perform(post("/api/v1/auth/cadastro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("🔴 Negativo: Deve retornar 409 Conflict quando e-mail ja estiver cadastrado")
        void deveRetornarConflictQuandoEmailJaExistir() throws Exception {
                CadastroUsuarioRequest request = new CadastroUsuarioRequest();
                request.setNome("Estudante Duplicado");
                request.setEmail("duplicado@ufape.edu.br");
                request.setSenha("senha1234");
                request.setRole(Role.ESTUDANTE);

                // Primeiro cadastro
                mockMvc.perform(post("/api/v1/auth/cadastro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)));

                // Tentativa de cadastro duplicado
                mockMvc.perform(post("/api/v1/auth/cadastro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        // TESTES DE LOGIN
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
        @DisplayName("🔴 Negativo: Deve retornar 401 Unauthorized quando credenciais de login forem incorretas")
        void deveRetornarUnauthorizedQuandoCredenciaisIncorretas() throws Exception {
                LoginRequest login = new LoginRequest();
                login.setUsuario("inexistente@ufape.edu.br");
                login.setSenha("senhaIncorreta");

                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isUnauthorized());
        }

        // TESTES DE LOGOUT
        @Test
        @DisplayName("Deve realizar logout com sucesso quando token for valido")
        void deveRealizarLogoutComSucesso() throws Exception {
                CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
                cadastro.setNome("Usuario Logout");
                cadastro.setEmail("logout.teste@ufape.edu.br");
                cadastro.setSenha("senha1234");
                cadastro.setRole(Role.ESTUDANTE);

                mockMvc.perform(post("/api/v1/auth/cadastro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cadastro)));

                String token = jwtService.generateToken("logout.teste@ufape.edu.br");

                mockMvc.perform(post("/api/v1/auth/logout")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("🔴 Negativo: Deve retornar 401 Unauthorized ao acessar logout sem header Authorization")
        void deveRetornarUnauthorizedQuandoAcessoAoEndpointProtegidoSemHeaderDeAutorizacao() throws Exception {
                mockMvc.perform(post("/api/v1/auth/logout"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("🔴 Negativo: Deve retornar 401 Unauthorized quando token revogado/blacklist for reutilizado no logout")
        void deveRetornarUnauthorizedQuandoTokenBlacklistForReutilizado() throws Exception {
                CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
                cadastro.setNome("Usuario Blacklist");
                cadastro.setEmail("blacklist.teste@ufape.edu.br");
                cadastro.setSenha("senha1234");
                cadastro.setRole(Role.ESTUDANTE);

                mockMvc.perform(post("/api/v1/auth/cadastro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cadastro)));

                String token = jwtService.generateToken("blacklist.teste@ufape.edu.br");
                tokenBlacklistService.blacklistToken(token);

                mockMvc.perform(post("/api/v1/auth/logout")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                                .andExpect(status().isUnauthorized());
        }
}