package br.edu.ufape.backend.certificado.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.edu.ufape.backend.certificado.exception.CertificadoInvalidoException;
import br.edu.ufape.backend.certificado.model.Certificado;

@Service
public class ArmazenamentoCertificadoService {

	private final Path diretorioRaiz;

	public ArmazenamentoCertificadoService(@Value("${sgac.certificados.diretorio:certificados}") String diretorio) {
		this.diretorioRaiz = Path.of(diretorio).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.diretorioRaiz);
		} catch (IOException ex) {
			throw new RuntimeException("Não foi possível criar o diretório de certificados", ex);
		}
	}

	public Certificado armazenar(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new CertificadoInvalidoException("Arquivo de certificado não pode ser vazio");
		}

        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new CertificadoInvalidoException("O tamanho do arquivo excede o limite máximo permitido de 5MB");
        }

        String tipo = arquivo.getContentType();
        if (tipo == null || !(tipo.equals("application/pdf") || tipo.equals("image/png") || tipo.equals("image/jpeg") || tipo.equals("image/jpg"))) {
            throw new CertificadoInvalidoException("Certificado inválido. Aceitos: PDF, PNG ou JPEG");
        }

		String nomeOriginal = arquivo.getOriginalFilename();
		if (nomeOriginal == null || nomeOriginal.isBlank()) {
			throw new CertificadoInvalidoException("Nome de arquivo inválido");
		}

		String nomeArquivoSeguro = System.currentTimeMillis() + "-" + nomeOriginal.replaceAll("[^a-zA-Z0-9._-]", "_");
		Path destino = this.diretorioRaiz.resolve(nomeArquivoSeguro);

		try {
			Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new RuntimeException("Falha ao gravar arquivo de certificado", ex);
		}

		return new Certificado(nomeOriginal, arquivo.getContentType(), arquivo.getSize(), destino.toString());
	}
}
