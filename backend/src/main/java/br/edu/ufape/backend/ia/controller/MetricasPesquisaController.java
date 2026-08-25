package br.edu.ufape.backend.ia.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.edu.ufape.backend.ia.facade.IaCertificadoFacade;

@RestController
@RequestMapping("/api/v1/metricas-pesquisa")
public class MetricasPesquisaController {

    private final IaCertificadoFacade iaCertificadoFacade;

    public MetricasPesquisaController(IaCertificadoFacade iaCertificadoFacade) {
        this.iaCertificadoFacade = iaCertificadoFacade;
    }

    @GetMapping("/concordancia-kappa")
    public ResponseEntity<Map<String, Object>> obterMetricasEmpiricas() {
        long totalAvaliadas = iaCertificadoFacade.contarAvaliadas();
        long concordancias = iaCertificadoFacade.contarConcordancias();
        double tempoMedioMs = iaCertificadoFacade.calcularTempoMedioMs();
        
        double concordanciaObservada = totalAvaliadas > 0 ? (double) concordancias / totalAvaliadas : 0.0;
        return ResponseEntity.ok(Map.of(
                "totalAmostrasAvaliadas", totalAvaliadas,
                "totalConcordancias", concordancias,
                "acuraciaObservada", concordanciaObservada,
                "tempoMedioInferenciaMs", tempoMedioMs));
    }
}