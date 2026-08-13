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
      <h1 class="text-2xl font-bold mb-2">Dashboard</h1>
      <p class="text-gray-600">Autenticação concluída com sucesso.</p>
      <nav class="mt-6 flex flex-wrap gap-4">
        <a routerLink="/atividades/cadastro" class="px-4 py-2 bg-[#003629] text-white rounded-lg hover:bg-[#1b4d3e] transition-colors">
          Cadastrar nova atividade
        </a>
        <a routerLink="/progresso" class="px-4 py-2 border border-[#003629] text-[#003629] rounded-lg hover:bg-[#f3f4f5] transition-colors">
          Acompanhar carga horária
        </a>
        <a routerLink="/logout" class="px-4 py-2 border border-red-600 text-red-600 rounded-lg hover:bg-red-50 transition-colors">
          Sair
        </a>
      </nav>
    </main>
  `
})
export class DashboardComponent { }
