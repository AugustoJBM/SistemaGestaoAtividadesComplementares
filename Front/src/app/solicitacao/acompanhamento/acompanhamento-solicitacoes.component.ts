import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitacaoDetalhe, SolicitacaoResumo } from '../solicitacao.model';
import { SolicitacaoService } from '../solicitacao.service';
import { classeStatus, rotuloStatus } from '../status-solicitacao';
import { dataFormatada } from '../solicitacao.helpers';
import { DetalheSolicitacaoComponent } from '../detalhe/detalhe-solicitacao.component';

@Component({
  selector: 'app-acompanhamento-solicitacoes',
  standalone: true,
  imports: [CommonModule, RouterLink, DetalheSolicitacaoComponent],
  templateUrl: './acompanhamento-solicitacoes.component.html'
})
export class AcompanhamentoSolicitacoesComponent implements OnInit {
  private readonly solicitacaoService = inject(SolicitacaoService);
  private detalheRequisicaoAtual = 0;

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly solicitacoes = signal<SolicitacaoResumo[]>([]);
  readonly semSolicitacoes = computed<boolean>(() => this.solicitacoes().length === 0);
  readonly idSelecionado = signal<number | null>(null);
  readonly detalheSelecionado = signal<SolicitacaoDetalhe | null>(null);
  readonly carregandoDetalhe = signal(false);
  readonly erroDetalhe = signal<string | null>(null);

  ngOnInit(): void {
    this.carregarSolicitacoes();
  }

  carregarSolicitacoes(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.solicitacaoService.listar().subscribe({
      next: (solicitacoes) => {
        this.solicitacoes.set(solicitacoes);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      }
    });
  }

  selecionar(solicitacao: SolicitacaoResumo): void {
    const id = solicitacao.id;
    const requisicao = ++this.detalheRequisicaoAtual;

    this.idSelecionado.set(id);
    this.detalheSelecionado.set(null);
    this.erroDetalhe.set(null);
    this.carregandoDetalhe.set(true);

    this.solicitacaoService.detalhar(id).subscribe({
      next: (detalhe) => {
        if (this.idSelecionado() !== id || this.detalheRequisicaoAtual !== requisicao) return;

        this.detalheSelecionado.set(detalhe);
        this.carregandoDetalhe.set(false);
      },
      error: (erro: Error) => {
        if (this.idSelecionado() !== id || this.detalheRequisicaoAtual !== requisicao) return;

        this.erroDetalhe.set(erro.message);
        this.carregandoDetalhe.set(false);
      }
    });
  }

  fecharDetalhe(): void {
    this.detalheRequisicaoAtual += 1;
    this.idSelecionado.set(null);
    this.detalheSelecionado.set(null);
    this.erroDetalhe.set(null);
    this.carregandoDetalhe.set(false);
  }

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
