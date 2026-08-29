import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificacaoService } from '../notificacao.service';
import { catchError, EMPTY } from 'rxjs';

@Component({
  selector: 'app-contador-notificacoes',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <a
      routerLink="/notificacoes"
      class="relative inline-flex items-center justify-center p-2 text-[#404945] hover:text-[#191c1d] transition-colors focus:outline-none focus:ring-2 focus:ring-[#003629] rounded-full"
      [attr.aria-label]="naoLidas() > 0 ? naoLidas() + ' notificações não lidas' : 'Nenhuma notificação não lida'"
    >
      <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-6 h-6">
        <path stroke-linecap="round" stroke-linejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
      </svg>

      @if (naoLidas() > 0) {
        <span class="absolute top-1 right-1 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold leading-none text-white transform translate-x-1/4 -translate-y-1/4 rounded-full bg-[#ba1a1a]">
          {{ naoLidas() > 99 ? '99+' : naoLidas() }}
        </span>
      }
    </a>
  `
})
export class ContadorNotificacoesComponent implements OnInit {
  private readonly notificacaoService = inject(NotificacaoService);
  readonly naoLidas = signal<number>(0);

  ngOnInit(): void {
    this.notificacaoService.contarNaoLidas()
      .pipe(
        catchError(() => EMPTY)
      )
      .subscribe(res => {
        this.naoLidas.set(res.naoLidas);
      });
  }
}
