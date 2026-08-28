import { Routes } from '@angular/router';
import { authGuard } from './autenticacao/auth.guard';
import { roleGuard } from './autenticacao/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/landing.component').then((m) => m.LandingComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./autenticacao/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./autenticacao/registro/registro.component').then((m) => m.RegistroComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./dashboard/dashboard.component').then((m) => m.DashboardComponent),
    canActivate: [authGuard],
  },
  {
    path: 'progresso',
    loadComponent: () =>
      import('./atividades/progresso/progresso.component').then((m) => m.ProgressoComponent),
    canActivate: [authGuard],
  },
  {
    path: 'atividades/cadastro',
    loadComponent: () =>
      import('./atividades/cadastro/cadastro-atividade.component').then(
        (m) => m.CadastroAtividadeComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'atividades/edicao/:id',
    loadComponent: () =>
      import('./atividades/edicao/edicao-atividade.component').then(
        (m) => m.EdicaoAtividadeComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'atividades',
    loadComponent: () =>
      import('./atividades/listagem/listagem-atividades.component').then(
        (m) => m.ListagemAtividadesComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'regulamentos/gestao',
    loadComponent: () =>
      import('./regulamentos/gestao-regulamentos.component').then(
        (m) => m.GestaoRegulamentosComponent,
      ),
    canActivate: [authGuard, roleGuard(['ADMINISTRADOR', 'AVALIADOR'])],
  },
  {
    path: 'relatorio',
    loadComponent: () =>
      import('./relatorio/relatorio.component').then((m) => m.RelatorioComponent),
    canActivate: [authGuard],
  },
  {
    path: 'solicitacoes',
    loadComponent: () =>
      import('./solicitacao/acompanhamento/acompanhamento-solicitacoes.component').then(
        (m) => m.AcompanhamentoSolicitacoesComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'notificacoes',
    loadComponent: () =>
      import('./notificacao/lista/lista-notificacoes.component').then(
        (m) => m.ListaNotificacoesComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'logout',
    loadComponent: () =>
      import('./autenticacao/logout/logout.component').then((m) => m.LogoutComponent),
    canActivate: [authGuard],
  },
  {
    path: 'avaliacao/solicitacoes',
    loadComponent: () =>
      import('./solicitacao/avaliacao/consulta/consulta-solicitacoes.component').then(
        (m) => m.ConsultaSolicitacoesComponent,
      ),
    canActivate: [authGuard, roleGuard(['AVALIADOR'])],
  },
  {
    path: 'avaliacao/solicitacoes/:id',
    loadComponent: () =>
      import('./solicitacao/avaliacao/detalhe/detalhe-avaliacao.component').then(
        (m) => m.DetalheAvaliacaoComponent,
      ),
    canActivate: [authGuard, roleGuard(['AVALIADOR'])],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
