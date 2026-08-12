package br.edu.ufape.backend.atividade.facade;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import br.edu.ufape.backend.atividade.service.ProgressoService;

@Component
public class AtividadeFacade {

    private final ProgressoService progressoService;
    private final AtividadeComplementarService atividadeComplementarService;

    public AtividadeFacade(
            ProgressoService progressoService,
            AtividadeComplementarService atividadeComplementarService) {
        this.progressoService = progressoService;
        this.atividadeComplementarService = atividadeComplementarService;
    }

    public ProgressoResponse obterProgresso(String emailEstudante) {
        return progressoService.obterProgresso(emailEstudante);
    }

    public AtividadeResponse cadastrarAtividade(
            CadastroAtividadeRequest request,
            MultipartFile arquivo,
            String emailEstudante) {
        return atividadeComplementarService.cadastrarAtividade(request, arquivo, emailEstudante);
    }
}
