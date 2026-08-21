package br.edu.ufape.backend.relatorioTest.integracao;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class RelatorioControllerTest {

    private static final String URL_RELATORIO = "/api/v1/relatorios/atividades";
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
        cadastro.setNome("Estudante Relatorio");
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
        usuarioContrato.salvar(new Avaliador("Avaliador Relatorio", email, "hash-irrelevante",
                "REG-001", "Extensao"));
        return jwtService.generateToken(email);
    }

    private void cadastrarAtividade(String token, String titulo, Natureza natureza, Categoria categoria,
            int cargaHoraria) throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY".getBytes());

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
    @DisplayName("Deve retornar 200 com naturezas vazias e totais zerados para estudante sem atividades")
    void deveRetornarRelatorioVazioParaEstudanteSemAtividades() throws Exception {
        String token = cadastrarEstudanteERetornarToken("relatorio.vazio@ufape.edu.br");

        mockMvc.perform(get(URL_RELATORIO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudanteEmail").value("relatorio.vazio@ufape.edu.br"))
                .andExpect(jsonPath("$.naturezas").isArray())
                .andExpect(jsonPath("$.naturezas").isEmpty())
                .andExpect(jsonPath("$.totalHorasAcc").value(0))
                .andExpect(jsonPath("$.totalHorasAcex").value(0))
                .andExpect(jsonPath("$.totalHorasGeral").value(0));
    }

    @Test
    @DisplayName("Deve retornar 200 com JSON agrupado por natureza e categoria para estudante com atividades ACC e ACEX")
    void deveRetornarRelatorioAgrupadoParaEstudanteComAtividades() throws Exception {
        String email = "relatorio.comdados@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);

        cadastrarAtividade(token, "Minicurso de Extensao ACC", Natureza.ACC, Categoria.EXTENSAO, 8);
        cadastrarAtividade(token, "Evento de Extensao ACEX", Natureza.ACEX, Categoria.EVENTOS, 5);

        mockMvc.perform(get(URL_RELATORIO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estudanteEmail").value(email))
                .andExpect(jsonPath("$.naturezas[0].natureza").value("ACC"))
                .andExpect(jsonPath("$.naturezas[1].natureza").value("ACEX"))
                .andExpect(jsonPath("$.naturezas[0].totalHoras").value(8))
                .andExpect(jsonPath("$.naturezas[0].categorias[0].categoria").value("EXTENSAO"))
                .andExpect(jsonPath("$.naturezas[0].categorias[0].totalHoras").value(8))
                .andExpect(jsonPath("$.naturezas[0].categorias[0].atividades[0].titulo").value("Minicurso de Extensao ACC"))
                .andExpect(jsonPath("$.naturezas[0].categorias[0].atividades[0].cargaHorariaEmHoras").value(8))
                .andExpect(jsonPath("$.naturezas[1].totalHoras").value(5))
                .andExpect(jsonPath("$.naturezas[1].categorias[0].categoria").value("EVENTOS"))
                .andExpect(jsonPath("$.naturezas[1].categorias[0].totalHoras").value(5))
                .andExpect(jsonPath("$.naturezas[1].categorias[0].atividades[0].titulo").value("Evento de Extensao ACEX"))
                .andExpect(jsonPath("$.naturezas[1].categorias[0].atividades[0].cargaHorariaEmHoras").value(5))
                .andExpect(jsonPath("$.totalHorasAcc").value(8))
                .andExpect(jsonPath("$.totalHorasAcex").value(5))
                .andExpect(jsonPath("$.totalHorasGeral").value(13));
    }

    @Test
    @DisplayName("Negativo: Deve retornar 401 Unauthorized quando requisicao de relatorio nao possuir header Authorization")
    void deveRetornarUnauthorizedQuandoNaoHouverToken() throws Exception {
        mockMvc.perform(get(URL_RELATORIO))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Negativo: Deve retornar 403 Forbidden quando o usuario autenticado nao for estudante")
    void deveRetornarForbiddenQuandoUsuarioNaoForEstudante() throws Exception {
        String token = criarAvaliadorERetornarToken("relatorio.avaliador@ufape.edu.br");

        mockMvc.perform(get(URL_RELATORIO)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Regressao: AVALIADOR continua recebendo 403 em GET /api/v1/atividades apos a nova regra de relatorios")
    void avaliadorContinuaRecebendo403EmAtividades() throws Exception {
        String token = criarAvaliadorERetornarToken("relatorio.avaliador.regressao@ufape.edu.br");

        mockMvc.perform(get(URL_ATIVIDADES)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
