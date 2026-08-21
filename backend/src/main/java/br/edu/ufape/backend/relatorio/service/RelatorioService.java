package br.edu.ufape.backend.relatorio.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.ufape.backend.atividade.contrato.AtividadeContrato;
import br.edu.ufape.backend.atividade.dto.AtividadeResponse;
import br.edu.ufape.backend.relatorio.dto.GrupoCategoriaResponse;
import br.edu.ufape.backend.relatorio.dto.GrupoNaturezaResponse;
import br.edu.ufape.backend.relatorio.dto.ItemAtividadeResponse;
import br.edu.ufape.backend.relatorio.dto.RelatorioAtividadesResponse;

@Service
public class RelatorioService {

    // Ordem fixa mantem o JSON deterministico independente da ordem vinda do contrato.
    private static final String ACC = "ACC";
    private static final String ACEX = "ACEX";
    private static final List<String> ORDEM_NATUREZA = List.of(ACC, ACEX);

    private final AtividadeContrato atividadeContrato;

    public RelatorioService(AtividadeContrato atividadeContrato) {
        this.atividadeContrato = atividadeContrato;
    }

    public RelatorioAtividadesResponse gerarRelatorio(String emailEstudante) {
        List<AtividadeResponse> atividades = atividadeContrato.buscarPorEstudante(emailEstudante);

        Map<String, List<AtividadeResponse>> porNatureza = atividades.stream()
                .collect(Collectors.groupingBy(
                        atividade -> atividade.natureza().name(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<GrupoNaturezaResponse> grupos = new ArrayList<>();
        for (String natureza : ORDEM_NATUREZA) {
            List<AtividadeResponse> doGrupo = porNatureza.getOrDefault(natureza, List.of());
            if (doGrupo.isEmpty()) {
                continue;
            }
            grupos.add(new GrupoNaturezaResponse(natureza, somarHoras(doGrupo), agruparPorCategoria(doGrupo)));
        }

        int totalHorasAcc = somarHoras(porNatureza.getOrDefault(ACC, List.of()));
        int totalHorasAcex = somarHoras(porNatureza.getOrDefault(ACEX, List.of()));

        return new RelatorioAtividadesResponse(
                emailEstudante,
                grupos,
                totalHorasAcc,
                totalHorasAcex,
                totalHorasAcc + totalHorasAcex);
    }

    private List<GrupoCategoriaResponse> agruparPorCategoria(List<AtividadeResponse> atividades) {
        return atividades.stream()
                .collect(Collectors.groupingBy(
                        atividade -> atividade.categoria().name(),
                        LinkedHashMap::new,
                        Collectors.toList()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entrada -> new GrupoCategoriaResponse(
                        entrada.getKey(),
                        somarHoras(entrada.getValue()),
                        entrada.getValue().stream().map(this::paraItem).toList()))
                .toList();
    }

    private ItemAtividadeResponse paraItem(AtividadeResponse atividade) {
        return new ItemAtividadeResponse(
                atividade.id(),
                atividade.titulo(),
                atividade.instituicaoResponsavel(),
                atividade.dataRealizacao(),
                atividade.cargaHorariaEmHoras());
    }

    private int somarHoras(List<AtividadeResponse> atividades) {
        return atividades.stream()
                .mapToInt(AtividadeResponse::cargaHorariaEmHoras)
                .sum();
    }
}
