package br.edu.ufape.backend.atividade.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.dto.ProgressoResponse;
import br.edu.ufape.backend.atividade.facade.AtividadeFacade;
import br.edu.ufape.backend.atividade.model.Categoria;
import br.edu.ufape.backend.atividade.model.Natureza;

@RestController
@RequestMapping("/api/v1/atividades")
public class AtividadeController {

    private final AtividadeFacade atividadeFacade;

    public AtividadeController(AtividadeFacade atividadeFacade) {
        this.atividadeFacade = atividadeFacade;
    }

    @GetMapping("/progresso")
    public ResponseEntity<ProgressoResponse> progresso(Authentication authentication) {
        String emailEstudante = authentication.getName();
        ProgressoResponse progressoResponse = atividadeFacade.obterProgresso(emailEstudante);
        return ResponseEntity.ok(progressoResponse);
    }

    @GetMapping
    public ResponseEntity<List<AtividadeResponse>> listar(
            @RequestParam(required = false) Natureza natureza,
            @RequestParam(required = false) Categoria categoria,
            Authentication authentication) {
        String emailEstudante = authentication.getName();
        List<AtividadeResponse> atividades = atividadeFacade.listarAtividadesDoEstudante(
                emailEstudante, natureza, categoria);
        return ResponseEntity.ok(atividades);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeResponse> cadastrar(
            @Valid @ModelAttribute CadastroAtividadeRequest request,
            @RequestPart("arquivo") MultipartFile arquivo,
            Authentication authentication) {

        String emailEstudante = authentication.getName();
        AtividadeResponse response = atividadeFacade.cadastrarAtividade(request, arquivo, emailEstudante);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> tratarFalhaDeValidacao(BindException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .orElse("Dados de cadastro inválidos");
        return ResponseEntity.badRequest().body(mensagem);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<String> tratarArquivoAusente(MissingServletRequestPartException ex) {
        return ResponseEntity.badRequest().body("Arquivo de certificado não pode ser vazio");
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
