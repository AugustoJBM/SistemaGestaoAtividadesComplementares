package br.edu.ufape.backend.atividade.controller;

import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest;
import br.edu.ufape.backend.atividade.service.AtividadeComplementarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/atividades")
public class AtividadeComplementarController {

    private final AtividadeComplementarService atividadeService;

    public AtividadeComplementarController(AtividadeComplementarService atividadeService) {
        this.atividadeService = atividadeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AtividadeResponse> cadastrar(
            @org.springframework.web.bind.annotation.RequestParam(name = "titulo") String titulo,
            @org.springframework.web.bind.annotation.RequestParam(name = "instituicaoResponsavel") String instituicaoResponsavel,
            @org.springframework.web.bind.annotation.RequestParam(name = "dataRealizacao") String dataRealizacao,
            @org.springframework.web.bind.annotation.RequestParam(name = "cargaHoraria") String cargaHoraria,
            @org.springframework.web.bind.annotation.RequestParam(name = "natureza") String natureza,
            @org.springframework.web.bind.annotation.RequestParam(name = "categoria") String categoria,
            @RequestPart("arquivo") MultipartFile arquivo,
            Authentication authentication) {

        try {
            java.time.LocalDate data = java.time.LocalDate.parse(dataRealizacao);
            Integer carga = Integer.valueOf(cargaHoraria);
            br.edu.ufape.backend.atividade.model.Natureza nat = br.edu.ufape.backend.atividade.model.Natureza.valueOf(natureza);
            br.edu.ufape.backend.atividade.model.Categoria cat = br.edu.ufape.backend.atividade.model.Categoria.valueOf(categoria);

            br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest request = new br.edu.ufape.backend.atividade.dto.CadastroAtividadeRequest(
                    titulo, instituicaoResponsavel, data, carga, nat, cat);

            String emailEstudante = authentication.getName();
            AtividadeResponse response = atividadeService.cadastrarAtividade(request, arquivo, emailEstudante);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
