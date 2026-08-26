package br.edu.ufape.backend.solicitacaoTest.integracao.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;

@SpringBootTest
@Transactional
class SolicitacaoEstudanteControllerTest {

    private static final String URL_SOLICITACOES = "/api/v1/solicitacoes";
    private static final String URL_ATIVIDADES = "/api/v1/atividades";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String cadastrarEstudanteERetornarToken(String email) throws Exception {
        CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
        cadastro.setNome("Estudante Solicitacao");
        cadastro.setEmail(email);
        cadastro.setSenha("senha1234");
        cadastro.setRole(Role.ESTUDANTE);
        mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)))
                .andExpect(status().isCreated());
        return jwtService.generateToken(email);
    }

    private String criarAvaliadorERetornarToken(String email) {
        usuarioContrato.salvar(new Avaliador("Avaliador Teste", email, "hash-irrelevante",
                "REG-001", "Extensao"));
        return jwtService.generateToken(email);
    }

    private void cadastrarAtividade(String token, String titulo, Natureza natureza, Categoria categoria, int cargaHoraria) throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY-CONTENT".getBytes());

        mockMvc.perform(multipart(URL_ATIVIDADES)
                .file(arquivo)
                .param("titulo", titulo)
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", String.valueOf(cargaHoraria))
                .param("natureza", natureza.name())
                .param("categoria", categoria.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Caminho feliz: Estudante com atividades cadastradas deve submeter solicitação retornando 201 Created")
    void deveSubmeterSolicitacaoComSucesso() throws Exception {
        String email = "estudante.solicitacao.ok@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        cadastrarAtividade(token, "Minicurso Spring Boot", Natureza.ACC, Categoria.ENSINO, 20);
        cadastrarAtividade(token, "Projeto de Extensao Comunitária", Natureza.ACEX, Categoria.EXTENSAO, 30);

        mockMvc.perform(post(URL_SOLICITACOES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("SUBMETIDA"))
                .andExpect(jsonPath("$.dataSubmissao").exists())
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].atividadeId").exists())
                .andExpect(jsonPath("$.itens[0].titulo").value("Minicurso Spring Boot"))
                .andExpect(jsonPath("$.itens[0].cargaHoraria").value(20))
                .andExpect(jsonPath("$.itens[0].natureza").value("ACC"))
                .andExpect(jsonPath("$.itens[1].atividadeId").exists())
                .andExpect(jsonPath("$.itens[1].titulo").value("Projeto de Extensao Comunitária"))
                .andExpect(jsonPath("$.itens[1].cargaHoraria").value(30))
                .andExpect(jsonPath("$.itens[1].natureza").value("ACEX"));
    }

    @Test
    @DisplayName("Regra 1: Deve retornar 409 Conflict quando o estudante já possuir uma solicitação em aberto")
    void deveRetornar409QuandoJaExisteSolicitacaoEmAberto() throws Exception {
        String email = "estudante.duplicado@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        cadastrarAtividade(token, "Atividade Unica", Natureza.ACC, Categoria.PESQUISA, 15);

        // Primeira submissão com sucesso (201)
        mockMvc.perform(post(URL_SOLICITACOES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());

        // Segunda tentativa deve falhar com 409 Conflict
        mockMvc.perform(post(URL_SOLICITACOES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Regra 3: Deve retornar 422 Unprocessable Entity quando o estudante não possuir nenhuma atividade cadastrada")
    void deveRetornar422QuandoEstudanteNaoPossuiAtividades() throws Exception {
        String email = "estudante.sem.atividades@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        mockMvc.perform(post(URL_SOLICITACOES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Segurança: Usuário não-estudante (ex: AVALIADOR) deve receber 403 Forbidden")
    void deveRetornar403ParaAvaliador() throws Exception {
        String token = criarAvaliadorERetornarToken("avaliador.bloqueado@ufape.edu.br");

        mockMvc.perform(post(URL_SOLICITACOES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Segurança: Requisição anônima sem token JWT deve receber 401 Unauthorized")
    void deveRetornar401ParaAnonimo() throws Exception {
        mockMvc.perform(post(URL_SOLICITACOES))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}

