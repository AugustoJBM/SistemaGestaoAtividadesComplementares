package br.edu.ufape.backend.atividade.unidade.dto;

import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoModalidadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.model.*;
import br.edu.ufape.backend.atividade.service.RegraAtividadeValida;
import br.edu.ufape.backend.certificado.model.Certificado;
import br.edu.ufape.backend.comum.exception.ErroResponse;
import br.edu.ufape.backend.ia.dto.RegulamentoChunkResponseDTO;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoDetalheResponseDTO;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResponseDTO;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResumoResponseDTO;
import br.edu.ufape.backend.solicitacao.model.DecisaoAvaliacao;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoAtividade;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.model.StatusSolicitacao;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelAndDtoTest {

	@Test
	@DisplayName("Branch: RegraAtividadeValida cobre nulos e todos os estados")
	void deveCobrirRegraAtividadeValida() {
		assertFalse(RegraAtividadeValida.isAprovada(null));
		assertFalse(RegraAtividadeValida.isPendente(null));
		assertFalse(RegraAtividadeValida.isValida(null));

		AtividadeComplementar a = new AtividadeComplementar();
		assertTrue(RegraAtividadeValida.isValida(a));
		assertTrue(RegraAtividadeValida.isPendente(a)); // status nulo é pendente
		assertFalse(RegraAtividadeValida.isAprovada(a));

		a.setStatus(StatusAtividade.APROVADA);
		assertTrue(RegraAtividadeValida.isAprovada(a));
		assertFalse(RegraAtividadeValida.isPendente(a));

		a.setStatus(StatusAtividade.REJEITADA);
		assertFalse(RegraAtividadeValida.isAprovada(a));
		assertFalse(RegraAtividadeValida.isPendente(a));
	}

	@Test
	@DisplayName("Branch: ProgressoModalidadeResponseDTO cálculos de teto e divisão zero")
	void deveCobrirProgressoModalidadeDTO() {
		ProgressoModalidadeResponseDTO dto1 = new ProgressoModalidadeResponseDTO(50, 10, 0);
		assertEquals(0, dto1.getPercentualConcluido());

		ProgressoModalidadeResponseDTO dto2 = new ProgressoModalidadeResponseDTO(150, 0, 100);
		assertEquals(100, dto2.getPercentualConcluido());

		ProgressoResponseDTO p = new ProgressoResponseDTO(dto1, dto2);
		assertSame(dto1, p.getAcc());
		assertSame(dto2, p.getAcex());
	}

	@Test
	@DisplayName("Branch: AtividadeResponseDTO com estudante nulo, status nulo e construtor direto")
	void deveCobrirAtividadeResponseDTO() {
		AtividadeComplementar a1 = new AtividadeComplementar();
		a1.setId(1L);
		a1.setTitulo("T");
		a1.setStatus(null);
		a1.setEstudante(null);

		AtividadeResponseDTO dto1 = new AtividadeResponseDTO(a1);
		assertNull(dto1.estudanteEmail());
		assertEquals(StatusAtividade.PENDENTE, dto1.status());

		AtividadeResponseDTO dto2 = new AtividadeResponseDTO(2L, "T", "I", LocalDate.now(), 20, Natureza.ACC,
				Categoria.ENSINO, LocalDateTime.now(), "email@a.com");
		assertEquals(StatusAtividade.PENDENTE, dto2.status());
	}

	@Test
	@DisplayName("Branch: SolicitacaoValidacao e DTOs com itens nulos e preenchidos")
	void deveCobrirSolicitacaoDTOs() {
		SolicitacaoValidacao s1 = new SolicitacaoValidacao(1L, LocalDateTime.now(), null, null);
		assertEquals(StatusSolicitacao.SUBMETIDA, s1.getStatus());
		assertTrue(s1.getItens().isEmpty());

		s1.setItens(null);
		assertTrue(s1.getItens().isEmpty());

		SolicitacaoResponseDTO r1 = new SolicitacaoResponseDTO(s1);
		assertTrue(r1.itens().isEmpty());

		SolicitacaoDetalheResponseDTO d1 = new SolicitacaoDetalheResponseDTO(s1);
		assertEquals(0, d1.totalAtividades());

		SolicitacaoResumoResponseDTO resumo = new SolicitacaoResumoResponseDTO(s1);
		assertEquals(0L, resumo.totalAtividades());

		SolicitacaoAtividade item = new SolicitacaoAtividade(10L, "T", 20, "ACC");
		item.setId(1L);
		s1.setItens(List.of(item));

		SolicitacaoDetalheResponseDTO d2 = new SolicitacaoDetalheResponseDTO(s1);
		assertEquals(1, d2.totalAtividades());
		assertEquals("T", d2.itens().get(0).titulo());
	}

	@Test
	@DisplayName("Branch: StatusSolicitacao, DecisaoAvaliacao e ErroResponse")
	void deveCobrirEnumsEOutrosDTOs() {
		assertTrue(StatusSolicitacao.SUBMETIDA.isEmAberto());
		assertTrue(StatusSolicitacao.EM_ANALISE.isEmAberto());
		assertTrue(StatusSolicitacao.COM_PENDENCIAS.isEmAberto());
		assertFalse(StatusSolicitacao.APROVADA.isEmAberto());
		assertFalse(StatusSolicitacao.REJEITADA.isEmAberto());

		assertEquals(StatusSolicitacao.APROVADA, DecisaoAvaliacao.APROVADA.toStatus());
		assertEquals(StatusSolicitacao.REJEITADA, DecisaoAvaliacao.REJEITADA.toStatus());
		assertEquals(StatusSolicitacao.COM_PENDENCIAS, DecisaoAvaliacao.COM_PENDENCIAS.toStatus());

		ErroResponse erro = new ErroResponse("msg", 400);
		assertEquals("msg", erro.mensagem());

		RegulamentoChunk chunk = new RegulamentoChunk("Art. 1", "Texto", "[0.1]");
		chunk.setEmbeddingVetor("[0.2]");
		assertEquals("[0.2]", chunk.getEmbeddingVetor());
		RegulamentoChunkResponseDTO chunkDto = RegulamentoChunkResponseDTO.fromEntity(chunk);
		assertEquals("Art. 1", chunkDto.artigo());
	}

	@Test
	@DisplayName("Branch: Getters e Setters de Entidades de Usuário e Certificado")
	void deveCobrirEntidadesDeUsuario() {
		Administrador admin = new Administrador("Admin", "admin@u.br", "h", "N1", "TI");
		admin.setNivelAcesso("N2");
		admin.setSetor("ADM");
		admin.setIsActive(true);
		assertEquals("N2", admin.getNivelAcesso());
		assertEquals("ADM", admin.getSetor());
		assertTrue(admin.getIsActive());

		Avaliador avaliador = new Avaliador("Av", "av@u.br", "h", "R1", "CC");
		avaliador.setRegistro("R2");
		avaliador.setAreaAtuacao("SI");
		avaliador.setSolicitacoesPendentes(5);
		assertEquals("R2", avaliador.getRegistro());
		assertEquals("SI", avaliador.getAreaAtuacao());
		assertEquals(5, avaliador.getSolicitacoesPendentes());

		Estudante est = new Estudante("Est", "est@u.br", "h", "M1", "BCC");
		est.setMatricula("M2");
		est.setCurso("CC");
		est.setCargaHorariaObrigatoria(300);
		est.setCargaHorariaCumprida(100);
		est.setSituacao("REGULAR");
		assertEquals("M2", est.getMatricula());
		assertEquals("CC", est.getCurso());
		assertEquals(300, est.getCargaHorariaObrigatoria());
		assertEquals(100, est.getCargaHorariaCumprida());
		assertEquals("REGULAR", est.getSituacao());

		Certificado cert = new Certificado("c.pdf", "app/pdf", 100L, "/path");
		cert.setNomeArquivo("c2.pdf");
		cert.setTipoConteudo("image/png");
		cert.setTamanhoEmBytes(200L);
		cert.setReferencia("/path2");
		assertEquals("c2.pdf", cert.getNomeArquivo());
		assertEquals("image/png", cert.getTipoConteudo());
		assertEquals(200L, cert.getTamanhoEmBytes());
		assertEquals("/path2", cert.getReferencia());
	}
}
