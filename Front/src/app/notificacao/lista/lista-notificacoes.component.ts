import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Notificacao } from '../notificacao.model';
import { NotificacaoService } from '../notificacao.service';

@Component({
  selector: 'app-lista-notificacoes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-notificacoes.component.html',
})
export class ListaNotificacoesComponent implements OnInit {
  private readonly notificacaoService = inject(NotificacaoService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly notificacoes = signal<Notificacao[]>([]);
  readonly apenasNaoLidas = signal(false);
  readonly marcandoId = signal<number | null>(null);
  readonly marcandoTodas = signal(false);

  readonly semNotificacoes = computed<boolean>(() => this.notificacoes().length === 0);
  readonly totalNaoLidas = computed<number>(
    () => this.notificacoes().filter((n) => !n.lida).length,
  );

  ngOnInit(): void {
    this.carregarNotificacoes();
  }

  carregarNotificacoes(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    const filtro = this.apenasNaoLidas() ? true : undefined;

    this.notificacaoService.listar(filtro).subscribe({
      next: (dados) => {
        this.notificacoes.set(dados);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      },
    });
  }

  alternarFiltro(apenasNaoLidas: boolean): void {
    if (this.apenasNaoLidas() === apenasNaoLidas) return;
    this.apenasNaoLidas.set(apenasNaoLidas);
    this.carregarNotificacoes();
  }

  marcarComoLida(notificacao: Notificacao): void {
    if (notificacao.lida || this.marcandoId() === notificacao.id) return;
    this.marcandoId.set(notificacao.id);

    this.notificacaoService.marcarComoLida(notificacao.id).subscribe({
      next: () => {
        this.notificacoes.update((atuais) =>
          atuais.map((item) => (item.id === notificacao.id ? { ...item, lida: true } : item)),
        );
        this.marcandoId.set(null);
      },
      error: () => {
        this.marcandoId.set(null);
      },
    });
  }

  marcarTodasComoLidas(): void {
    if (this.marcandoTodas() || this.totalNaoLidas() === 0) return;
    this.marcandoTodas.set(true);

    this.notificacaoService.marcarTodasComoLidas().subscribe({
      next: () => {
        this.notificacoes.update((atuais) => atuais.map((item) => ({ ...item, lida: true })));
        this.marcandoTodas.set(false);
      },
      error: () => {
        this.marcandoTodas.set(false);
      },
    });
  }

  dataFormatada(dataIso: string): string {
    if (!dataIso) return '';
    const partes = dataIso.split('T');
    const dataPartes = partes[0].split('-');
    if (dataPartes.length !== 3) return '';
    const [ano, mes, dia] = dataPartes;
    if (partes.length > 1) {
      const horaMinuto = partes[1].substring(0, 5);
      return `${dia}/${mes}/${ano} às ${horaMinuto}`;
    }
    return `${dia}/${mes}/${ano}`;
  }
}
