package br.edu.ufape.backend.atividadeTest.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.repository.ParecerConformidadeRepository;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.AuditoriaConformidadeService;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;

@ExtendWith(MockitoExtension.class)
class AtividadeComplementarServiceDiretorioCertificadosTest {

    private static final Path FALLBACK = Path.of("certificados").toAbsolutePath().normalize();

    @TempDir
    Path diretorioConfigurado;

    @Mock
    private AtividadeComplementarRepository atividadeRepository;

    @Mock
    private UsuarioContrato usuarioContrato;

    @Mock
    private ArmazenamentoCertificadoService armazenamentoCertificadoService;

    @Mock
    private AuditoriaConformidadeService auditoriaConformidadeService;

    @Mock
    private ParecerConformidadeRepository parecerConformidadeRepository;

    private Path diretorioResolvido(String configuracao) {
        AtividadeComplementarService service = new AtividadeComplementarService(
                atividadeRepository,
                usuarioContrato,
                armazenamentoCertificadoService,
                auditoriaConformidadeService,
                parecerConformidadeRepository,
                configuracao);
        return (Path) ReflectionTestUtils.getField(service, "diretorioCertificados");
    }

    @Test
    @DisplayName("Deve manter o diretorio configurado quando a configuracao e valida")
    void deveManterDiretorioConfigurado() {
        Path esperado = diretorioConfigurado.toAbsolutePath().normalize();

        assertEquals(esperado, diretorioResolvido(diretorioConfigurado.toString()));
    }

    @Test
    @DisplayName("Deve usar o diretorio padrao quando a configuracao e nula")
    void deveUsarFallbackQuandoConfiguracaoNula() {
        assertEquals(FALLBACK, diretorioResolvido(null));
    }

    @Test
    @DisplayName("Deve usar o diretorio padrao quando a configuracao e vazia")
    void deveUsarFallbackQuandoConfiguracaoVazia() {
        assertEquals(FALLBACK, diretorioResolvido(""));
    }

    @Test
    @DisplayName("Deve usar o diretorio padrao quando a configuracao contem apenas espacos")
    void deveUsarFallbackQuandoConfiguracaoEmBranco() {
        assertEquals(FALLBACK, diretorioResolvido("   "));
    }
}
