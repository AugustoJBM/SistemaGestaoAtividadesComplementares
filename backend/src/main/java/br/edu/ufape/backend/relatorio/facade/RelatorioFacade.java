package br.edu.ufape.backend.relatorio.facade;

import org.springframework.stereotype.Component;

import br.edu.ufape.backend.relatorio.dto.RelatorioAtividadesResponse;
import br.edu.ufape.backend.relatorio.service.RelatorioService;

@Component
public class RelatorioFacade {

    private final RelatorioService relatorioService;

    public RelatorioFacade(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    public RelatorioAtividadesResponse gerarRelatorio(String emailEstudante) {
        return relatorioService.gerarRelatorio(emailEstudante);
    }
}
