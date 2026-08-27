package br.edu.ufape.backend.solicitacaoTest.integracao.controller;

import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.solicitacao.repository.SolicitacaoValidacaoRepository;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class SolicitacaoAvaliacaoSecurityTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    @Autowired
    private SolicitacaoValidacaoRepository solicitacaoRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String criarEstudanteERetornarToken(String email) {
        Estudante estudante = (Estudante) usuarioContrato.salvar(
                new Estudante("Estudante Teste", email, "hash-senha", "2026001", "BCC"));
        return jwtService.generateToken(estudante.getEmail());
    }

    private String criarAvaliadorERetornarToken(String email) {
        usuarioContrato.salvar(
                new Avaliador("Avaliador Teste", email, "hash-senha", "REG-001", "Ciencia da Computacao"));
        return jwtService.generateToken(email);
    }

    private SolicitacaoValidacao criarSolicitacaoSubmetida() {
        SolicitacaoValidacao s = new SolicitacaoValidacao(1L);
        s.setStatus(StatusSolicitacao.SUBMETIDA);
        return solicitacaoRepository.save(s);
    }

    @Test
    @DisplayName("ESTUDANTE autenticado deve receber 403 ao tentar avaliar solicitacao")
    void estudanteNaoPodeAvaliarSolicitacao() throws Exception {
        String tokenEstudante = criarEstudanteERetornarToken("estudante.sec.solic@ufape.edu.br");

        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("decisao", "APROVADA"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("AVALIADOR autenticado deve ter permissao (200) para avaliar solicitacao SUBMETIDA")
    void avaliadorPodeAvaliarSolicitacao() throws Exception {
        SolicitacaoValidacao solicitacao = criarSolicitacaoSubmetida();
        String tokenAvaliador = criarAvaliadorERetornarToken("avaliador.sec.solic@ufape.edu.br");

        mockMvc.perform(patch("/api/v1/solicitacoes/" + solicitacao.getId() + "/avaliacao")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAvaliador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("decisao", "APROVADA"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"))
                .andExpect(jsonPath("$.totalAtividades").value(0));
    }

    @Test
    @DisplayName("Requisicao sem autenticacao deve retornar 401 Unauthorized")
    void requisicaoSemAutenticacaoRetornaUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/v1/solicitacoes/1/avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("decisao", "APROVADA"))))
                .andExpect(status().isUnauthorized());
    }
}