package br.edu.ufape.backend.atividade.facade;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
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

    public Page<AtividadeResponse> listarAtividadesDoEstudante(
            String emailEstudante, Natureza natureza, Categoria categoria, Pageable pageable) {
        List<AtividadeResponse> atividades = atividadeComplementarService
                .listarAtividadesDoEstudante(emailEstudante, natureza, categoria)
                .stream()
                .map(AtividadeResponse::new)
                .toList();

        if (pageable.isUnpaged()) {
            return new PageImpl<>(atividades);
        }

        int start = (int) pageable.getOffset();
        if (start >= atividades.size()) {
            return new PageImpl<>(List.of(), pageable, atividades.size());
        }

        int end = Math.min(start + pageable.getPageSize(), atividades.size());
        return new PageImpl<>(atividades.subList(start, end), pageable, atividades.size());
    }

    public AtividadeResponse cadastrarAtividade(
            CadastroAtividadeRequest request,
            MultipartFile arquivo,
            String emailEstudante) {
        return atividadeComplementarService.cadastrarAtividade(request, arquivo, emailEstudante);
    }
}
