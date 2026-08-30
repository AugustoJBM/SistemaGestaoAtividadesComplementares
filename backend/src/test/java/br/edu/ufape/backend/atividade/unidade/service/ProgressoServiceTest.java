package br.edu.ufape.backend.atividade.unidade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.backend.atividade.config.ProgressoProperties;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.atividade.service.ProgressoService;
import br.edu.ufape.backend.certificado.model.Certificado;
import br.edu.ufape.backend.curso.contrato.CursoContrato;
import br.edu.ufape.backend.curso.dto.CursoDTO;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Estudante;

@ExtendWith(MockitoExtension.class)
class ProgressoServiceTest {

	@Mock
	private UsuarioContrato usuarioContrato;

	@Mock
	private AtividadeComplementarRepository atividadeComplementarRepository;

	@Mock
	private CursoContrato cursoContrato;

	private ProgressoProperties progressoProperties;
	private ProgressoService service;

	private final String emailEstudante = "estudante@ufape.edu.br";
	private Estudante estudante;

	@BeforeEach
	void setUp() {
		estudante = new Estudante("Lucas Silva", emailEstudante, "senhaHash");
		estudante.setId(1L);

		progressoProperties = new ProgressoProperties();
		progressoProperties.getAcc().setHorasExigidas(90);
		progressoProperties.getAcex().setHorasExigidas(320);

		service = new ProgressoService(usuarioContrato, atividadeComplementarRepository, progressoProperties,
				cursoContrato);
	}

	private AtividadeComplementar criarAtividade(Natureza natureza, int horas, StatusAtividade status) {
		Certificado cert = new Certificado("doc.pdf", "application/pdf", 1024L, "/path/doc.pdf");
		AtividadeComplementar atv = new AtividadeComplementar("Atividade Teste", "UFAPE", LocalDate.now(), horas,
				natureza, Categoria.ENSINO, cert, estudante);
		atv.setStatus(status);
		return atv;
	}

