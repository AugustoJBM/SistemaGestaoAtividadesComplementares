import { Routes } from '@angular/router';
import { RegistroComponent } from './autenticacao/registro/registro.component';
import { LogoutComponent } from './autenticacao/logout/logout.component';
import { LoginComponent } from './autenticacao/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProgressoComponent } from './atividades/progresso/progresso.component';
import { authGuard } from './autenticacao/auth.guard';
import { CadastroAtividadeComponent } from './atividades/cadastro/cadastro-atividade.component';
import { ListagemAtividadesComponent } from './atividades/listagem/listagem-atividades.component';
import { RelatorioComponent } from './relatorio/relatorio.component';
import { AcompanhamentoSolicitacoesComponent } from './solicitacao/acompanhamento/acompanhamento-solicitacoes.component';
import { EdicaoAtividadeComponent } from './atividades/edicao/edicao-atividade.component';
import { LandingComponent } from './landing/landing.component';
import { GestaoRegulamentosComponent } from './regulamentos/gestao-regulamentos.component';
import { roleGuard } from './autenticacao/role.guard';

export const routes: Routes = [
    { path: '', component: LandingComponent },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'progresso', component: ProgressoComponent, canActivate: [authGuard] },
    { path: 'atividades/cadastro', component: CadastroAtividadeComponent, canActivate: [authGuard] },
    { path: 'atividades/edicao/:id', component: EdicaoAtividadeComponent, canActivate: [authGuard] },
    { path: 'atividades', component: ListagemAtividadesComponent, canActivate: [authGuard] },
    {
        path: 'regulamentos/gestao',
        component: GestaoRegulamentosComponent,
        canActivate: [authGuard, roleGuard(['ADMINISTRADOR', 'AVALIADOR'])]
    },
    { path: 'relatorio', component: RelatorioComponent, canActivate: [authGuard] },
    { path: 'solicitacoes', component: AcompanhamentoSolicitacoesComponent, canActivate: [authGuard] },
    { path: 'logout', component: LogoutComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'login' }
];