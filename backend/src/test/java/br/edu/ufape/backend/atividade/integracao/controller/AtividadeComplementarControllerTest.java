package br.edu.ufape.backend.atividade.integracao.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
		mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	private String cadastrarEstudanteERetornarToken(String email) throws Exception {
		CadastroUsuarioRequest cadastro = new CadastroUsuarioRequest();
		cadastro.setNome("Estudante Atividade");
		cadastro.setEmail(email);
		cadastro.setSenha("senha1234");
		cadastro.setRole(Role.ESTUDANTE);

		mockMvc.perform(post("/api/v1/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cadastro))).andExpect(status().isCreated());

		return jwtService.generateToken(email);
	}

	private String criarAvaliadorERetornarToken(String email) {
		usuarioContrato.salvar(new Avaliador("Avaliador Atividade", email, "hash-irrelevante", "REG-001", "Extensao"));
		return jwtService.generateToken(email);
	}

	private Long cadastrarAtividadeERetornarId(String token, String titulo) throws Exception {
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		var result = mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", titulo)
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "8").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isCreated()).andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
	}

	@Test
	@DisplayName("Cenário 1: Deve cadastrar atividade com sucesso e retornar 201 Created")
	void deveCadastrarAtividadeComSucesso() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.sucesso@ufape.edu.br");

		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso de Testes")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "8").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
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

		String[] camposObrigatorios = {"titulo", "instituicaoResponsavel", "dataRealizacao", "cargaHoraria", "natureza",
				"categoria"};

		for (String campoAusente : camposObrigatorios) {
			MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
					"PDF-DUMMY".getBytes());

			var builder = multipart(URL_CADASTRO).file(arquivo).header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

			for (Map.Entry<String, String> e : baseParams.entrySet()) {
				if (!e.getKey().equals(campoAusente)) {
					builder.param(e.getKey(), e.getValue());
				}
			}

			mockMvc.perform(builder).andExpect(status().isBadRequest());
		}
	}

	@Test
	@DisplayName("Deve retornar mensagem de validação quando título estiver vazio")
	void deveRetornarMensagemParaTituloVazio() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.validacao.titulo@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "8").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("titulo: Título é obrigatório"));
	}

	@Test
	@DisplayName("Deve retornar mensagem de validação para carga horária zero ou negativa")
	void deveRetornarMensagemParaCargaHorariaInvalida() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.validacao.carga@ufape.edu.br");

		for (String cargaHoraria : new String[]{"0", "-1"}) {
			MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
					"PDF-DUMMY".getBytes());

			mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
					.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
					.param("cargaHoraria", cargaHoraria).param("natureza", Natureza.ACC.name())
					.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value("cargaHoraria: Carga horária deve ser maior que zero"));
		}
	}

	@Test
	@DisplayName("Deve retornar mensagem de validação para data futura")
	void deveRetornarMensagemParaDataFutura() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.validacao.data@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE")
				.param("dataRealizacao", LocalDate.now().plusDays(1).toString()).param("cargaHoraria", "8")
				.param("natureza", Natureza.ACC.name()).param("categoria", Categoria.EXTENSAO.name())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("dataRealizacao: Data de realização não pode ser no futuro"));
	}

	@Test
	@DisplayName("Deve retornar mensagem de parsing para carga horária inválida")
	void deveRetornarMensagemParaCargaHorariaNaoNumerica() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.parsing.carga@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "oito").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("cargaHoraria")));
	}

	@Test
	@DisplayName("Deve retornar mensagem de parsing para data inválida")
	void deveRetornarMensagemParaDataInvalida() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.parsing.data@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", "data-invalida")
				.param("cargaHoraria", "8").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("dataRealizacao")));
	}

	@Test
	@DisplayName("Deve retornar mensagem de parsing para enum natureza inválido")
	void deveRetornarMensagemParaNaturezaInvalidaNoCadastro() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.parsing.natureza@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "8").param("natureza", "INEXISTENTE")
				.param("categoria", Categoria.EXTENSAO.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("natureza")));
	}

	@Test
	@DisplayName("Deve retornar mensagem de parsing para enum categoria inválido")
	void deveRetornarMensagemParaCategoriaInvalidaNoCadastro() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.parsing.categoria@ufape.edu.br");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "8").param("natureza", Natureza.ACC.name()).param("categoria", "INEXISTENTE")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("categoria")));
	}

	@Test
	@DisplayName("Cenário 3: Deve retornar 400 quando certificado ausente ou tipo inválido")
	void deveRetornarBadRequestParaArquivoInvalidoOuAusente() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.arquivo@ufape.edu.br");

		// Sem arquivo
		mockMvc.perform(multipart(URL_CADASTRO).param("titulo", "Minicurso").param("instituicaoResponsavel", "UFAPE")
				.param("dataRealizacao", LocalDate.now().toString()).param("cargaHoraria", "4")
				.param("natureza", Natureza.ACC.name()).param("categoria", Categoria.EVENTOS.name())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Arquivo de certificado não pode ser vazio"));

		// Arquivo com tipo não permitido
		MockMultipartFile arquivoInvalido = new MockMultipartFile("arquivo", "certificado.exe",
				"application/octet-stream", "DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivoInvalido).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "4").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EVENTOS.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Certificado inválido. Aceitos: PDF, PNG ou JPEG"));
	}

	@Test
	@DisplayName("Cenário 4: Deve retornar 401 Unauthorized quando não houver token")
	void deveRetornarUnauthorizedQuandoNaoHouverToken() throws Exception {
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "4").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EVENTOS.name())).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Cenário 5: Deve retornar 403 Forbidden quando usuário não for estudante")
	void deveRetornarForbiddenQuandoUsuarioNaoForEstudante() throws Exception {
		String token = criarAvaliadorERetornarToken("atividade.avaliador@ufape.edu.br");

		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "certificado.pdf", "application/pdf",
				"PDF-DUMMY".getBytes());

		mockMvc.perform(multipart(URL_CADASTRO).file(arquivo).param("titulo", "Minicurso")
				.param("instituicaoResponsavel", "UFAPE").param("dataRealizacao", LocalDate.now().toString())
				.param("cargaHoraria", "4").param("natureza", Natureza.ACC.name())
				.param("categoria", Categoria.EVENTOS.name()).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Cenário 6: Deve excluir atividade do próprio estudante e retornar 204 No Content")
	void deveExcluirAtividadeDoProprioEstudanteERetornarNoContent() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.delete.ok@ufape.edu.br");
		Long id = cadastrarAtividadeERetornarId(token, "Atividade para exclusao");

		mockMvc.perform(delete(URL_CADASTRO + "/{id}", id).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("Cenário 7: Deve retornar 403 Forbidden ao tentar excluir atividade de outro estudante")
	void deveRetornarForbiddenAoExcluirAtividadeDeOutroEstudante() throws Exception {
		String tokenEstudante1 = cadastrarEstudanteERetornarToken("atividade.delete.outro1@ufape.edu.br");
		String tokenEstudante2 = cadastrarEstudanteERetornarToken("atividade.delete.outro2@ufape.edu.br");
		Long id = cadastrarAtividadeERetornarId(tokenEstudante1, "Atividade de outro estudante");

		mockMvc.perform(
				delete(URL_CADASTRO + "/{id}", id).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenEstudante2))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Cenário 8: Deve retornar 404 Not Found ao tentar excluir id inexistente")
	void deveRetornarNotFoundAoExcluirIdInexistente() throws Exception {
		String token = cadastrarEstudanteERetornarToken("atividade.delete.inexistente@ufape.edu.br");

		mockMvc.perform(delete(URL_CADASTRO + "/{id}", 999999L).header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("Cenário 9: Deve retornar 401 Unauthorized ao tentar excluir sem token")
	void deveRetornarUnauthorizedAoExcluirSemToken() throws Exception {
		mockMvc.perform(delete(URL_CADASTRO + "/{id}", 1L)).andExpect(status().isUnauthorized());
	}
}
