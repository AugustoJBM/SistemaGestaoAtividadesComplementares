package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.dto.AvaliacaoDecisaoRequestDTO;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoAvaliador;
import br.edu.ufape.backend.atividade.model.ParecerConformidade.DecisaoIA;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.atividade.service.AvaliacaoAtividadeService;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

@ExtendWith(MockitoExtension.class)
class AvaliacaoAtividadeServiceTest {

    @Mock
    private AtividadeComplementarRepository atividadeRepository;

    @Mock
    private AuditoriaConformidadeService auditoriaConformidadeService;

    @Mock
    private ParecerConformidadeRepository parecerRepository;

    @InjectMocks
    private AvaliacaoAtividadeService service;

    @Test
    @DisplayName("Deve registrar deferimento do avaliador com concordância quando a IA deferiu")
    void deveRegistrarDeferimentoComConcordancia() {
        AtividadeComplementar atividade = new AtividadeComplementar();
        atividade.setId(1L);
        atividade.setStatus(StatusAtividade.PENDENTE);

        ParecerConformidade parecer = new ParecerConformidade();
        parecer.setId(10L);
        parecer.setAtividade(atividade);
        parecer.setDecisaoIA(DecisaoIA.DEFERIDO);
        parecer.setScoreConfianca(0.95);
        parecer.setTempoProcessamentoMs(100L);

        AvaliacaoDecisaoRequestDTO request = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Certificado autêntico e válido");

        when(atividadeRepository.findById(1L)).thenReturn(Optional.of(atividade));
        when(auditoriaConformidadeService.auditarOuObterParecer(atividade)).thenReturn(parecer);
        when(parecerRepository.save(any(ParecerConformidade.class))).thenAnswer(i -> i.getArgument(0));

        ParecerResponseDTO resultado = service.avaliarAtividade(1L, request);

        assertNotNull(resultado);
        assertEquals(StatusAtividade.APROVADA, atividade.getStatus());
        assertEquals(DecisaoAvaliador.DEFERIDO, parecer.getDecisaoFinalAvaliador());
        assertTrue(parecer.getAvaliadorConcordouComIA());

        verify(atividadeRepository, times(1)).save(atividade);
        verify(parecerRepository, times(1)).save(parecer);
    }

    @Test
    @DisplayName("Deve registrar indeferimento com discordância quando a IA havia deferido")
    void deveRegistrarIndeferimentoComDiscordancia() {
        AtividadeComplementar atividade = new AtividadeComplementar();
        atividade.setId(2L);
        atividade.setStatus(StatusAtividade.PENDENTE);

        ParecerConformidade parecer = new ParecerConformidade();
        parecer.setId(20L);
        parecer.setAtividade(atividade);
        parecer.setDecisaoIA(DecisaoIA.DEFERIDO);
        parecer.setScoreConfianca(0.90);
        parecer.setTempoProcessamentoMs(120L);

        AvaliacaoDecisaoRequestDTO request = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.INDEFERIDO, "Documento sem assinatura");

        when(atividadeRepository.findById(2L)).thenReturn(Optional.of(atividade));
        when(auditoriaConformidadeService.auditarOuObterParecer(atividade)).thenReturn(parecer);
        when(parecerRepository.save(any(ParecerConformidade.class))).thenAnswer(i -> i.getArgument(0));

        ParecerResponseDTO resultado = service.avaliarAtividade(2L, request);

        assertNotNull(resultado);
        assertEquals(StatusAtividade.REJEITADA, atividade.getStatus());
        assertEquals(DecisaoAvaliador.INDEFERIDO, parecer.getDecisaoFinalAvaliador());
        assertFalse(parecer.getAvaliadorConcordouComIA());

        verify(atividadeRepository, times(1)).save(atividade);
        verify(parecerRepository, times(1)).save(parecer);
    }

    @Test
    @DisplayName("Deve recusar avaliação quando a atividade já estiver em estado terminal APROVADA")
    void deveRecusarAvaliacaoEmAtividadeJaAprovada() {
        AtividadeComplementar atividade = new AtividadeComplementar();
        atividade.setId(3L);
        atividade.setStatus(StatusAtividade.APROVADA);

        AvaliacaoDecisaoRequestDTO request = new AvaliacaoDecisaoRequestDTO(
                DecisaoAvaliador.DEFERIDO, "Segunda tentativa");

        when(atividadeRepository.findById(3L)).thenReturn(Optional.of(atividade));

        assertThrows(IllegalArgumentException.class, () -> service.avaliarAtividade(3L, request));
        verify(parecerRepository, never()).save(any());
    }
}