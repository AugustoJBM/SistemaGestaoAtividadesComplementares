package br.edu.ufape.backend.ia.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class HuggingFaceEmbeddingServiceTest {

    @Spy
    private HuggingFaceEmbeddingService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve calcular similaridade cosseno perfeitamente entre vetores iguais")
    void deveCalcularSimilaridadeCossenoVetoresIguais() {
        float[] vetorA = new float[]{1.0f, 0.0f, 0.0f};
        float[] vetorB = new float[]{1.0f, 0.0f, 0.0f};

        double similaridade = service.calcularSimilaridadeCosseno(vetorA, vetorB);

        assertEquals(1.0, similaridade, 0.001);
    }

    @Test
    @DisplayName("Deve retornar 0 para vetores ortogonais")
    void deveCalcularSimilaridadeCossenoOrtogonais() {
        float[] vetorA = new float[]{1.0f, 0.0f, 0.0f};
        float[] vetorB = new float[]{0.0f, 1.0f, 0.0f};

        double similaridade = service.calcularSimilaridadeCosseno(vetorA, vetorB);

        assertEquals(0.0, similaridade, 0.001);
    }

    @Test
    @DisplayName("Deve retornar vetor neutro de 384 dimensões quando não houver chave de API")
    void deveRetornarVetorPadraoSemChave() {
        float[] resultado = service.gerarEmbedding("Texto de teste");

        assertNotNull(resultado, "O resultado não deveria ser nulo.");
        assertEquals(384, resultado.length);
    }
}