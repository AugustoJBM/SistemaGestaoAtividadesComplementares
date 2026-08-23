package br.edu.ufape.backend.ia.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;

@Component
public class RegulamentoDataInitializerService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RegulamentoDataInitializerService.class);

    private final RegulamentoChunkRepository repository;
    private final HuggingFaceEmbeddingService embeddingService;

    public RegulamentoDataInitializerService(RegulamentoChunkRepository repository,
            HuggingFaceEmbeddingService embeddingService) {
        this.repository = repository;
        this.embeddingService = embeddingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (repository.count() > 0) {
                return;
            }

            log.info("Inicializando base de regulamentos padrão da UFAPE...");

            List<RegulamentoChunk> chunks = List.of(
                    new RegulamentoChunk("Art. 12, §1º",
                            "Atividades de Ensino e Monitoria Acadêmica: Válidas como ACC com limite de aproveitamento máximo de 40 horas por semestre letivo.",
                            ""),
                    new RegulamentoChunk("Art. 13",
                            "Atividades de Pesquisa e Iniciação Científica (PIBIC/PIBITI): Válidas como ACC com limite máximo de 60 horas comprovadas por projeto.",
                            ""),
                    new RegulamentoChunk("Art. 14, §2º",
                            "Ações Contínuas de Extensão Universitária e Projetos Comunitários: Exclusivas para cumprimento da carga horária de ACEX (exigência total de 320h).",
                            ""),
                    new RegulamentoChunk("Art. 15",
                            "Participação em Eventos Científicos, Congressos, Simpósios e Seminários: Válidos como ACC na categoria EVENTOS, com limite acumulado de 30 horas.",
                            ""),
                    new RegulamentoChunk("Art. 4º",
                            "Requisitos Formais: O certificado deve conter identificação institucional clara, assinatura responsável, carga horária e período de realização.",
                            ""));

            for (RegulamentoChunk chunk : chunks) {
                try {
                    float[] vet = embeddingService.gerarEmbedding(chunk.getConteudoTexto());
                    chunk.setEmbeddingVetor(Arrays.toString(vet));
                } catch (Exception e) {
                    log.warn("Não foi possível gerar embedding para chunk no startup: {}", e.getMessage());
                }
                repository.save(chunk);
            }

            log.info("Base de regulamentos inicializada com {} registros.", chunks.size());
        } catch (Exception e) {
            log.error("Falha não-bloqueante na inicialização de dados de regulamento: {}", e.getMessage());
        }
    }
}