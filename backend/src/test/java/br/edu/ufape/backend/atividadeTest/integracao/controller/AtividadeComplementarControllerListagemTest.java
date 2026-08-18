package br.edu.ufape.backend.atividadeTest.integracao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.autenticacao.dto.CadastroUsuarioRequest;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Role;

@SpringBootTest
@Transactional
class AtividadeComplementarControllerListagemTest {

    private static final String URL_LISTAGEM = "/api/v1/atividades";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    @MockitoSpyBean
    private AtividadeFacade atividadeFacade;

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

    @Test
    @DisplayName("Estudante autenticado sem atividades retorna 200 com content vazio")
    void estudanteAutenticadoSemAtividadesRetorna200ComArrayVazio() throws Exception {
        // Arrange
        String email = "listagem.vazia@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);
        doReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0))
                .when(atividadeFacade)
                .listarAtividadesDoEstudante(eq(email), isNull(), isNull(), any(Pageable.class));

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Estudante com atividades retorna 200 e apenas as atividades dele")
    void estudanteComAtividadesRetorna200ComAtividadesDele() throws Exception {
        // Arrange
        String email = "listagem.com.atividades@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);
        List<AtividadeResponse> atividades = List.of(
                new AtividadeResponse(1L, "Minicurso de Testes", "UFAPE", LocalDate.of(2025, 6, 10),
                        8, Natureza.ACC, Categoria.EXTENSAO, LocalDateTime.of(2025, 6, 11, 10, 0)),
                new AtividadeResponse(2L, "Workshop de Spring", "UFAPE", LocalDate.of(2025, 7, 15),
                        12, Natureza.ACEX, Categoria.EVENTOS, LocalDateTime.of(2025, 7, 16, 14, 30)));
        doReturn(new PageImpl<>(atividades, PageRequest.of(0, 20), atividades.size()))
                .when(atividadeFacade)
                .listarAtividadesDoEstudante(eq(email), isNull(), isNull(), any(Pageable.class));

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Minicurso de Testes"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].natureza").value("ACEX"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(atividadeFacade).listarAtividadesDoEstudante(eq(email), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Filtro por natureza funciona via query param")
    void filtroPorNaturezaFuncionaViaQueryParam() throws Exception {
        // Arrange
        String email = "listagem.filtro.natureza@ufape.edu.br";
        String token = cadastrarEstudanteERetornarToken(email);
        List<AtividadeResponse> atividadesFiltradas = List.of(
                new AtividadeResponse(1L, "Atividade ACC", "UFAPE", LocalDate.of(2025, 5, 1),
                        10, Natureza.ACC, Categoria.PESQUISA, LocalDateTime.of(2025, 5, 2, 9, 0)));
        doReturn(new PageImpl<>(atividadesFiltradas, PageRequest.of(0, 20), 1))
                .when(atividadeFacade)
                .listarAtividadesDoEstudante(eq(email), eq(Natureza.ACC), isNull(), any(Pageable.class));

        // Act & Assert
        mockMvc.perform(get(URL_LISTAGEM)
                .param("natureza", "ACC")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].natureza").value("ACC"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(atividadeFacade).listarAtividadesDoEstudante(eq(email), eq(Natureza.ACC), isNull(), any(Pageable.class));
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
