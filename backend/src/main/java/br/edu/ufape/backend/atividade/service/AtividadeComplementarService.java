package br.edu.ufape.backend.atividade.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.AtualizarAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.exception.AcessoNegadoAtividadeException;
import br.edu.ufape.backend.atividade.exception.AtividadeNaoEncontradaException;
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
    private static final String MENSAGEM_ACESSO_NEGADO_EDICAO = "Você não tem permissão para editar esta atividade.";
    private static final String MENSAGEM_ACESSO_NEGADO_EXCLUSAO = "Atividade não encontrada ou não pertence ao estudante autenticado.";

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

    @Transactional
    public AtividadeResponseDTO cadastrarAtividade(CadastroAtividadeRequestDTO request, MultipartFile arquivo,
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

        try {
            AtividadeComplementar atividadeSalva = atividadeRepository.save(atividade);
            return new AtividadeResponseDTO(atividadeSalva);
        } catch (RuntimeException e) {
            // compensa a gravação em disco já feita, evitando certificado órfão
            try {
                Files.deleteIfExists(Paths.get(certificado.getReferencia()));
            } catch (IOException ioException) {
                e.addSuppressed(ioException);
            }
            throw e;
        }
    }

    @Transactional
    public AtividadeResponseDTO atualizarAtividade(Long id, AtualizarAtividadeRequestDTO request,
            MultipartFile novoArquivo, String emailEstudante) {
        Estudante estudante = obterEstudante(emailEstudante);

        // 1. 404 se não existir
        AtividadeComplementar atividade = atividadeRepository.findById(id)
                .orElseThrow(() -> new AtividadeNaoEncontradaException("Atividade não encontrada."));

        // 2. 403 se não pertencer ao estudante autenticado
        if (!atividade.getEstudante().getId().equals(estudante.getId())) {
            throw new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO_EDICAO);
        }

        // 3. Atualiza os dados cadastrais
        atividade.setTitulo(request.titulo());
        atividade.setInstituicaoResponsavel(request.instituicaoResponsavel());
        atividade.setDataRealizacao(request.dataRealizacao());
        atividade.setCargaHorariaEmHoras(request.cargaHoraria());
        atividade.setNatureza(request.natureza());
        atividade.setCategoria(request.categoria());

        // 4. Tratamento de substituição opcional do certificado
        Certificado certificadoAntigo = atividade.getCertificado();
        Certificado novoCertificado = null;

        if (novoArquivo != null && !novoArquivo.isEmpty()) {
            validarTipoArquivo(novoArquivo);
            novoCertificado = armazenamentoCertificadoService.armazenar(novoArquivo);
            atividade.setCertificado(novoCertificado);
        }

        try {
            AtividadeComplementar atividadeSalva = atividadeRepository.save(atividade);

            // Remove o arquivo físico antigo do disco após persistência com sucesso
            if (novoCertificado != null && certificadoAntigo != null) {
                removerArquivoCertificado(certificadoAntigo);
            }

            return new AtividadeResponseDTO(atividadeSalva);
        } catch (RuntimeException e) {
            // Se houver falha ao salvar, remove o novo arquivo gerado para evitar arquivo
            // órfão
            if (novoCertificado != null) {
                removerArquivoCertificado(novoCertificado);
            }
            throw e;
        }
    }

    @Transactional
    public void excluirAtividade(Long id, String emailEstudante) {
        Estudante estudante = obterEstudante(emailEstudante);

        AtividadeComplementar atividade = atividadeRepository.findByIdAndEstudante(id, estudante)
                .orElseThrow(() -> new AcessoNegadoAtividadeException(MENSAGEM_ACESSO_NEGADO_EXCLUSAO));

        removerArquivoCertificado(atividade.getCertificado());

        atividadeRepository.delete(atividade);
    }

    private void removerArquivoCertificado(Certificado certificado) {
        if (certificado == null || certificado.getReferencia() == null) {
            return;
        }

        try {
            Files.deleteIfExists(Path.of(certificado.getReferencia()));
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao remover arquivo do certificado", ex);
        }
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