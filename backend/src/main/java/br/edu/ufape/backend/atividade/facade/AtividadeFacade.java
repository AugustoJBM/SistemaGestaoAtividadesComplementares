package br.edu.ufape.backend.atividade.facade;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.AtualizarAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
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

    public AtividadeResponseDTO atualizarAtividade(
            Long id,
            AtualizarAtividadeRequestDTO request,
            MultipartFile arquivo,
            String emailEstudante) {
        return atividadeComplementarService.atualizarAtividade(id, request, arquivo, emailEstudante);
    }

    public Resource obterCertificado(Long id, String emailEstudante) {
        return atividadeComplementarService.obterArquivoCertificado(id, emailEstudante);
    }

    public ProgressoResponseDTO obterProgresso(String emailEstudante) {
        return progressoService.obterProgresso(emailEstudante);
    }

    public List<AtividadeResponseDTO> listarAtividadesDoEstudante(
            String emailEstudante, Natureza natureza, Categoria categoria) {
        return atividadeComplementarService
                .listarAtividadesDoEstudante(emailEstudante, natureza, categoria)
                .stream()
                .map(AtividadeResponseDTO::new)
                .toList();
    }

    public AtividadeResponseDTO cadastrarAtividade(
            CadastroAtividadeRequestDTO request,
            MultipartFile arquivo,
            String emailEstudante) {
        return atividadeComplementarService.cadastrarAtividade(request, arquivo, emailEstudante);
    }

    public void excluirAtividade(Long id, String emailEstudante) {
        atividadeComplementarService.excluirAtividade(id, emailEstudante);
    }
}
