package br.edu.ufape.backend.atividade.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(name = "titulo") String titulo,
            @RequestParam(name = "instituicaoResponsavel") String instituicaoResponsavel,
            @RequestParam(name = "dataRealizacao") String dataRealizacao,
            @RequestParam(name = "cargaHoraria") String cargaHoraria,
            @RequestParam(name = "natureza") String natureza,
            @RequestParam(name = "categoria") String categoria,
            @RequestPart("arquivo") MultipartFile arquivo,
            Authentication authentication) {

        LocalDate data = LocalDate.parse(dataRealizacao);
        Integer carga = Integer.valueOf(cargaHoraria);
        Natureza nat = Natureza.valueOf(natureza);
        Categoria cat = Categoria.valueOf(categoria);
        CadastroAtividadeRequest request = new CadastroAtividadeRequest(
                titulo, instituicaoResponsavel, data, carga, nat, cat);
        String emailEstudante = authentication.getName();
        AtividadeResponse response = atividadeFacade.cadastrarAtividade(request, arquivo, emailEstudante);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}