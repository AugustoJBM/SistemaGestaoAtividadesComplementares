package br.edu.ufape.backend.curso.unidade.service;

import br.edu.ufape.backend.curso.dto.CursoDTO;
import br.edu.ufape.backend.curso.model.Curso;
import br.edu.ufape.backend.curso.repository.CursoRepository;
import br.edu.ufape.backend.curso.service.CursoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private CursoService cursoService;

	@Test
	@DisplayName("Deve criar curso com sucesso")
	void deveCriarCursoComSucesso() {
		CursoDTO dto = new CursoDTO(null, "Agronomia", "AGRO", 100, 300, true);
		when(cursoRepository.existsByCodigoIgnoreCase("AGRO")).thenReturn(false);
		when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> {
			Curso c = inv.getArgument(0);
			c.setId(1L);
			return c;
		});

		CursoDTO criado = cursoService.criarCurso(dto);

		assertNotNull(criado);
		assertEquals(1L, criado.id());
		assertEquals("AGRO", criado.codigo());
	}

	@Test
	@DisplayName("Deve listar todos os cursos cadastrados")
	void deveListarCursos() {
		Curso c1 = new Curso("Computação", "BCC", 90, 320);
		when(cursoRepository.findAll()).thenReturn(List.of(c1));

		List<CursoDTO> lista = cursoService.listarCursos();

		assertEquals(1, lista.size());
		assertEquals("BCC", lista.get(0).codigo());
	}
}
