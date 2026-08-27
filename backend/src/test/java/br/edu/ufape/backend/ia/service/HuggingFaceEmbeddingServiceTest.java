package br.edu.ufape.backend.ia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HuggingFaceEmbeddingServiceTest {

	private HuggingFaceEmbeddingService service;

	@BeforeEach
	void setUp() {
		service = new HuggingFaceEmbeddingService();
	}

	@Test
	@DisplayName("Branch: gerarEmbedding sem API key retorna vetor zerado de 384 posicoes")
	void deveRetornarVetorZeradoSemApiKey() {
		ReflectionTestUtils.setField(service, "apiKey", "");
		float[] res = service.gerarEmbedding("texto");
		assertEquals(384, res.length);
		assertEquals(0.0f, res[0]);
	}

	@Test
	@DisplayName("Branch: calcularSimilaridadeCosseno com vetores nulos ou tamanhos diferentes retorna 0.0")
	void deveRetornarZeroComVetoresInvalidos() {
		assertEquals(0.0, service.calcularSimilaridadeCosseno(null, new float[]{1.0f}));
		assertEquals(0.0, service.calcularSimilaridadeCosseno(new float[]{1.0f}, null));
		assertEquals(0.0, service.calcularSimilaridadeCosseno(new float[]{1.0f}, new float[]{1.0f, 2.0f}));
	}

	@Test
	@DisplayName("Branch: calcularSimilaridadeCosseno com vetores zerados retorna 0.0")
	void deveRetornarZeroComVetoresZerados() {
		float[] v1 = new float[]{0.0f, 0.0f};
		float[] v2 = new float[]{1.0f, 1.0f};
		assertEquals(0.0, service.calcularSimilaridadeCosseno(v1, v2));
	}

	@Test
	@DisplayName("Branch: calcularSimilaridadeCosseno com vetores ortogonais e paralelos")
	void deveCalcularSimilaridadeCorretamente() {
		float[] v1 = new float[]{1.0f, 0.0f};
		float[] v2 = new float[]{0.0f, 1.0f};
		assertEquals(0.0, service.calcularSimilaridadeCosseno(v1, v2), 0.001);

		float[] v3 = new float[]{1.0f, 1.0f};
		float[] v4 = new float[]{1.0f, 1.0f};
		assertEquals(1.0, service.calcularSimilaridadeCosseno(v3, v4), 0.001);
	}
}
