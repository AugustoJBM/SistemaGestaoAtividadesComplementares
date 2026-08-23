package br.edu.ufape.backend.ia.contrato;

import org.springframework.web.multipart.MultipartFile;
import br.edu.ufape.backend.ia.dto.ExtracaoCertificadoResponseDTO;
import br.edu.ufape.backend.ia.dto.ParecerResponseDTO;

public interface IaContrato {

    /**
     * Extrai metadados do documento (PDF/Imagem) usando OCR ou leitura direta.
     */
    ExtracaoCertificadoResponseDTO extrairDadosArquivo(MultipartFile arquivo);

    /**
     * Executa RAG semântico contra os regulamentos da UFAPE e emite parecer técnico.
     */
    ParecerResponseDTO gerarParecerConformidade(String titulo, String instituicao, String natureza,
            String categoria, int cargaHoraria);
}