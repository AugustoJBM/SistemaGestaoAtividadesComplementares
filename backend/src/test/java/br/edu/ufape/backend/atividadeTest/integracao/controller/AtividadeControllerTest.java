package br.edu.ufape.backend.atividadeTest.integracao.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
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
class AtividadeControllerTest {

    private static final String URL_PROGRESSO = "/api/v1/atividades/progresso";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Matcher<Object> percentualEntreZeroECem() {
        return new CustomMatcher<Object>("um numero entre 0 e 100 (inclusive)") {
            @Override
            public boolean matches(Object item) {
                if (!(item instanceof Number)) {
                    return false;
                }
                double valor = ((Number) item).doubleValue();
                return valor >= 0.0 && valor <= 100.0;
            }
        };
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String cadastrarEstudanteERetornarToken(String email) throws Exception {
        CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
        cadastro.setNome("Estudante Progresso");
        cadastro.setEmail(email);
        cadastro.setSenha("senha1234");
        cadastro.setRole(Role.ESTUDANTE);

        // Falha aqui, e nao mais adiante, caso o cadastro pare de funcionar.
        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)))
                .andExpect(status().isCreated());

        return jwtService.generateToken(email);
    }

    /**
     * Persiste um usuario NAO estudante (avaliador) direto pelo contrato de
     * usuario, ja que o cadastro publico so aceita o perfil ESTUDANTE.
     */
    private String criarAvaliadorERetornarToken(String email) {
        usuarioContrato.salvar(new Avaliador("Avaliador Progresso", email, "hash-irrelevante",
                "REG-001", "Extensao"));

        return jwtService.generateToken(email);
    }

    // Constraint: progresso-response-shape
    @Test
    @DisplayName("Deve retornar 200 com progresso de ACC e ACEX do proprio estudante autenticado, sem campos nulos")
    void deveRetornarProgressoDoEstudanteAutenticadoComSucesso() throws Exception {
        String token = cadastrarEstudanteERetornarToken("progresso.sucesso@ufape.edu.br");

        mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc").exists())
                .andExpect(jsonPath("$.acc.horasAcumuladas").exists())
                .andExpect(jsonPath("$.acc.horasAcumuladas").isNotEmpty())
                .andExpect(jsonPath("$.acc.horasExigidas").exists())
                .andExpect(jsonPath("$.acc.horasExigidas").isNotEmpty())
                .andExpect(jsonPath("$.acc.percentualConcluido").exists())
                .andExpect(jsonPath("$.acc.percentualConcluido").isNotEmpty())
                .andExpect(jsonPath("$.acc.percentualConcluido").value(percentualEntreZeroECem()))
                .andExpect(jsonPath("$.acex").exists())
                .andExpect(jsonPath("$.acex.horasAcumuladas").exists())
                .andExpect(jsonPath("$.acex.horasAcumuladas").isNotEmpty())
                .andExpect(jsonPath("$.acex.horasExigidas").exists())
                .andExpect(jsonPath("$.acex.horasExigidas").isNotEmpty())
                .andExpect(jsonPath("$.acex.percentualConcluido").exists())
                .andExpect(jsonPath("$.acex.percentualConcluido").isNotEmpty())
                .andExpect(jsonPath("$.acex.percentualConcluido").value(percentualEntreZeroECem()));
    }

    // Constraint: auth-401-unauthenticated
    @Test
    @DisplayName("Negativo: Deve retornar 401 Unauthorized quando requisicao de progresso nao possuir header Authorization")
    void deveRetornarUnauthorizedQuandoNaoHouverToken() throws Exception {
        mockMvc.perform(get(URL_PROGRESSO))
                .andExpect(status().isUnauthorized());
    }

    // Constraint: auth-401-unauthenticated
    @Test
    @DisplayName("Negativo: Deve retornar 401 Unauthorized quando token de progresso for invalido/malformado")
    void deveRetornarUnauthorizedQuandoTokenForInvalido() throws Exception {
        mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido.malformado"))
                .andExpect(status().isUnauthorized());
    }

    // Constraint: apenas-estudante-consulta-progresso
    @Test
    @DisplayName("Negativo: Deve retornar 403 Forbidden quando o usuario autenticado nao for estudante")
    void deveRetornarForbiddenQuandoUsuarioNaoForEstudante() throws Exception {
        String token = criarAvaliadorERetornarToken("progresso.avaliador@ufape.edu.br");

        mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Negativo: 403 deve trazer mensagem do contexto de progresso, nunca a mensagem do cadastro publico")
    void deveResponderComMensagemCoerenteComOContextoDeProgresso() throws Exception {
        String token = criarAvaliadorERetornarToken("progresso.avaliador.mensagem@ufape.edu.br");

        String corpo = mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(
                "Apenas estudantes podem consultar o progresso de atividades.", corpo);
        org.junit.jupiter.api.Assertions.assertFalse(corpo.contains("cadastro"),
                "A mensagem do cadastro publico nao pode vazar pelo endpoint de progresso");
    }

    // Constraint: no-student-id-param
    @Test
    @DisplayName("Negativo: Nao deve existir rota que aceite id de outro estudante via path variable")
    void naoDeveExistirRotaComIdDeEstudanteNoPath() throws Exception {
        String token = cadastrarEstudanteERetornarToken("progresso.semrota.id@ufape.edu.br");

        mockMvc.perform(get(URL_PROGRESSO + "/999999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve ignorar parametro desconhecido (estudanteId) na query string e retornar sempre o progresso do estudante do token")
    void deveIgnorarParametroDesconhecidoNaQueryString() throws Exception {
        String token = cadastrarEstudanteERetornarToken("progresso.querystring@ufape.edu.br");

        String respostaSemParametro = mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String respostaComParametroDeOutroEstudante = mockMvc.perform(get(URL_PROGRESSO)
                .param("estudanteId", "999999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(respostaSemParametro, respostaComParametroDeOutroEstudante);
    }

    @Test
    @DisplayName("Deve derivar o estudante exclusivamente do token, sem influencia da identidade de outro estudante")
    void deveDerivarEstudanteExclusivamenteDoTokenIgnorandoIdentidadeDeOutroEstudante() throws Exception {
        String tokenEstudanteA = cadastrarEstudanteERetornarToken("progresso.isolamento.a@ufape.edu.br");
        String tokenEstudanteB = cadastrarEstudanteERetornarToken("progresso.isolamento.b@ufape.edu.br");

        String respostaA = mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudanteA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc").exists())
                .andExpect(jsonPath("$.acex").exists())
                .andReturn().getResponse().getContentAsString();

        String respostaAComIdDeB = mockMvc.perform(get(URL_PROGRESSO)
                .param("estudanteId", "999999")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudanteA))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get(URL_PROGRESSO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudanteB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acc").exists())
                .andExpect(jsonPath("$.acex").exists());

        org.junit.jupiter.api.Assertions.assertEquals(respostaA, respostaAComIdDeB);
    }
}
