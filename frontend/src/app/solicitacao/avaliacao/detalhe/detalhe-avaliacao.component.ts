import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SolicitacaoAvaliadorDetalhe } from '../avaliacao.model';
import { AvaliacaoSolicitacaoService } from '../avaliacao-solicitacao.service';
import { classeStatus, rotuloStatus } from '../../status-solicitacao';
import { dataFormatada } from '../../solicitacao.helpers';

@Component({
  selector: 'app-detalhe-avaliacao',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './detalhe-avaliacao.component.html',
})
export class DetalheAvaliacaoComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly avaliacaoService = inject(AvaliacaoSolicitacaoService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly detalhe = signal<SolicitacaoAvaliadorDetalhe | null>(null);

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

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
