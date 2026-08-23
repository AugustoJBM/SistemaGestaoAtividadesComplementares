package br.edu.ufape.backend.ia.service;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.ia.contrato.IaContrato;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaConformidadeServiceTest {

    @Mock
    private IaContrato iaContrato;

    @Mock
    private ParecerConformidadeRepository parecerRepository;

    @InjectMocks
    private AuditoriaConformidadeService auditoriaService;

    private AtividadeComplementar atividade;

    @BeforeEach
    void setUp() {
        atividade = mock(AtividadeComplementar.class);
        when(atividade.getId()).thenReturn(1L);
        when(atividade.getTitulo()).thenReturn("Monitoria de Programação");
        when(atividade.getInstituicaoResponsavel()).thenReturn("UFAPE");
        when(atividade.getNatureza()).thenReturn(Natureza.ACC);
        when(atividade.getCategoria()).thenReturn(Categoria.ENSINO);
        when(atividade.getCargaHorariaEmHoras()).thenReturn(30);
    }

    @Test
    @DisplayName("Deve auditar atividade gerando e gravando parecer com sucesso")
    void deveAuditarAtividadeEGravandoParecer() {
        ParecerResponseDTO dtoMock = new ParecerResponseDTO(
                null, null, "ACC", "ENSINO", 30, "Art. 12, § 1º",
                "Atividade compatível com monitoria de ensino.", 0.95, "DEFERIDO", null);

        when(iaContrato.gerarParecerConformidade(anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(dtoMock);
        when(parecerRepository.findByAtividadeId(1L)).thenReturn(Optional.empty());
        when(parecerRepository.save(any(ParecerConformidade.class))).thenAnswer(i -> i.getArgument(0));

        ParecerConformidade resultado = auditoriaService.auditarAtividade(atividade);

        assertNotNull(resultado);
        assertEquals("ACC", resultado.getNaturezaSugerida());
        assertEquals("Art. 12, § 1º", resultado.getArtigoRegulamento());
        assertEquals(ParecerConformidade.DecisaoIA.DEFERIDO, resultado.getDecisaoIA());
        verify(parecerRepository, times(1)).save(any(ParecerConformidade.class));
        verify(iaContrato, times(1)).gerarParecerConformidade(
                eq("Monitoria de Programação"), eq("UFAPE"), eq("ACC"), eq("ENSINO"), eq(30));
    }
}