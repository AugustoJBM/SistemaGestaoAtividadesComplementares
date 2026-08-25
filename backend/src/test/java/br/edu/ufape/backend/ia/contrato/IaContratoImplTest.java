package br.edu.ufape.backend.ia.contrato;

import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import br.edu.ufape.backend.ia.service.GroqRagService;
import br.edu.ufape.backend.ia.service.HuggingFaceEmbeddingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IaContratoImplTest {

    @Mock
    private GroqRagService groqRagService;

    @Mock
    private RegulamentoChunkRepository regulamentoChunkRepository;

    @Mock
    private HuggingFaceEmbeddingService embeddingService;

    @InjectMocks
    private IaContratoImpl iaContrato;

    @Test
    @DisplayName("Deve extrair dados de arquivo de texto/imagem delegando para GroqRagService")
    void deveExtrairDadosDeArquivoTexto() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "arquivo", "certificado.txt", "text/plain", "Curso de Java 40h".getBytes());
        ExtracaoCertificadoResponseDTO dtoEsperado = new ExtracaoCertificadoResponseDTO(
                "Curso de Java", "UFAPE", null, 40, "ACC", "ENSINO");

        when(groqRagService.extrairDadosDeTexto(anyString())).thenReturn(dtoEsperado);

        ExtracaoCertificadoResponseDTO resultado = iaContrato.extrairDadosArquivo(arquivo);

        assertNotNull(resultado);
        assertEquals("Curso de Java", resultado.titulo());
        assertEquals(40, resultado.cargaHoraria());
        verify(groqRagService, times(1)).extrairDadosDeTexto(anyString());
    }

    @Test
    @DisplayName("Deve gerar parecer de conformidade recuperando contexto RAG")
    void deveGerarParecerConformidadeComRAG() {
        RegulamentoChunk chunk = new RegulamentoChunk(
                "Art. 12", "Monitoria vale até 40h", "[0.1, 0.2]");
        ParecerResponseDTO dtoEsperado = new ParecerResponseDTO(
                null, null, "ACC", "ENSINO", 30, "Art. 12", "Conforme norma", 0.95, "DEFERIDO", null);

        when(embeddingService.gerarEmbedding(anyString())).thenReturn(new float[384]);
        when(regulamentoChunkRepository.findAll()).thenReturn(List.of(chunk));
        when(groqRagService.gerarParecerComContextoRAG(anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyString()))
                .thenReturn(dtoEsperado);

        ParecerResponseDTO resultado = iaContrato.gerarParecerConformidade(
                "Monitoria", "UFAPE", "ACC", "ENSINO", 30);

        assertNotNull(resultado);
        assertEquals("DEFERIDO", resultado.decisaoIA());
        assertEquals("Art. 12", resultado.artigoRegulamento());
        verify(embeddingService, times(1)).gerarEmbedding(anyString());
        verify(groqRagService, times(1)).gerarParecerComContextoRAG(
                eq("Monitoria"), eq("UFAPE"), eq("ACC"), eq("ENSINO"), eq(30), anyString());
    }
}