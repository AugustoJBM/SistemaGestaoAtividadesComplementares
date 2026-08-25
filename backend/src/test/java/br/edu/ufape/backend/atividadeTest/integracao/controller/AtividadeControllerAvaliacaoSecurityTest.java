package br.edu.ufape.backend.atividadeTest.integracao.controller;

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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufape.backend.atividade.dto.AvaliacaoDecisaoRequestDTO;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoAvaliador;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.autenticacao.service.JwtService;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

@SpringBootTest
@Transactional
class AtividadeControllerAvaliacaoSecurityTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioContrato usuarioContrato;

    @Autowired
    private AtividadeComplementarRepository atividadeRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private Estudante criarEstudante(String email) {
        return (Estudante) usuarioContrato.salvar(
                new Estudante("Estudante Teste", email, "hash-senha", "2026001", "BCC"));
    }

    private String criarAvaliadorERetornarToken(String email) {
        usuarioContrato.salvar(
                new Avaliador("Avaliador Teste", email, "hash-senha", "REG-001", "Ciência da Computação"));
        return jwtService.generateToken(email);
    }

    private String criarAdminERetornarToken(String email) {
        usuarioContrato.salvar(
                new Administrador("Admin Teste", email, "hash-senha", "TOTAL", "Coordenação"));
        return jwtService.generateToken(email);
    }

    private AtividadeComplementar criarAtividadePendente(Estudante estudante) {
        Certificado cert = new Certificado("cert.pdf", "application/pdf", 1024L, "/uploads/fake.pdf");
        AtividadeComplementar atividade = new AtividadeComplementar(
                "Monitoria de Programação",
                "UFAPE",
                LocalDate.now(),
                30,
                Natureza.ACC,
                Categoria.ENSINO,
                cert,
                estudante);
        atividade.setStatus(StatusAtividade.PENDENTE);
        return atividadeRepository.save(atividade);
    }

    @Test
    @DisplayName("ESTUDANTE autenticado deve receber 403 Forbidden ao tentar avaliar atividade")
    void estudanteNaoPodeAvaliarAtividade() throws Exception {
        Estudante estudante = criarEstudante("estudante.avaliar@ufape.edu.br");
        AtividadeComplementar atividade = criarAtividadePendente(estudante);
        String tokenEstudante = jwtService.generateToken(estudante.getEmail());

        AvaliacaoDecisaoRequestDTO dto = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Tentativa de autoavaliação");

        mockMvc.perform(post("/api/v1/atividades/" + atividade.getId() + "/avaliar")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudante)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("AVALIADOR autenticado deve ter permissão (200 OK) para avaliar atividade")
    void avaliadorPodeAvaliarAtividade() throws Exception {
        Estudante estudante = criarEstudante("estudante.avaliado1@ufape.edu.br");
        AtividadeComplementar atividade = criarAtividadePendente(estudante);
        String tokenAvaliador = criarAvaliadorERetornarToken("avaliador.security@ufape.edu.br");

        AvaliacaoDecisaoRequestDTO dto = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Certificado validado com sucesso");

        mockMvc.perform(post("/api/v1/atividades/" + atividade.getId() + "/avaliar")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAvaliador)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atividadeId").value(atividade.getId()))
                .andExpect(jsonPath("$.decisaoIA").exists());
    }

    @Test
    @DisplayName("ADMINISTRADOR autenticado deve ter permissão (200 OK) para avaliar atividade")
    void adminPodeAvaliarAtividade() throws Exception {
        Estudante estudante = criarEstudante("estudante.avaliado2@ufape.edu.br");
        AtividadeComplementar atividade = criarAtividadePendente(estudante);
        String tokenAdmin = criarAdminERetornarToken("admin.security@ufape.edu.br");

        AvaliacaoDecisaoRequestDTO dto = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Homologado pela coordenação");

        mockMvc.perform(post("/api/v1/atividades/" + atividade.getId() + "/avaliar")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atividadeId").value(atividade.getId()))
                .andExpect(jsonPath("$.decisaoIA").exists());
    }

    @Test
    @DisplayName("Requisição sem autenticação deve retornar 401 Unauthorized")
    void requisicaoSemAutenticacaoRetornaUnauthorized() throws Exception {
        AvaliacaoDecisaoRequestDTO dto = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Sem token");

        mockMvc.perform(post("/api/v1/atividades/1/avaliar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}