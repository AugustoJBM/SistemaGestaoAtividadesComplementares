import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AutenticacaoService } from '../../../autenticacao/autenticacao.service';
import { Role } from '../../../autenticacao/autenticacao.model';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
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
    const papel = this.perfil;
    return papel === 'AVALIADOR' || papel === 'ADMINISTRADOR';
  }
}
