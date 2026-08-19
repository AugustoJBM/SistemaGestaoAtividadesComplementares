package br.edu.ufape.backend.atividade.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.contrato.UsuarioContrato;
import br.edu.ufape.backend.usuario.model.Estudante;
import br.edu.ufape.backend.usuario.model.Usuario;

@Service
public class AtividadeComplementarService {

    private static final String MENSAGEM_ACESSO_NEGADO = "Apenas estudantes podem listar atividades complementares.";

    private final AtividadeComplementarRepository atividadeRepository;
    private final UsuarioContrato usuarioContrato;
    private final ArmazenamentoCertificadoService armazenamentoCertificadoService;

    public AtividadeComplementarService(
            AtividadeComplementarRepository atividadeRepository,
            UsuarioContrato usuarioContrato,
            ArmazenamentoCertificadoService armazenamentoCertificadoService) {
        this.atividadeRepository = atividadeRepository;
        this.usuarioContrato = usuarioContrato;
        this.armazenamentoCertificadoService = armazenamentoCertificadoService;
    }

    public List<AtividadeComplementar> listarAtividadesDoEstudante(
            String emailEstudante, Natureza natureza, Categoria categoria) {
        Estudante estudante = obterEstudante(emailEstudante);
        return atividadeRepository.findByEstudanteComFiltros(estudante, natureza, categoria);
    }

    public AtividadeResponse cadastrarAtividade(CadastroAtividadeRequest request, MultipartFile arquivo,
            String emailEstudante) {
        validarTipoArquivo(arquivo);

        Usuario estudante = usuarioContrato.buscarPorEmail(emailEstudante)
            .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));

        Certificado certificado = armazenamentoCertificadoService.armazenar(arquivo);

        AtividadeComplementar atividade = new AtividadeComplementar(
                request.titulo(),
                request.instituicaoResponsavel(),
                request.dataRealizacao(),
                request.cargaHoraria(),
                request.natureza(),
                request.categoria(),
                certificado,
                estudante);

        AtividadeComplementar atividadeSalva = atividadeRepository.save(atividade);
        return new AtividadeResponse(atividadeSalva);
    }

    private Estudante obterEstudante(String email) {
        Usuario usuario = usuarioContrato.buscarPorEmail(email)
                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO));

        if (!(usuario instanceof Estudante estudante)) {
            throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO);
        }

        return estudante;
    }

    private void validarTipoArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new CertificadoInvalidoException("Arquivo de certificado não pode ser vazio");
        }

        String tipo = arquivo.getContentType();
        if (tipo == null || !(tipo.equals("application/pdf") || tipo.equals("image/png") || tipo.equals("image/jpeg")
                || tipo.equals("image/jpg"))) {
            throw new CertificadoInvalidoException("Certificado inválido. Aceitos: PDF, PNG ou JPEG");
        }
    }
}
