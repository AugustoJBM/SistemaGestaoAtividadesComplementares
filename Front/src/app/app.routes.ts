import { Routes } from '@angular/router';
import { RegistroComponent } from './autenticacao/registro/registro.component';
import { LogoutComponent } from './autenticacao/logout/logout.component';
import { LoginComponent } from './autenticacao/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProgressoComponent } from './atividades/progresso/progresso.component';
import { authGuard } from './autenticacao/auth.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'progresso', component: ProgressoComponent, canActivate: [authGuard] },
    { path: 'logout', component: LogoutComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: 'login' }
];