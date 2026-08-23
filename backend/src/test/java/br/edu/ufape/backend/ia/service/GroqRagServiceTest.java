package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GroqRagServiceTest {

    private final GroqRagService service = new GroqRagService(null);

    @Test
    @DisplayName("Deve retornar parecer padrão quando a chave Groq não estiver configurada")
    void deveRetornarParecerPadraoSemChave() {
        ParecerResponseDTO parecer = service.gerarParecerComContextoRAG(
                "Monitoria", "UFAPE", "ACC", "ENSINO", 30, "Art. 12: Monitoria até 40h");

        assertNotNull(parecer);
        assertEquals("ACC", parecer.naturezaSugerida());
        assertEquals("ENSINO", parecer.categoriaSugerida());
        assertEquals(30, parecer.cargaHorariaAproveitavel());
        assertEquals("DEFERIDO", parecer.decisaoIA());
    }
}