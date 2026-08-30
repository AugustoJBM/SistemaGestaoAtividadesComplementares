import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../../../autenticacao/autenticacao.service';
import { Role } from '../../../autenticacao/autenticacao.model';
import { ContadorNotificacoesComponent } from '../../../notificacao/contador/contador-notificacoes.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, ContadorNotificacoesComponent],
  templateUrl: './navbar.component.html',
})
export class NavbarComponent {
  private readonly authService = inject(AutenticacaoService);

  get perfil(): Role | null {
    return this.authService.perfilAtual();
  }

  get estaAutenticado(): boolean {
    return this.authService.isAuthenticated();
  }

  get isEstudante(): boolean {
    return this.perfil === 'ESTUDANTE';
  }

  get isAvaliador(): boolean {
    return this.perfil === 'AVALIADOR' || this.perfil === 'ADMINISTRADOR';
  }

  get isAdmin(): boolean {
    return this.perfil === 'ADMINISTRADOR';
  }
}
