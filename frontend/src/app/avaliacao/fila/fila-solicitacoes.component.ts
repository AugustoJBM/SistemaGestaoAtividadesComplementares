import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  DecisaoAvaliacao,
  SolicitacaoDetalheAvaliacao,
  SolicitacaoFilaItem,
} from '../avaliacao.model';
import { AvaliacaoService } from '../avaliacao.service';
import { classeStatus, rotuloStatus } from '../../solicitacao/status-solicitacao';
import { dataFormatada } from '../../solicitacao/solicitacao.helpers';

@Component({
  selector: 'app-fila-solicitacoes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fila-solicitacoes.component.html',
})
export class FilaSolicitacoesComponent implements OnInit {
  private readonly avaliacaoService = inject(AvaliacaoService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly mensagemSucesso = signal<string | null>(null);
  readonly solicitacoes = signal<SolicitacaoFilaItem[]>([]);
  readonly semSolicitacoes = computed(() => this.solicitacoes().length === 0);

  // Detalhe inline / expansão de itens
  readonly solicitacaoExpandidaId = signal<number | null>(null);
  readonly detalheExpandido = signal<SolicitacaoDetalheAvaliacao | null>(null);
  readonly carregandoDetalhe = signal(false);
  readonly erroDetalhe = signal<string | null>(null);

  // Modal de Decisão
  readonly modalDecisaoAberto = signal(false);
  readonly solicitacaoSelecionada = signal<SolicitacaoFilaItem | null>(null);
  readonly decisaoSelecionada = signal<DecisaoAvaliacao>('APROVADA');
  readonly justificativa = signal('');
  readonly enviandoDecisao = signal(false);
  readonly erroDecisao = signal<string | null>(null);

  readonly isJustificativaObrigatoria = computed(
    () =>
      this.decisaoSelecionada() === 'REJEITADA' || this.decisaoSelecionada() === 'COM_PENDENCIAS',
  );

  readonly isDecisaoInvalida = computed(() => {
    if (this.enviandoDecisao()) return true;
    if (this.isJustificativaObrigatoria()) {
      return !this.justificativa().trim();
    }
    return false;
  });

  ngOnInit(): void {
    this.carregarFila();
  }

  carregarFila(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.avaliacaoService.listarPendentes().subscribe({
      next: (lista) => {
        this.solicitacoes.set(lista);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      },
    });
  }

  alternarDetalhes(solicitacao: SolicitacaoFilaItem): void {
    if (this.solicitacaoExpandidaId() === solicitacao.id) {
      this.solicitacaoExpandidaId.set(null);
      this.detalheExpandido.set(null);
      this.erroDetalhe.set(null);
      return;
    }

    this.solicitacaoExpandidaId.set(solicitacao.id);
    this.detalheExpandido.set(null);
    this.erroDetalhe.set(null);
    this.carregandoDetalhe.set(true);

    this.avaliacaoService.detalhar(solicitacao.id).subscribe({
      next: (detalhe) => {
        this.detalheExpandido.set(detalhe);
        this.carregandoDetalhe.set(false);
      },
      error: (erro: Error) => {
        this.erroDetalhe.set(erro.message);
        this.carregandoDetalhe.set(false);
      },
    });
  }

  abrirModalDecisao(
    solicitacao: SolicitacaoFilaItem,
    decisaoPadrao: DecisaoAvaliacao = 'APROVADA',
  ): void {
    this.solicitacaoSelecionada.set(solicitacao);
    this.decisaoSelecionada.set(decisaoPadrao);
    this.justificativa.set('');
    this.erroDecisao.set(null);
    this.modalDecisaoAberto.set(true);
  }

  fecharModalDecisao(): void {
    if (this.enviandoDecisao()) return;
    this.modalDecisaoAberto.set(false);
    this.solicitacaoSelecionada.set(null);
    this.erroDecisao.set(null);
  }

  confirmarDecisao(): void {
    const solicitacao = this.solicitacaoSelecionada();
    if (!solicitacao || this.isDecisaoInvalida()) return;

    this.enviandoDecisao.set(true);
    this.erroDecisao.set(null);
    this.mensagemSucesso.set(null);

    const decisao = this.decisaoSelecionada();
    const textoJustificativa = this.justificativa();

    this.avaliacaoService.avaliar(solicitacao.id, decisao, textoJustificativa).subscribe({
      next: () => {
        this.solicitacoes.update((lista) => lista.filter((item) => item.id !== solicitacao.id));
        if (this.solicitacaoExpandidaId() === solicitacao.id) {
          this.solicitacaoExpandidaId.set(null);
          this.detalheExpandido.set(null);
        }
        this.enviandoDecisao.set(false);
        this.modalDecisaoAberto.set(false);
        this.mensagemSucesso.set(
          `Solicitação #${solicitacao.id} de ${solicitacao.estudanteNome} avaliada com sucesso (${rotuloStatus(
            decisao,
          )}).`,
        );
      },
      error: (erro: Error) => {
        this.enviandoDecisao.set(false);
        this.erroDecisao.set(erro.message);
        if (erro.message.includes('já foi avaliada') || erro.message.includes('alterado')) {
          this.carregarFila();
        }
      },
    });
  }

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
