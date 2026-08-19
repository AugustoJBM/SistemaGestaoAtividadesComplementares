import { Routes } from '@angular/router';
import { RegistroComponent } from './autenticacao/registro/registro.component';
import { LogoutComponent } from './autenticacao/logout/logout.component';
import { LoginComponent } from './autenticacao/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProgressoComponent } from './atividades/progresso/progresso.component';
import { authGuard } from './autenticacao/auth.guard';
import { CadastroAtividadeComponent } from './atividades/cadastro/cadastro-atividade.component';
import { ListagemAtividadesComponent } from './atividades/listagem/listagem-atividades.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'progresso', component: ProgressoComponent, canActivate: [authGuard] },
    { path: 'atividades/cadastro', component: CadastroAtividadeComponent, canActivate: [authGuard] },
    { path: 'atividades', component: ListagemAtividadesComponent, canActivate: [authGuard] },
    { path: 'logout', component: LogoutComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'login' }
];