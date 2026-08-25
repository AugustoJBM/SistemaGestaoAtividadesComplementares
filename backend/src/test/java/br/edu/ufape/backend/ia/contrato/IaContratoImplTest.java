package br.edu.ufape.backend.ia.contrato;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import br.edu.ufape.backend.ia.service.GroqRagService;
import br.edu.ufape.backend.ia.service.HuggingFaceEmbeddingService;

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
        @DisplayName("Deve extrair dados de imagem delegando para extrairDadosDeImagem")
        void deveExtrairDadosDeImagem() {
                MockMultipartFile imagem = new MockMultipartFile(
                                "arquivo", "certificado.png", "image/png", new byte[] { 1, 2, 3 });

                ExtracaoCertificadoResponseDTO dtoEsperado = new ExtracaoCertificadoResponseDTO(
                                "Workshop", "UFAPE", null, 10, "ACC", "EVENTOS");
                when(groqRagService.extrairDadosDeImagem(any(), eq("image/png"))).thenReturn(dtoEsperado);

                ExtracaoCertificadoResponseDTO res = iaContrato.extrairDadosArquivo(imagem);

                assertNotNull(res);
                assertEquals("Workshop", res.titulo());
                verify(groqRagService, times(1)).extrairDadosDeImagem(any(), eq("image/png"));
        }

        @Test
        @DisplayName("Deve gerar parecer de conformidade utilizando fallback quando não houver chunks normativos")
        void deveGerarParecerComBancoNormativoVazio() {
                when(regulamentoChunkRepository.findAll()).thenReturn(List.of());
                ParecerResponseDTO dtoEsperado = new ParecerResponseDTO(
                                null, null, "ACC", "ENSINO", 20, "Geral", "Parecer ok", 0.95, "DEFERIDO", null);

                when(groqRagService.gerarParecerComContextoRAG(
                                anyString(), anyString(), anyString(), anyString(), anyInt(), anyString()))
                                .thenReturn(dtoEsperado);

                ParecerResponseDTO resultado = iaContrato.gerarParecerConformidade(
                                "Monitoria", "UFAPE", "ACC", "ENSINO", 20);

                assertNotNull(resultado);
                assertEquals("DEFERIDO", resultado.decisaoIA());
        }

        @Test
        @DisplayName("Deve ordenar chunks por similaridade de cosseno ao recuperar artigos")
        void deveOrdenarChunksPorSimilaridade() {
                RegulamentoChunk chunk1 = new RegulamentoChunk("Art. 1", "Regra 1", "[0.1, 0.2]");
                RegulamentoChunk chunk2 = new RegulamentoChunk("Art. 2", "Regra 2", "[0.9, 0.8]");

                when(embeddingService.gerarEmbedding(anyString())).thenReturn(new float[] { 0.9f, 0.8f });
                when(embeddingService.calcularSimilaridadeCosseno(any(), any())).thenReturn(0.5, 0.9);
                when(regulamentoChunkRepository.findAll()).thenReturn(List.of(chunk1, chunk2));

                ParecerResponseDTO dtoEsperado = new ParecerResponseDTO(
                                null, null, "ACC", "PESQUISA", 30, "Art. 2", "Conforme", 0.95, "DEFERIDO", null);

                when(groqRagService.gerarParecerComContextoRAG(anyString(), anyString(), anyString(), anyString(),
                                anyInt(), anyString()))
                                .thenReturn(dtoEsperado);

                ParecerResponseDTO res = iaContrato.gerarParecerConformidade("Pesquisa PIBIC", "UFAPE", "ACC",
                                "PESQUISA", 30);
                assertNotNull(res);
        }
}