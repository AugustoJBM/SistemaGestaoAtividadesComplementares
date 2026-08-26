import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitacaoDetalhe } from '../solicitacao.model';
import { SolicitacaoService } from '../solicitacao.service';
import { rotuloStatus } from '../status-solicitacao';

@Component({
  selector: 'app-submissao-solicitacao',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './submissao-solicitacao.component.html'
})
export class SubmissaoSolicitacaoComponent {
  private readonly solicitacaoService = inject(SolicitacaoService);

  readonly confirmacaoAberta = signal(false);
  readonly enviando = signal(false);
  readonly mensagemErro = signal<string | null>(null);
  readonly solicitacaoEnviada = signal<SolicitacaoDetalhe | null>(null);

  abrirConfirmacao(): void {
    this.mensagemErro.set(null);
    this.confirmacaoAberta.set(true);
  }

  cancelar(): void {
    if (this.enviando()) return;
    this.confirmacaoAberta.set(false);
  }

  confirmarSubmissao(): void {
    if (this.enviando()) return;

    this.enviando.set(true);
    this.mensagemErro.set(null);

    this.solicitacaoService.submeter().subscribe({
      next: (solicitacao) => {
        this.solicitacaoEnviada.set(solicitacao);
        this.confirmacaoAberta.set(false);
        this.enviando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.confirmacaoAberta.set(false);
        this.enviando.set(false);
      }
    });
  }

  readonly rotuloStatus = rotuloStatus;
}
