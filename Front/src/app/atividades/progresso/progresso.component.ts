import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProgressoModalidade, ProgressoCargaHoraria } from './progresso.model';
import { ProgressoService } from './progresso.service';


interface CardProgresso {
  titulo: string;
  descricao: string;
  dados: ProgressoModalidade;
}

@Component({
  selector: 'app-progresso',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './progresso.component.html'
})
export class ProgressoComponent implements OnInit {
  private readonly progressoService = inject(ProgressoService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly progresso = signal<ProgressoCargaHoraria | null>(null);

  readonly cards = computed<CardProgresso[]>(() => {
    const progresso = this.progresso();
    if (!progresso) {
      return [];
    }

    return [
      { titulo: 'ACC', descricao: 'Atividades Complementares de Curso', dados: progresso.acc },
      { titulo: 'ACEX', descricao: 'Atividades de Extensão', dados: progresso.acex }
    ];
  });

  readonly semAtividades = computed<boolean>(() => {
    const progresso = this.progresso();
    if (!progresso) {
      return false;
    }

    return progresso.acc.horasAcumuladas + progresso.acex.horasAcumuladas === 0;
  });

  ngOnInit(): void {
    this.buscarProgresso();
  }

  // Trava o percentual visual entre 0 e 100, mesmo que a API devolva um
  // valor fora desse intervalo.
  percentualExibido(dados: ProgressoModalidade): number {
    return Math.min(100, Math.max(0, dados.percentualConcluido));
  }

  private buscarProgresso(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);

    this.progressoService.obterProgresso().subscribe({
      next: (progresso) => {
        this.progresso.set(progresso);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      }
    });
  }
}
