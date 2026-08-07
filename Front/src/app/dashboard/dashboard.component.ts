import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

// PLACEHOLDER: destino pos-login. Existe para que o fluxo de autenticacao
// termine em uma rota real; a tela definitiva ainda sera implementada.
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <main class="p-8">
      <h1 class="text-2xl font-bold">Dashboard</h1>
      <p>Autenticação concluída com sucesso.</p>
      <a routerLink="/logout">Sair</a>
    </main>
  `
})
export class DashboardComponent {}
