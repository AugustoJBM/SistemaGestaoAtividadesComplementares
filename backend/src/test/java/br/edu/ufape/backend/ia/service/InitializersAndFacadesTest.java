package br.edu.ufape.backend.ia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.ParecerConformidade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AtividadeDataInitializerService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.atividade.service.AvaliacaoAtividadeService;
import br.edu.ufape.backend.atividade.service.ProgressoService;
import br.edu.ufape.backend.certificados.facade.CertificadoFacade;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.IngestaoNormativaResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.exception.IaProcessamentoException;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;
import br.edu.ufape.backend.ia.facade.RegulamentoFacade;
import br.edu.ufape.backend.ia.repository.RegulamentoChunkRepository;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;
import br.edu.ufape.backend.usuario.service.UsuarioDataInitializerService;

@ExtendWith(MockitoExtension.class)
class InitializersAndFacadesTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AtividadeComplementarRepository atividadeRepository;
    @Mock
    private UsuarioContrato usuarioContrato;
    @Mock
    private RegulamentoChunkRepository regulamentoRepository;
    @Mock
    private HuggingFaceEmbeddingService embeddingService;

    @Test
    @DisplayName("UsuarioDataInitializerService deve criar contas caso não existam")
    void deveExecutarUsuarioDataInitializer() {
        when(passwordEncoder.encode(any())).thenReturn("hash");
        when(usuarioRepository.existsByEmailIgnoreCase(any())).thenReturn(false);

        UsuarioDataInitializerService init = new UsuarioDataInitializerService(usuarioRepository, passwordEncoder);
        init.run(null);

        verify(usuarioRepository, times(4)).save(any());
    }

    @Test
    @DisplayName("AtividadeDataInitializerService deve popular atividades no perfil dev se o banco estiver vazio")
    void deveExecutarAtividadeDataInitializer() {
        when(atividadeRepository.count()).thenReturn(0L);
        Estudante estudante = new Estudante("Lucas", "aluno1@ufape.edu.br", "hash");
        when(usuarioContrato.buscarPorEmail(any())).thenReturn(Optional.of(estudante));

        AtividadeDataInitializerService init = new AtividadeDataInitializerService(atividadeRepository, usuarioContrato);
        init.run(null);

        verify(atividadeRepository, atLeast(10)).save(any());
    }

    @Test
    @DisplayName("RegulamentoDataInitializerService não deve duplicar registros se já populado")
    void deveIgnorarRegulamentoDataInitializerSeJaExistiremNormas() {
        when(regulamentoRepository.count()).thenReturn(5L);
        RegulamentoDataInitializerService init = new RegulamentoDataInitializerService(regulamentoRepository, embeddingService);
        init.run(null);

        verify(regulamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("IaCertificadoFacade deve delegar corretamente para os serviços de auditoria")
    void deveDelegarIaCertificadoFacade() {
        AuditoriaConformidadeService auditoria = mock(AuditoriaConformidadeService.class);
        AtividadeComplementarService atvService = mock(AtividadeComplementarService.class);
        IaCertificadoFacade facade = new IaCertificadoFacade(auditoria, atvService);

        MockMultipartFile file = new MockMultipartFile("arquivo", "cert.pdf", "application/pdf", new byte[]{1});
        when(auditoria.extrairDadosArquivo(file)).thenReturn(new ExtracaoCertificadoResponseDTO("T", "I", null, 10, "ACC", "ENSINO"));
        when(auditoria.contarAvaliadas()).thenReturn(5L);
        when(auditoria.contarConcordancias()).thenReturn(4L);
        when(auditoria.calcularTempoMedioMs()).thenReturn(100.0);

        AtividadeComplementar atv = new AtividadeComplementar();
        ParecerConformidade parecer = new ParecerConformidade();
        parecer.setAtividade(atv);
        parecer.setScoreConfianca(0.95);
        parecer.setDecisaoIA(ParecerConformidade.DecisaoIA.DEFERIDO);
        parecer.setTempoProcessamentoMs(50L);
        when(atvService.buscarPorId(1L)).thenReturn(atv);
        when(auditoria.auditarOuObterParecer(atv)).thenReturn(parecer);

        assertNotNull(facade.extrairDadosCertificado(file));
        assertEquals(5L, facade.contarAvaliadas());
        assertEquals(4L, facade.contarConcordancias());
        assertEquals(100.0, facade.calcularTempoMedioMs());
        assertNotNull(facade.obterOuGerarParecer(1L));
    }

    @Test
    @DisplayName("RegulamentoFacade e CertificadoFacade devem delegar chamadas")
    void deveDelegarOutrasFacades() {
        IngestaoDocumentoNormativoService ingestao = mock(IngestaoDocumentoNormativoService.class);
        RegulamentoFacade regFacade = new RegulamentoFacade(ingestao);
        MockMultipartFile file = new MockMultipartFile("arquivo", "norma.pdf", "application/pdf", new byte[]{1});

        when(ingestao.ingerirDocumentoNormativo(file, false))
                .thenReturn(new IngestaoNormativaResponseDTO("norma.pdf", 2, "SUCESSO", "ok"));
        when(ingestao.listarChunks()).thenReturn(List.of(new RegulamentoChunkResponseDTO(1L, "Art. 1", "Texto")));

        assertNotNull(regFacade.ingerirDocumentoNormativo(file, false));
        assertEquals(1, regFacade.listarChunks().size());

        AtividadeComplementarService atvService = mock(AtividadeComplementarService.class);
        CertificadoFacade certFacade = new CertificadoFacade(atvService);
        Resource res = new ByteArrayResource(new byte[]{1});
        when(atvService.obterArquivoCertificado(1L, "aluno@ufape.edu.br")).thenReturn(res);

        assertEquals(res, certFacade.obterCertificado(1L, "aluno@ufape.edu.br"));
    }

    @Test
    @DisplayName("IaProcessamentoException deve encapsular mensagem e causa")
    void deveInstanciarIaProcessamentoException() {
        IaProcessamentoException ex1 = new IaProcessamentoException("Erro IA");
        Throwable causa = new RuntimeException("Timeout");
        IaProcessamentoException ex2 = new IaProcessamentoException("Erro IA", causa);

        assertEquals("Erro IA", ex1.getMessage());
        assertEquals(causa, ex2.getCause());
    }
}