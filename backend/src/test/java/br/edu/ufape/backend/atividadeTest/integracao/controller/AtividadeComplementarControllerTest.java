package br.edu.ufape.backend.atividadeTest.integracao.controller;

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
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class AtividadeComplementarControllerTest {

    private static final String URL_CADASTRO = "/api/v1/atividades";

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
        cadastro.setNome("Estudante Atividade");
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
        usuarioContrato.salvar(new Avaliador("Avaliador Atividade", email, "hash-irrelevante",
                "REG-001", "Extensao"));
        return jwtService.generateToken(email);
    }

    @Test
    @DisplayName("Cenário 1: Deve cadastrar atividade com sucesso e retornar 201 Created")
    void deveCadastrarAtividadeComSucesso() throws Exception {
        String token = cadastrarEstudanteERetornarToken("atividade.sucesso@ufape.edu.br");

        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY".getBytes());

        mockMvc.perform(multipart(URL_CADASTRO)
                .file(arquivo)
                .param("titulo", "Minicurso de Testes")
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "8")
                .param("natureza", Natureza.ACC.name())
                .param("categoria", Categoria.EXTENSAO.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Cenário 2: Deve retornar 400 Bad Request quando faltar campo obrigatório")
    void deveRetornarBadRequestParaCamposObrigatoriosAusentes() throws Exception {
        String token = cadastrarEstudanteERetornarToken("atividade.badrequest@ufape.edu.br");

        Map<String, String> baseParams = new HashMap<>();
        baseParams.put("titulo", "Minicurso de Testes");
        baseParams.put("instituicaoResponsavel", "UFAPE");
        baseParams.put("dataRealizacao", LocalDate.now().toString());
        baseParams.put("cargaHoraria", "8");
        baseParams.put("natureza", Natureza.ACC.name());
        baseParams.put("categoria", Categoria.EXTENSAO.name());

        String[] camposObrigatorios = {"titulo", "instituicaoResponsavel", "dataRealizacao", "cargaHoraria", "natureza", "categoria"};

        for (String campoAusente : camposObrigatorios) {
            MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                    "application/pdf", "PDF-DUMMY".getBytes());

            var builder = multipart(URL_CADASTRO).file(arquivo)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            for (Map.Entry<String, String> e : baseParams.entrySet()) {
                if (!e.getKey().equals(campoAusente)) {
                    builder.param(e.getKey(), e.getValue());
                }
            }

            mockMvc.perform(builder).andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Cenário 3: Deve retornar 400 quando certificado ausente ou tipo inválido")
    void deveRetornarBadRequestParaArquivoInvalidoOuAusente() throws Exception {
        String token = cadastrarEstudanteERetornarToken("atividade.arquivo@ufape.edu.br");

        // Sem arquivo
        mockMvc.perform(multipart(URL_CADASTRO)
                .param("titulo", "Minicurso")
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "4")
                .param("natureza", Natureza.ACC.name())
                .param("categoria", Categoria.EVENTOS.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest());

        // Arquivo com tipo não permitido
        MockMultipartFile arquivoInvalido = new MockMultipartFile("arquivo", "certificado.exe",
                "application/octet-stream", "DUMMY".getBytes());

        mockMvc.perform(multipart(URL_CADASTRO)
                .file(arquivoInvalido)
                .param("titulo", "Minicurso")
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "4")
                .param("natureza", Natureza.ACC.name())
                .param("categoria", Categoria.EVENTOS.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Cenário 4: Deve retornar 401 Unauthorized quando não houver token")
    void deveRetornarUnauthorizedQuandoNaoHouverToken() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY".getBytes());

        mockMvc.perform(multipart(URL_CADASTRO)
                .file(arquivo)
                .param("titulo", "Minicurso")
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "4")
                .param("natureza", Natureza.ACC.name())
                .param("categoria", Categoria.EVENTOS.name()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Cenário 5: Deve retornar 403 Forbidden quando usuário não for estudante")
    void deveRetornarForbiddenQuandoUsuarioNaoForEstudante() throws Exception {
        String token = criarAvaliadorERetornarToken("atividade.avaliador@ufape.edu.br");

        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf",
                "application/pdf", "PDF-DUMMY".getBytes());

        mockMvc.perform(multipart(URL_CADASTRO)
                .file(arquivo)
                .param("titulo", "Minicurso")
                .param("instituicaoResponsavel", "UFAPE")
                .param("dataRealizacao", LocalDate.now().toString())
                .param("cargaHoraria", "4")
                .param("natureza", Natureza.ACC.name())
                .param("categoria", Categoria.EVENTOS.name())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
