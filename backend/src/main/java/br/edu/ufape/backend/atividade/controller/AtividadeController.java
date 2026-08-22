package br.edu.ufape.backend.atividade.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindException;

import jakarta.validation.Valid;

import br.edu.ufape.backend.atividade.dto.AtividadeResponseDTO;
import br.edu.ufape.backend.atividade.dto.AtualizarAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequestDTO;
import br.edu.ufape.backend.atividade.dto.ProgressoResponseDTO;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;
import br.edu.ufape.backend.certificados.exception.CertificadoInvalidoException;

@RestController
@RequestMapping("/api/v1/atividades")
public class AtividadeController {

    private final AtividadeFacade atividadeFacade;

    public AtividadeController(AtividadeFacade atividadeFacade) {
        this.atividadeFacade = atividadeFacade;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeResponseDTO> atualizarAtividade(
            @PathVariable Long id,
            @Valid @ModelAttribute AtualizarAtividadeRequestDTO request,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String emailEstudante = authentication.getName();
        AtividadeResponseDTO response = atividadeFacade.atualizarAtividade(id, request, arquivo, emailEstudante);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/certificado")
    public ResponseEntity<Resource> obterCertificado(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String emailEstudante = authentication.getName();
        Resource resource = atividadeFacade.obterCertificado(id, emailEstudante);

        String contentType = "application/pdf";
        try {
            Path path = resource.getFile().toPath();
            String probedType = Files.probeContentType(path);
            if (probedType != null) {
                contentType = probedType;
            }
        } catch (IOException ignored) {}

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/progresso")
    public ResponseEntity<ProgressoResponseDTO> progresso(Authentication authentication) {
        String emailEstudante = authentication.getName();
        ProgressoResponseDTO progressoResponse = atividadeFacade.obterProgresso(emailEstudante);
        return ResponseEntity.ok(progressoResponse);
    }

    @GetMapping
    public ResponseEntity<List<AtividadeResponseDTO>> listar(
            @RequestParam(required = false) Natureza natureza,
            @RequestParam(required = false) Categoria categoria,
            Authentication authentication) {
        String emailEstudante = authentication.getName();
        List<AtividadeResponseDTO> atividades = atividadeFacade.listarAtividadesDoEstudante(
                emailEstudante, natureza, categoria);
        return ResponseEntity.ok(atividades);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeResponseDTO> cadastrar(
            @Valid @ModelAttribute CadastroAtividadeRequestDTO request,
            @RequestPart("arquivo") MultipartFile arquivo,
            Authentication authentication) {

        String emailEstudante = authentication.getName();
        AtividadeResponseDTO response = atividadeFacade.cadastrarAtividade(request, arquivo, emailEstudante);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> tratarFalhaDeValidacao(BindException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .orElse("Dados de cadastro inválidos");
        return ResponseEntity.badRequest().body(Map.of("message", mensagem));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> tratarArquivoAusente(MissingServletRequestPartException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "Arquivo de certificado não pode ser vazio"));
    }

    @ExceptionHandler(CertificadoInvalidoException.class)
    public ResponseEntity<Map<String, String>> tratarCertificadoInvalido(CertificadoInvalidoException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {
        String emailEstudante = authentication.getName();
        atividadeFacade.excluirAtividade(id, emailEstudante);
        return ResponseEntity.noContent().build();
    }
}
