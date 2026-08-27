package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.exception.IaProcessamentoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GroqRagServiceBranchTest {

	private GroqRagService service;

	@BeforeEach
	void setUp() {
		service = new GroqRagService(new ObjectMapper());
	}

	private Object invocarMetodoPrivado(String nomeMetodo, Class<?>[] tiposParametros, Object... args) {
		try {
			Method metodo = GroqRagService.class.getDeclaredMethod(nomeMetodo, tiposParametros);
			metodo.setAccessible(true);
			return metodo.invoke(service, args);
		} catch (Exception e) {
			throw new RuntimeException("Falha ao invocar método reflexivo: " + nomeMetodo, e);
		}
	}

	@Test
	@DisplayName("Branch: gerarParecerComContextoRAG sem apiKey retorna AMBIGUO")
	void deveRetornarAmbiguoSemApiKey() {
		ReflectionTestUtils.setField(service, "apiKey", "");
		ParecerResponseDTO dto = service.gerarParecerComContextoRAG("Curso", "UFAPE", "ACC", "ENSINO", 20, null);
		assertEquals("AMBIGUO", dto.decisaoIA());
		assertEquals(0.0, dto.scoreConfianca());

		ParecerResponseDTO dtoComContexto = service.gerarParecerComContextoRAG("Curso", "UFAPE", "ACC", "ENSINO", 20,
				"Contexto customizado");
		assertEquals("AMBIGUO", dtoComContexto.decisaoIA());
	}

	@Test
	@DisplayName("Branch: extrações sem apiKey lançam IaProcessamentoException")
	void deveLancarExcecaoSemApiKey() {
		ReflectionTestUtils.setField(service, "apiKey", "");
		assertThrows(IaProcessamentoException.class, () -> service.extrairDadosDeTexto("texto"));
		assertThrows(IaProcessamentoException.class, () -> service.extrairDadosDeImagem(new byte[10], "image/png"));
	}

	@ParameterizedTest
	@CsvSource({"'ACEX Extensao', 'ACC', 'ACEX'", "'ACC Curricular', 'ACEX', 'ACC'", "'Outro', 'FALLBACK', 'FALLBACK'"})
	@DisplayName("Branch: normalizarNatureza cobre todas as ramificações")
	void deveNormalizarNatureza(String entrada, String fallback, String esperado) {
		String res = (String) invocarMetodoPrivado("normalizarNatureza", new Class<?>[]{String.class, String.class},
				entrada, fallback);
		assertEquals(esperado, res);
	}

	@Test
	@DisplayName("Branch: normalizarNatureza nula retorna fallback")
	void deveRetornarFallbackNaturezaNula() {
		String res = (String) invocarMetodoPrivado("normalizarNatureza", new Class<?>[]{String.class, String.class},
				null, "ACC");
		assertEquals("ACC", res);
	}

	@ParameterizedTest
	@CsvSource({"'MONITORIA DE ALGORITMOS', 'EVENTOS', 'ENSINO'", "'AULA PRATICA', 'EVENTOS', 'ENSINO'",
			"'PESQUISA PIBIC', 'EVENTOS', 'PESQUISA'", "'ARTIGO CIENTIFICO', 'EVENTOS', 'PESQUISA'",
			"'PROJETO COMUNITARIO', 'ENSINO', 'EXTENSAO'", "'CONGRESSO NACIONAL', 'ENSINO', 'EVENTOS'",
			"'SIMPOSIO REGIONAL', 'ENSINO', 'EVENTOS'", "'CURSO RAPIDO', 'ENSINO', 'EVENTOS'",
			"'OUTRO DESCONHECIDO', 'EVENTOS', 'EVENTOS'"})
	@DisplayName("Branch: normalizarCategoria cobre todas as palavras-chave")
	void deveNormalizarCategoria(String entrada, String fallback, String esperado) {
		String res = (String) invocarMetodoPrivado("normalizarCategoria", new Class<?>[]{String.class, String.class},
				entrada, fallback);
		assertEquals(esperado, res);
	}

	@Test
	@DisplayName("Branch: normalizarCategoria nula retorna fallback")
	void deveRetornarFallbackCategoriaNula() {
		String res = (String) invocarMetodoPrivado("normalizarCategoria", new Class<?>[]{String.class, String.class},
				null, "ENSINO");
		assertEquals("ENSINO", res);
	}

	@ParameterizedTest
	@CsvSource({"'DEFERIDO', 'DEFERIDO'", "'APROVADO', 'DEFERIDO'", "'INDEFERIDO', 'INDEFERIDO'",
			"'REJEITADO', 'INDEFERIDO'", "'RECUSADO', 'INDEFERIDO'", "'NEGADO', 'INDEFERIDO'", "'NAO', 'INDEFERIDO'",
			"'INCONCLUSIVO', 'AMBIGUO'"})
	@DisplayName("Branch: normalizarDecisao cobre todas as ramificações")
	void deveNormalizarDecisao(String entrada, String esperado) {
		String res = (String) invocarMetodoPrivado("normalizarDecisao", new Class<?>[]{String.class}, entrada);
		assertEquals(esperado, res);
	}

	@Test
	@DisplayName("Branch: normalizarDecisao nula retorna AMBIGUO")
	void deveRetornarAmbiguoDecisaoNula() {
		String res = (String) invocarMetodoPrivado("normalizarDecisao", new Class<?>[]{String.class}, (Object) null);
		assertEquals("AMBIGUO", res);
	}

	@Test
	@DisplayName("Branch: normalizarCargaHoraria cobre Number, String e nulos")
	void deveNormalizarCargaHoraria() {
		int r1 = (int) invocarMetodoPrivado("normalizarCargaHoraria", new Class<?>[]{Object.class, int.class}, 40, 10);
		int r2 = (int) invocarMetodoPrivado("normalizarCargaHoraria", new Class<?>[]{Object.class, int.class}, "20",
				10);
		int r3 = (int) invocarMetodoPrivado("normalizarCargaHoraria", new Class<?>[]{Object.class, int.class},
				"invalido", 10);
		int r4 = (int) invocarMetodoPrivado("normalizarCargaHoraria", new Class<?>[]{Object.class, int.class}, null,
				10);

		assertEquals(40, r1);
		assertEquals(20, r2);
		assertEquals(10, r3);
		assertEquals(10, r4);
	}

	@Test
	@DisplayName("Branch: normalizarScore cobre números, textos, limites e nulos")
	void deveNormalizarScore() {
		Double s1 = (Double) invocarMetodoPrivado("normalizarScore", new Class<?>[]{Object.class}, "0.85");
		Double s2 = (Double) invocarMetodoPrivado("normalizarScore", new Class<?>[]{Object.class}, 2.5);
		Double s3 = (Double) invocarMetodoPrivado("normalizarScore", new Class<?>[]{Object.class}, 0.0);
		Double s4 = (Double) invocarMetodoPrivado("normalizarScore", new Class<?>[]{Object.class}, (Object) null);
		Double s5 = (Double) invocarMetodoPrivado("normalizarScore", new Class<?>[]{Object.class}, "invalido");

		assertEquals(0.85, s1, 0.001);
		assertEquals(1.0, s2, 0.001);
		assertEquals(0.0, s3, 0.001);
		assertEquals(0.95, s4, 0.001);
		assertEquals(0.95, s5, 0.001);
	}

	@Test
	@DisplayName("Branch: sanitizarJson e fallback por Regex")
	void deveSanitizarJsonEFazerFallbackRegex() {
		String j1 = (String) invocarMetodoPrivado("sanitizarJson", new Class<?>[]{String.class}, "   ");
		String j2 = (String) invocarMetodoPrivado("sanitizarJson", new Class<?>[]{String.class}, (Object) null);
		assertEquals("{}", j1);
		assertEquals("{}", j2);

		String rawRegex = """
				"naturezaSugerida": "ACEX",
				"categoriaSugerida": "EXTENSAO",
				"artigoRegulamento": "Art. 14",
				"justificativaTecnica": "Atividade comunitaria",
				"decisaoIA": "DEFERIDO",
				"cargaHorariaAproveitavel": 30
				""";
		ParecerResponseDTO parsed = (ParecerResponseDTO) invocarMetodoPrivado("extrairCamposViaRegex",
				new Class<?>[]{String.class, String.class, String.class, int.class}, rawRegex, "ACC", "ENSINO", 10);

		assertNotNull(parsed);
		assertEquals("ACEX", parsed.naturezaSugerida());
		assertEquals("EXTENSAO", parsed.categoriaSugerida());
		assertEquals("DEFERIDO", parsed.decisaoIA());
	}

	@Test
	@DisplayName("Branch: normalizarTexto com corte de string e null")
	void deveNormalizarTexto() {
		String t1 = (String) invocarMetodoPrivado("normalizarTexto",
				new Class<?>[]{String.class, int.class, String.class}, null, 10, "padrao");
		String t2 = (String) invocarMetodoPrivado("normalizarTexto",
				new Class<?>[]{String.class, int.class, String.class}, "   ", 10, "padrao");
		String t3 = (String) invocarMetodoPrivado("normalizarTexto",
				new Class<?>[]{String.class, int.class, String.class}, "123456789", 5, "padrao");
		String t4 = (String) invocarMetodoPrivado("normalizarTexto",
				new Class<?>[]{String.class, int.class, String.class}, "curto", 10, "padrao");

		assertEquals("padrao", t1);
		assertEquals("padrao", t2);
		assertEquals("12345", t3);
		assertEquals("curto", t4);
	}
}
