import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { Atividade } from '../atividade.model';
import { AtividadeService } from '../atividade.service';

const ROTULOS_NATUREZA: Record<string, string> = {
  ACC: 'ACC',
  ACEX: 'ACEX'
};

const ROTULOS_CATEGORIA: Record<string, string> = {
  PESQUISA: 'Pesquisa',
  EXTENSAO: 'Extensão',
  ENSINO: 'Ensino',
  EVENTOS: 'Eventos'
};

@Component({
  selector: 'app-listagem-atividades',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './listagem-atividades.component.html'
})
export class ListagemAtividadesComponent implements OnInit {
  private readonly atividadeService = inject(AtividadeService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly atividades = signal<Atividade[]>([]);

  readonly semAtividades = computed<boolean>(() => this.atividades().length === 0);

  ngOnInit(): void {
    this.buscarAtividades();
  }

  buscarAtividades(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);

    this.atividadeService.listar().subscribe({
      next: (atividades) => {
        this.atividades.set(atividades);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      }
    });
  }

  // Converte a data ISO do backend sem passar por Date, que deslocaria o dia
  // ao interpretar 'yyyy-MM-dd' como UTC em fusos negativos.
  dataFormatada(dataIso: string): string {
    const partes = dataIso.split('-');
    if (partes.length !== 3) {
      return '—';
    }

    const [ano, mes, dia] = partes;
    return `${dia}/${mes}/${ano}`;
  }

  rotuloNatureza(natureza: string): string {
    return ROTULOS_NATUREZA[natureza] ?? '—';
  }

  rotuloCategoria(categoria: string): string {
    return ROTULOS_CATEGORIA[categoria] ?? '—';
  }
}
