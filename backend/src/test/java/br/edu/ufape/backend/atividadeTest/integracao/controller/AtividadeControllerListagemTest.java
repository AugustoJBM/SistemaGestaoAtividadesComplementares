package br.edu.ufape.backend.atividadeTest.integracao.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class AtividadeControllerListagemTest {

    private static final String URL_LISTAGEM = "/api/v1/atividades";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String cadastrarEstudanteERetornarToken(String email) throws Exception {
        CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
        cadastro.setNome("Estudante Listagem");
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
        usuarioContrato.salvar(new Avaliador("Avaliador Listagem", email, "hash-irrelevante",
                "REG-001", "Extensao"));
        return jwtService.generateToken(email);
    }

    private void cadastrarAtividade(String token, String titulo, Natureza natureza, Categoria categoria)
            throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY".getBytes());

        mockMvc.perform(multipart(URL_LISTAGEM)
                .file(arquivo)
                .param("titulo", titulo)
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "8")
                .param("natureza", natureza.name())
                .param("categoria", categoria.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Estudante autenticado sem atividades retorna 200 com array vazio")
    void estudanteAutenticadoSemAtividadesRetorna200ComArrayVazio() throws Exception {
        // Arrange
        String email = "listagem.vazia@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Estudante com atividades retorna 200 e apenas as atividades dele")
    void estudanteComAtividadesRetorna200ComAtividadesDele() throws Exception {
        // Arrange
        String emailDono = "listagem.dono@ufape.edu.br";
        String tokenDono = cadastrarEstudanteERetornarToken(emailDono);
        String emailOutro = "listagem.outro.estudante@ufape.edu.br";
        String tokenOutro = cadastrarEstudanteERetornarToken(emailOutro);

        cadastrarAtividade(tokenDono, "Minicurso de Testes", Natureza.ACC, Categoria.EXTENSAO);
        cadastrarAtividade(tokenDono, "Workshop de Spring", Natureza.ACEX, Categoria.EVENTOS);
        cadastrarAtividade(tokenOutro, "Atividade de outro estudante", Natureza.ACC, Categoria.PESQUISA);

        // Act & Assert: garante isolamento por identidade do JWT, nao por stub artificial
        mockMvc.perform(get(URL_LISTAGEM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDono))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].estudanteEmail", everyItem(is(emailDono))))
                .andExpect(jsonPath("$[*].titulo", containsInAnyOrder("Minicurso de Testes", "Workshop de Spring")));
    }

    @Test
    @DisplayName("Filtro por natureza funciona via query param")
    void filtroPorNaturezaFuncionaViaQueryParam() throws Exception {
        // Arrange
        String email = "listagem.filtro.natureza@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        cadastrarAtividade(token, "Atividade ACC", Natureza.ACC, Categoria.PESQUISA);
        cadastrarAtividade(token, "Atividade ACEX", Natureza.ACEX, Categoria.PESQUISA);

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .param("natureza", "ACC")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Atividade ACC"))
                .andExpect(jsonPath("$[0].natureza").value("ACC"));
    }

    @Test
    @DisplayName("Filtro por categoria funciona via query param")
    void filtroPorCategoriaFuncionaViaQueryParam() throws Exception {
        // Arrange
        String email = "listagem.filtro.categoria@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        cadastrarAtividade(token, "Atividade Extensao", Natureza.ACC, Categoria.EXTENSAO);
        cadastrarAtividade(token, "Atividade Pesquisa", Natureza.ACC, Categoria.PESQUISA);

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .param("categoria", "PESQUISA")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Atividade Pesquisa"))
                .andExpect(jsonPath("$[0].categoria").value("PESQUISA"));
    }

    @Test
    @DisplayName("Natureza invalida na query param retorna 400")
    void naturezaInvalidaRetorna400() throws Exception {
        // Arrange
        String email = "listagem.natureza.invalida@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .param("natureza", "XPTO")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Requisicao sem token retorna 401")
    void requisicaoSemTokenRetorna401() throws Exception {
        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Usuario autenticado com perfil nao-estudante retorna 403")
    void usuarioNaoEstudanteRetorna403() throws Exception {
        // Arrange: avaliador autenticado via JWT; sem stub da facade,
        // para o 403 nascer no service (AcessoNegadoAtividadeException).
        String email = "listagem.avaliador@ufape.edu.br";
        String token = criarAvaliadorERetornarToken(email);

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
