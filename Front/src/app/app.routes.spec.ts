import { describe, it, expect } from 'vitest';
import { Route } from '@angular/router';

import { routes } from './app.routes';
import { authGuard } from './autenticacao/auth.guard';

describe('routes', () => {
  it('deve redirecionar a rota raiz para login com pathMatch full', () => {
    const raiz = routes.find((rota: Route) => rota.path === '');

    expect(raiz).toBeTruthy();
    expect(raiz?.redirectTo).toBe('login');
    expect(raiz?.pathMatch).toBe('full');
  });

  it('deve expor a rota login sem authGuard', () => {
    const login = routes.find((rota: Route) => rota.path === 'login');

    expect(login).toBeTruthy();
    expect(login?.canActivate).toBeUndefined();
  });

  it('deve expor a rota registro sem authGuard', () => {
    const registro = routes.find((rota: Route) => rota.path === 'registro');

    expect(registro).toBeTruthy();
    expect(registro?.canActivate).toBeUndefined();
  });

  it('deve proteger a rota dashboard com authGuard', () => {
    const dashboard = routes.find((rota: Route) => rota.path === 'dashboard');

    expect(dashboard).toBeTruthy();
    expect(dashboard?.canActivate).toContain(authGuard);
  });

  it('deve proteger a rota logout com authGuard', () => {
    const logout = routes.find((rota: Route) => rota.path === 'logout');

    expect(logout).toBeTruthy();
    expect(logout?.canActivate).toContain(authGuard);
  });

  it('deve proteger a rota progresso com authGuard', () => {
    const progresso = routes.find((rota: Route) => rota.path === 'progresso');

    expect(progresso).toBeTruthy();
    expect(progresso?.canActivate).toContain(authGuard);
  });

  it('deve proteger a rota de edicao de atividade com authGuard', () => {
    const edicao = routes.find((rota: Route) => rota.path === 'atividades/edicao/:id');
    expect(edicao).toBeTruthy();
    expect(edicao?.canActivate).toContain(authGuard);
  });

  it('deve proteger a rota de listagem de atividades com authGuard', () => {
    const listagem = routes.find((rota: Route) => rota.path === 'atividades');

    expect(listagem).toBeTruthy();
    expect(listagem?.canActivate).toContain(authGuard);
  });

  it('deve declarar a rota de cadastro e edicao antes da listagem para nao capturar o segmento extra', () => {
    const indiceCadastro = routes.findIndex((rota: Route) => rota.path === 'atividades/cadastro');
    const indiceEdicao = routes.findIndex((rota: Route) => rota.path === 'atividades/edicao/:id');
    const indiceListagem = routes.findIndex((rota: Route) => rota.path === 'atividades');
    expect(indiceCadastro).toBeGreaterThanOrEqual(0);
    expect(indiceCadastro).toBeLessThan(indiceListagem);
    expect(indiceEdicao).toBeGreaterThanOrEqual(0);
    expect(indiceEdicao).toBeLessThan(indiceListagem);
  });

  it('deve proteger a rota de relatorio com authGuard', () => {
    const rota = routes.find((r: Route) => r.path === 'relatorio');
    expect(rota).toBeTruthy();
    expect(rota?.canActivate).toEqual([authGuard]);
  });

  it('deve manter a rota curinga como a última entrada redirecionando para login', () => {
    const ultimaRota = routes[routes.length - 1];

    expect(ultimaRota.path).toBe('**');
    expect(ultimaRota.redirectTo).toBe('login');
  });
});
