import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { StatusSolicitacao } from '../../solicitacao.model';
import { SolicitacaoAvaliadorResumo } from '../avaliacao.model';
import { AvaliacaoSolicitacaoService } from '../avaliacao-solicitacao.service';
import { classeStatus, rotuloStatus } from '../../status-solicitacao';
import { dataFormatada } from '../../solicitacao.helpers';

interface OpcaoFiltroStatus {
  valor: StatusSolicitacao | '';
  rotulo: string;
}

const OPCOES_FILTRO_STATUS: OpcaoFiltroStatus[] = [
  { valor: '', rotulo: 'Todos' },
  { valor: 'SUBMETIDA', rotulo: rotuloStatus('SUBMETIDA') },
  { valor: 'EM_ANALISE', rotulo: rotuloStatus('EM_ANALISE') },
  { valor: 'COM_PENDENCIAS', rotulo: rotuloStatus('COM_PENDENCIAS') },
  { valor: 'APROVADA', rotulo: rotuloStatus('APROVADA') },
  { valor: 'REJEITADA', rotulo: rotuloStatus('REJEITADA') },
];

@Component({
  selector: 'app-consulta-solicitacoes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './consulta-solicitacoes.component.html',
})
export class ConsultaSolicitacoesComponent implements OnInit {
  private readonly avaliacaoService = inject(AvaliacaoSolicitacaoService);
  private readonly router = inject(Router);

  readonly opcoesFiltroStatus = OPCOES_FILTRO_STATUS;
  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly solicitacoes = signal<SolicitacaoAvaliadorResumo[]>([]);
  readonly filtroStatus = signal<StatusSolicitacao | ''>('');
  readonly semSolicitacoes = computed<boolean>(() => this.solicitacoes().length === 0);

  ngOnInit(): void {
    this.carregarSolicitacoes();
  }

  carregarSolicitacoes(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    const status = this.filtroStatus() || undefined;
    this.avaliacaoService.consultar(status).subscribe({
      next: (solicitacoes) => {
        this.solicitacoes.set(solicitacoes);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      },
    });
  }

  alterarFiltro(status: string): void {
    this.filtroStatus.set(status as StatusSolicitacao | '');
    this.carregarSolicitacoes();
  }

  abrirDetalhe(solicitacao: SolicitacaoAvaliadorResumo): void {
    this.router.navigate(['/avaliacao/solicitacoes', solicitacao.id]);
  }

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
