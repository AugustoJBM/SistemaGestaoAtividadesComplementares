package br.edu.ufape.backend.ia.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;

@Service
public class HuggingFaceEmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceEmbeddingService.class);
    private static final String MODEL_URL = "https://api-inference.huggingface.co/pipeline/feature-extraction/sentence-transformers/all-MiniLM-L6-v2";

    @Value("${huggingface.api.key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HuggingFaceEmbeddingService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate(factory);
    }

    public float[] gerarEmbedding(String texto) {
        if (apiKey == null || apiKey.isBlank()) {
            return new float[384];
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of("inputs", List.of(texto));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(MODEL_URL, entity, String.class);
            double[][] matriz = objectMapper.readValue(response.getBody(), double[][].class);
            float[] embedding = new float[matriz[0].length];
            for (int i = 0; i < matriz[0].length; i++) {
                embedding[i] = (float) matriz[0][i];
            }
            return embedding;
        } catch (Exception e) {
            log.warn("Falha ao gerar embeddings via Hugging Face: {}", e.getMessage());
            return new float[384];
        }
    }

    public double calcularSimilaridadeCosseno(float[] vetorA, float[] vetorB) {
        if (vetorA == null || vetorB == null || vetorA.length != vetorB.length) return 0.0;
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vetorA.length; i++) {
            dotProduct += vetorA[i] * vetorB[i];
            normA += vetorA[i] * vetorA[i];
            normB += vetorB[i] * vetorB[i];
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}