package br.edu.ufape.backend.solicitacao.facade;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.solicitacao.dto.SolicitacaoResponseDTO;
import br.edu.ufape.backend.solicitacao.model.SolicitacaoValidacao;
import br.edu.ufape.backend.solicitacao.service.SolicitacaoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;

@Component
public class SolicitacaoFacade {

    private final SolicitacaoService solicitacaoService;
    private final UsuarioContrato usuarioContrato;

    public SolicitacaoFacade(SolicitacaoService solicitacaoService, UsuarioContrato usuarioContrato) {
        this.solicitacaoService = solicitacaoService;
        this.usuarioContrato = usuarioContrato;
    }

    public SolicitacaoResponseDTO submeter(String emailEstudante) {
        Usuario usuario = usuarioContrato.buscarPorEmail(emailEstudante)
                .orElseThrow(() -> new AcessoNegadoAtividadeException("Estudante não encontrado"));

        if (!(usuario instanceof Estudante)) {
            throw new AcessoNegadoAtividadeException("Apenas estudantes podem submeter solicitações.");
        }

        SolicitacaoValidacao solicitacao = solicitacaoService.submeter(usuario.getId());
        return new SolicitacaoResponseDTO(solicitacao);
    }

    public SolicitacaoResponseDTO submeter(Long estudanteId) {
        SolicitacaoValidacao solicitacao = solicitacaoService.submeter(estudanteId);
        return new SolicitacaoResponseDTO(solicitacao);
    }
}

