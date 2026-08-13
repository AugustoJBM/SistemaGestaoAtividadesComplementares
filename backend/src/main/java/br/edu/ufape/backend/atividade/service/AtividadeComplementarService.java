package br.edu.ufape.backend.atividade.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.atividade.model.AtividadeComplementar;
import br.edu.ufape.backend.atividade.repository.AtividadeComplementarRepository;
import br.edu.ufape.backend.certificados.model.Certificado;
import br.edu.ufape.backend.certificados.service.ArmazenamentoCertificadoService;
import br.edu.ufape.backend.usuario.model.Usuario;
import br.edu.ufape.backend.usuario.repository.UsuarioRepository;

@Service
public class AtividadeComplementarService {

    private final AtividadeComplementarRepository atividadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArmazenamentoCertificadoService armazenamentoCertificadoService;

    public AtividadeComplementarService(
            AtividadeComplementarRepository atividadeRepository,
            UsuarioRepository usuarioRepository,
            ArmazenamentoCertificadoService armazenamentoCertificadoService) {
        this.atividadeRepository = atividadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.armazenamentoCertificadoService = armazenamentoCertificadoService;
    }

    public AtividadeResponse cadastrarAtividade(CadastroAtividadeRequest request, MultipartFile arquivo,
            String emailEstudante) {
        validarTipoArquivo(arquivo);

        Usuario estudante = usuarioRepository.findByEmail(emailEstudante)
                .orElseThrow(() -> new CertificadoInvalidoException("Estudante não encontrado para o email autenticado"));

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
