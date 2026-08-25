package br.edu.ufape.backend.ia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

class GroqRagServiceTest {

    private final GroqRagService service = new GroqRagService(null);

    @Test
    @DisplayName("Deve retornar parecer inconclusivo AMBIGUO quando a chave Groq não estiver configurada")
    void deveRetornarParecerInconclusivoSemChave() {
        ParecerResponseDTO parecer = service.gerarParecerComContextoRAG(
                "Monitoria", "UFAPE", "ACC", "ENSINO", 30, "Art. 12: Monitoria até 40h");

        assertNotNull(parecer);
        assertEquals("ACC", parecer.naturezaSugerida());
        assertEquals("ENSINO", parecer.categoriaSugerida());
        assertEquals("AMBIGUO", parecer.decisaoIA());
        assertEquals(0.0, parecer.scoreConfianca());
    }
}