import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DecisaoAvaliacao, SolicitacaoAvaliadorDetalhe } from '../avaliacao.model';
import { AvaliacaoService } from '../avaliacao.service';
import { classeStatus, rotuloStatus } from '../../solicitacao/status-solicitacao';
import { dataFormatada } from '../../solicitacao/solicitacao.helpers';

@Component({
  selector: 'app-detalhe-avaliacao',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './detalhe-avaliacao.component.html',
})
export class DetalheAvaliacaoComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly avaliacaoService = inject(AvaliacaoService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly mensagemSucesso = signal<string | null>(null);
  readonly detalhe = signal<SolicitacaoAvaliadorDetalhe | null>(null);

  // Decisão
  readonly modalDecisaoAberto = signal(false);
  readonly decisaoSelecionada = signal<DecisaoAvaliacao>('APROVADA');
  readonly justificativa = signal('');
  readonly enviandoDecisao = signal(false);
  readonly erroDecisao = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.carregarDetalhe(id);
  }

  carregarDetalhe(id: number): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.avaliacaoService.detalhar(id).subscribe({
      next: (detalhe) => {
        this.detalhe.set(detalhe);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      },
    });
  }

  abrirModalDecisao(decisao: DecisaoAvaliacao): void {
    this.decisaoSelecionada.set(decisao);
    this.justificativa.set('');
    this.erroDecisao.set(null);
    this.modalDecisaoAberto.set(true);
  }

  fecharModalDecisao(): void {
    if (this.enviandoDecisao()) return;
    this.modalDecisaoAberto.set(false);
    this.erroDecisao.set(null);
  }

  confirmarDecisao(): void {
    const d = this.detalhe();
    if (!d || this.isDecisaoInvalida()) return;

    this.enviandoDecisao.set(true);
    this.erroDecisao.set(null);

    this.avaliacaoService.avaliar(d.id, this.decisaoSelecionada(), this.justificativa()).subscribe({
      next: (atualizado) => {
        this.detalhe.set(atualizado);
        this.enviandoDecisao.set(false);
        this.modalDecisaoAberto.set(false);
        this.mensagemSucesso.set(`Solicitação #${d.id} avaliada com sucesso.`);
      },
      error: (erro: Error) => {
        this.enviandoDecisao.set(false);
        this.erroDecisao.set(erro.message);
      },
    });
  }

  isJustificativaObrigatoria(): boolean {
    return (
      this.decisaoSelecionada() === 'REJEITADA' || this.decisaoSelecionada() === 'COM_PENDENCIAS'
    );
  }

  isDecisaoInvalida(): boolean {
    if (this.enviandoDecisao()) return true;
    if (this.isJustificativaObrigatoria()) {
      return !this.justificativa().trim();
    }
    return false;
  }

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