	@Test
    @DisplayName("Deve calcular progresso zerado quando estudante nao possuir atividades")
    void deveCalcularProgressoZeradoSemAtividades() {
        when(usuarioContrato.buscarPorEmail(emailEstudante)).thenReturn(Optional.of(estudante));
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACC))
                .thenReturn(Collections.emptyList());
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACEX))
                .thenReturn(Collections.emptyList());

        ProgressoResponseDTO dto = service.obterProgresso(emailEstudante);

        assertNotNull(dto);
        assertEquals(0, dto.getAcc().getHorasAcumuladas());
        assertEquals(0, dto.getAcc().getHorasPendentes());
        assertEquals(90, dto.getAcc().getHorasExigidas());
        assertEquals(0, dto.getAcc().getPercentualConcluido());

        assertEquals(0, dto.getAcex().getHorasAcumuladas());
        assertEquals(0, dto.getAcex().getHorasPendentes());
        assertEquals(320, dto.getAcex().getHorasExigidas());
        assertEquals(0, dto.getAcex().getPercentualConcluido());
    }

	@Test
    @DisplayName("Deve somar horas aprovadas e pendentes separadamente para ACC e ACEX")
    void deveSomarHorasAprovadasEPendentes() {
        when(usuarioContrato.buscarPorEmail(emailEstudante)).thenReturn(Optional.of(estudante));

        List<AtividadeComplementar> atividadesAcc = List.of(
                criarAtividade(Natureza.ACC, 30, StatusAtividade.APROVADA),
                criarAtividade(Natureza.ACC, 15, StatusAtividade.APROVADA),
                criarAtividade(Natureza.ACC, 20, StatusAtividade.PENDENTE),
                criarAtividade(Natureza.ACC, 10, StatusAtividade.REJEITADA));

        List<AtividadeComplementar> atividadesAcex = List.of(
                criarAtividade(Natureza.ACEX, 100, StatusAtividade.APROVADA),
                criarAtividade(Natureza.ACEX, 50, StatusAtividade.PENDENTE));

        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACC))
                .thenReturn(atividadesAcc);
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACEX))
                .thenReturn(atividadesAcex);

        ProgressoResponseDTO dto = service.obterProgresso(emailEstudante);

        assertNotNull(dto);
        assertEquals(45, dto.getAcc().getHorasAcumuladas());
        assertEquals(20, dto.getAcc().getHorasPendentes());
        assertEquals(90, dto.getAcc().getHorasExigidas());
        assertEquals(50, dto.getAcc().getPercentualConcluido());

        assertEquals(100, dto.getAcex().getHorasAcumuladas());
        assertEquals(50, dto.getAcex().getHorasPendentes());
        assertEquals(320, dto.getAcex().getHorasExigidas());
        assertEquals(31, dto.getAcex().getPercentualConcluido());
    }

	@Test
    @DisplayName("Deve limitar percentual maximo em 100 quando horas acumuladas excederem a meta")
    void deveLimitarPercentualEmCem() {
        when(usuarioContrato.buscarPorEmail(emailEstudante)).thenReturn(Optional.of(estudante));

        List<AtividadeComplementar> atividadesAcc = List.of(
                criarAtividade(Natureza.ACC, 120, StatusAtividade.APROVADA));

        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACC))
                .thenReturn(atividadesAcc);
        when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudante, Natureza.ACEX))
                .thenReturn(Collections.emptyList());

        ProgressoResponseDTO dto = service.obterProgresso(emailEstudante);

        assertEquals(120, dto.getAcc().getHorasAcumuladas());
        assertEquals(100, dto.getAcc().getPercentualConcluido());
    }

	@Test
	@DisplayName("Deve lancar AcessoNegadoAtividadeException se usuario for administrador ou avaliador")
	void deveLancarExcecaoParaNaoEstudante() {
		String emailAdmin = "admin@ufape.edu.br";
		Administrador admin = new Administrador("Admin", emailAdmin, "pwd", "TOTAL", "Coord");
		when(usuarioContrato.buscarPorEmail(emailAdmin)).thenReturn(Optional.of(admin));

		assertThrows(AcessoNegadoAtividadeException.class, () -> service.obterProgresso(emailAdmin));
	}

	@Test
	@DisplayName("Deve lancar AcessoNegadoAtividadeException se email nao for encontrado")
	void deveLancarExcecaoParaEmailInexistente() {
		String emailInexistente = "naoexiste@ufape.edu.br";
		when(usuarioContrato.buscarPorEmail(emailInexistente)).thenReturn(Optional.empty());

		assertThrows(AcessoNegadoAtividadeException.class, () -> service.obterProgresso(emailInexistente));
	}

	@Test
	@DisplayName("Deve calcular progresso com metas customizadas do Curso quando o estudante possuir vinculo")
	void deveCalcularProgressoComMetasDoCurso() {
		Estudante estudanteComCurso = new Estudante("Lucas", emailEstudante, "senha123", "202601", "BCC");
		when(usuarioContrato.buscarPorEmail(emailEstudante)).thenReturn(Optional.of(estudanteComCurso));

		CursoDTO cursoDto = new CursoDTO(1L, "Bacharelado em Ciencia da Computacao", "BCC", 120, 360, true);
		when(cursoContrato.buscarPorNomeOuCodigo("BCC")).thenReturn(Optional.of(cursoDto));

		when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudanteComCurso, Natureza.ACC))
				.thenReturn(Collections.emptyList());
		when(atividadeComplementarRepository.findByEstudanteAndNatureza(estudanteComCurso, Natureza.ACEX))
				.thenReturn(Collections.emptyList());

		ProgressoResponseDTO progresso = service.obterProgresso(emailEstudante);

		assertNotNull(progresso);
		assertEquals(120, progresso.getAcc().getHorasExigidas());
		assertEquals(360, progresso.getAcex().getHorasExigidas());
	}
}
