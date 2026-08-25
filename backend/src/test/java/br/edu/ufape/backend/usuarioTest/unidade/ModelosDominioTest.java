package br.edu.ufape.backend.usuarioTest.unidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.StatusAtividade;
import br.edu.ufape.backend.atividade.service.RegraAtividadeValida;
import br.edu.ufape.backend.autenticacao.dto.LoginResponse;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.ia.model.RegulamentoChunk;
import br.edu.ufape.backend.usuario.model.Administrador;
import br.edu.ufape.backend.usuario.model.Avaliador;
import br.edu.ufape.backend.usuario.model.Estudante;

class ModelosDominioTest {

    @Test
    @DisplayName("Deve testar getters, setters e construtores de Estudante")
    void deveTestarEstudante() {
        Estudante e = new Estudante();
        e.setId(1L);
        e.setNome("Estudante");
        e.setEmail("estudante@ufape.edu.br");
        e.setSenhaHash("hash");
        e.setMatricula("2026001");
        e.setCurso("BCC");
        e.setCargaHorariaObrigatoria(90);
        e.setCargaHorariaCumprida(40);
        e.setSituacao("REGULAR");
        e.setIsActive(true);

        assertEquals(1L, e.getId());
        assertEquals("Estudante", e.getNome());
        assertEquals("estudante@ufape.edu.br", e.getEmail());
        assertEquals("hash", e.getSenhaHash());
        assertEquals("2026001", e.getMatricula());
        assertEquals("BCC", e.getCurso());
        assertEquals(90, e.getCargaHorariaObrigatoria());
        assertEquals(40, e.getCargaHorariaCumprida());
        assertEquals("REGULAR", e.getSituacao());
        assertTrue(e.getIsActive());
    }

    @Test
    @DisplayName("Deve testar getters e setters de Avaliador e Administrador")
    void deveTestarAvaliadorEAdministrador() {
        Avaliador av = new Avaliador();
        av.setRegistro("REG-01");
        av.setAreaAtuacao("Computação");
        av.setSolicitacoesPendentes(3);
        assertEquals("REG-01", av.getRegistro());
        assertEquals("Computação", av.getAreaAtuacao());
        assertEquals(3, av.getSolicitacoesPendentes());

        Administrador adm = new Administrador();
        adm.setNivelAcesso("TOTAL");
        adm.setSetor("Coordenação");
        assertEquals("TOTAL", adm.getNivelAcesso());
        assertEquals("Coordenação", adm.getSetor());
    }

    @Test
    @DisplayName("Deve testar getters e setters de Certificado, LoginResponse e RegulamentoChunk")
    void deveTestarCertificadoELoginResponse() {
        Certificado cert = new Certificado();
        cert.setNomeArquivo("cert.pdf");
        cert.setTipoConteudo("application/pdf");
        cert.setTamanhoEmBytes(1024L);
        cert.setReferencia("/path/cert.pdf");

        assertEquals("cert.pdf", cert.getNomeArquivo());
        assertEquals("application/pdf", cert.getTipoConteudo());
        assertEquals(1024L, cert.getTamanhoEmBytes());
        assertEquals("/path/cert.pdf", cert.getReferencia());

        LoginResponse login = new LoginResponse("tok", "Bearer");
        login.setToken("novo-token");
        login.setTipo("Bearer");
        assertEquals("novo-token", login.getToken());
        assertEquals("Bearer", login.getTipo());

        RegulamentoChunk chunk = new RegulamentoChunk();
        assertNotNull(chunk);
    }

    @Test
    @DisplayName("Deve testar todas as ramificações de RegraAtividadeValida")
    void deveTestarRegraAtividadeValida() {
        AtividadeComplementar aprovada = new AtividadeComplementar();
        aprovada.setStatus(StatusAtividade.APROVADA);

        AtividadeComplementar pendente = new AtividadeComplementar();
        pendente.setStatus(StatusAtividade.PENDENTE);

        AtividadeComplementar rejeitada = new AtividadeComplementar();
        rejeitada.setStatus(StatusAtividade.REJEITADA);

        assertTrue(RegraAtividadeValida.isAprovada(aprovada));
        assertFalse(RegraAtividadeValida.isAprovada(pendente));
        assertFalse(RegraAtividadeValida.isAprovada(null));

        assertTrue(RegraAtividadeValida.isPendente(pendente));
        assertFalse(RegraAtividadeValida.isPendente(aprovada));
        assertFalse(RegraAtividadeValida.isPendente(null));

        assertTrue(RegraAtividadeValida.isValida(aprovada));
        assertFalse(RegraAtividadeValida.isValida(null));
    }
}