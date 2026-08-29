import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SolicitacaoService } from '../../solicitacao/solicitacao.service';
import { SolicitacaoResumo } from '../../solicitacao/solicitacao.model';

@Component({
  selector: 'app-situacao-solicitacao',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    @if (!carregando() && !erro()) {
      <div class="bg-white p-6 rounded-lg shadow-md border border-gray-200 mb-6">
        @if (!solicitacaoMaisRecente()) {
          <div class="flex flex-col sm:flex-row justify-between items-center gap-4">
            <div>
              <h3 class="text-lg font-semibold text-gray-800">Atividades Complementares</h3>
              <p class="text-sm text-gray-600">Você ainda não enviou nenhuma solicitação de validação.</p>
            </div>
            <a routerLink="/solicitacoes" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition text-sm font-medium">
              Submeter Solicitação
            </a>
          </div>
        } @else if (solicitacaoMaisRecente()?.status === 'SUBMETIDA' || solicitacaoMaisRecente()?.status === 'EM_ANALISE') {
          <div class="flex flex-col sm:flex-row justify-between items-center gap-4">
            <div>
              <span class="inline-block bg-yellow-100 text-yellow-800 text-xs px-2.5 py-0.5 rounded font-semibold mb-1">
                {{ solicitacaoMaisRecente()?.status }}
              </span>
              <h3 class="text-lg font-semibold text-gray-800">Solicitação em Andamento</h3>
              <p class="text-sm text-gray-600">Submetida em: {{ solicitacaoMaisRecente()?.dataSubmissao | date:'dd/MM/yyyy' }}</p>
            </div>
            <a routerLink="/solicitacoes" class="text-blue-600 hover:underline text-sm font-medium">
              Ver detalhes &rarr;
            </a>
          </div>
        } @else if (solicitacaoMaisRecente()?.status === 'COM_PENDENCIAS' || solicitacaoMaisRecente()?.status === 'REJEITADA') {
          <div class="bg-red-50 border-l-4 border-red-500 p-4 rounded flex flex-col sm:flex-row justify-between items-center gap-4">
            <div>
              <span class="inline-block bg-red-100 text-red-800 text-xs px-2.5 py-0.5 rounded font-semibold mb-1">
                Atenção Necessária: {{ solicitacaoMaisRecente()?.status }}
              </span>
              <h3 class="text-lg font-semibold text-red-900">Sua solicitação requer ajustes</h3>
              <p class="text-sm text-red-700">Verifique as pendências apontadas pelo avaliador.</p>
            </div>
            <a routerLink="/solicitacoes" class="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition text-sm font-medium">
              Resolver Pendências
            </a>
          </div>
        }
      </div>
    }
  `
})
export class SituacaoSolicitacaoComponent implements OnInit {
  private solicitacaoService = inject(SolicitacaoService);

  solicitacaoMaisRecente = signal<SolicitacaoResumo | null>(null);
  carregando = signal<boolean>(true);
  erro = signal<boolean>(false);

  ngOnInit(): void {
    this.solicitacaoService.listar().subscribe({
      next: (lista) => {
        if (lista && lista.length > 0) {
          const maisRecente = lista.reduce((prev, current) => (prev.id > current.id) ? prev : current);
          this.solicitacaoMaisRecente.set(maisRecente);
        } else {
          this.solicitacaoMaisRecente.set(null);
        }
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set(true);
        this.carregando.set(false);
      }
    });
  }
}
