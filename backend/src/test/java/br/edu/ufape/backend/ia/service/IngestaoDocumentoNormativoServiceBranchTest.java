package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestaoDocumentoNormativoServiceBranchTest {

	@Mock
	private RegulamentoChunkRepository repository;
	@Mock
	private HuggingFaceEmbeddingService embeddingService;

	private IngestaoDocumentoNormativoService service;

	@BeforeEach
	void setUp() {
		service = new IngestaoDocumentoNormativoService(new ObjectMapper(), repository, embeddingService);
	}

	@Test
	@DisplayName("Branch: Ingestão com arquivo nulo, vazio ou sem texto")
	void deveTratarEntradasInvalidas() {
		IngestaoNormativaResponseDTO rNull = service.ingerirDocumentoNormativo(null, false);
		assertEquals("ERRO", rNull.status());

		MockMultipartFile vazio = new MockMultipartFile("arquivo", "vazio.txt", "text/plain", new byte[0]);
		IngestaoNormativaResponseDTO rVazio = service.ingerirDocumentoNormativo(vazio, false);
		assertEquals("ERRO", rVazio.status());

		MockMultipartFile semTexto = new MockMultipartFile("arquivo", "espacos.txt", "text/plain", "   ".getBytes());
		IngestaoNormativaResponseDTO rSemTexto = service.ingerirDocumentoNormativo(semTexto, false);
		assertEquals("ERRO", rSemTexto.status());
	}

	@Test
	@DisplayName("Branch: processarRespostaJsonOuRegex com array JSON e fallback Regex")
	void deveProcessarRespostaJsonOuRegex() {
		List<RegulamentoChunk> listaNula = ReflectionTestUtils.invokeMethod(service, "processarRespostaJsonOuRegex",
				(String) null);
		assertTrue(listaNula.isEmpty());

		String jsonValido = "[{\"artigo\": \"Art. 12\", \"conteudoTexto\": \"Atividade de Ensino e Monitoria valida\"}]";
		List<RegulamentoChunk> listaArray = ReflectionTestUtils.invokeMethod(service, "processarRespostaJsonOuRegex",
				jsonValido);
		assertEquals(1, listaArray.size());
		assertEquals("Art. 12", listaArray.get(0).getArtigo());

		String jsonRegex = "\"artigo\": \"Art. 15\", \"conteudoTexto\": \"Participacao em eventos cientificos\"";
		List<RegulamentoChunk> listaRegex = ReflectionTestUtils.invokeMethod(service, "processarRespostaJsonOuRegex",
				jsonRegex);
		assertEquals(1, listaRegex.size());
		assertEquals("Art. 15", listaRegex.get(0).getArtigo());
	}

	@Test
	@DisplayName("Branch: extrairRegrasDiretasDoTexto cobre seções, resoluções e quadro 5")
	void deveExtrairRegrasDiretasDoTexto() {
		String texto = """
				7.1 ATIVIDADES COMPLEMENTARES
				Carga horaria de monitoria com aproveitamento maximo de 40 horas semestrais.
				Resolucao N 08/2024
				Regulamenta o aproveitamento de atividades de extensao na UFAPE.
				Quadro 5 - Sintese da Carga Horaria
				""";
		List<RegulamentoChunk> chunks = ReflectionTestUtils.invokeMethod(service, "extrairRegrasDiretasDoTexto", texto);
		assertFalse(chunks.isEmpty());
		assertTrue(chunks.stream().anyMatch(c -> c.getArtigo().contains("Quadro 5")));
	}

	@Test
	@DisplayName("Branch: extrairSecoesNormativas com e sem blocos mapeados")
	void deveExtrairSecoesNormativas() {
		String textoLongo = "7.1 ATIVIDADES COMPLEMENTARES " + "A".repeat(150);
		List<String> b1 = ReflectionTestUtils.invokeMethod(service, "extrairSecoesNormativas", textoLongo);
		assertFalse(b1.isEmpty());

		String textoSemMatch = "Texto generico de apresentacao do documento institucional " + "B".repeat(200);
		List<String> b2 = ReflectionTestUtils.invokeMethod(service, "extrairSecoesNormativas", textoSemMatch);
		assertFalse(b2.isEmpty());
	}

	@Test
	@DisplayName("Branch: Ingestão com substituição e persistência no banco")
	void deveIngerirDocumentoComSubstituicao() {
		ReflectionTestUtils.setField(service, "apiKey", "");
		when(embeddingService.gerarEmbedding(any())).thenReturn(new float[384]);

		String texto = "7.1 ATIVIDADES COMPLEMENTARES\nRegra oficial com mais de vinte caracteres para ser valida.";
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "norma.txt", "text/plain", texto.getBytes());

		IngestaoNormativaResponseDTO res = service.ingerirDocumentoNormativo(arquivo, true);
		assertEquals("SUCESSO", res.status());
		verify(repository).deleteAll();
		verify(repository, atLeastOnce()).save(any());
	}

	@Test
	@DisplayName("Branch: Ingestão sem chunks válidos retorna ERRO")
	void deveRetornarErroSemChunksValidos() {
		ReflectionTestUtils.setField(service, "apiKey", "");
		MockMultipartFile arquivo = new MockMultipartFile("arquivo", "sem_regras.txt", "text/plain",
				"Texto curto sem nenhuma regra".getBytes());

		IngestaoNormativaResponseDTO res = service.ingerirDocumentoNormativo(arquivo, false);
		assertEquals("ERRO", res.status());
	}
}
