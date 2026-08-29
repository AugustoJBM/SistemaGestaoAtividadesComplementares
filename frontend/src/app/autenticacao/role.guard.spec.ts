import '@angular/compiler';
import { EnvironmentInjector, Injector, runInInjectionContext } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { describe, it, expect, beforeEach, vi, type Mock } from 'vitest';
import { roleGuard } from './role.guard';
import { AutenticacaoService } from './autenticacao.service';

describe('roleGuard', () => {
  let authServiceSpy: { isAuthenticated: Mock; perfilAtual: Mock; getRole: Mock };
  let routerSpy: { parseUrl: Mock };
  let testInjector: EnvironmentInjector;

  beforeEach(() => {
    authServiceSpy = { isAuthenticated: vi.fn(), perfilAtual: vi.fn(), getRole: vi.fn() };
    routerSpy = {
      parseUrl: vi.fn((url: string) => ({ toString: () => url }) as unknown as UrlTree),
    };
    testInjector = Injector.create({
      providers: [
        { provide: AutenticacaoService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }) as EnvironmentInjector;
  });

  it('deve redirecionar para /login se não estiver autenticado', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(false);
    const dummyUrlTree = routerSpy.parseUrl('/login');
    routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

    const guard = roleGuard(['ADMINISTRADOR']);
    const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

    expect(result).toBe(dummyUrlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/login');
  });

  it('deve redirecionar para /dashboard quando a role estiver ausente no token', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);
    authServiceSpy.perfilAtual.mockReturnValue(null);
    authServiceSpy.getRole.mockReturnValue(null);
    const dummyUrlTree = routerSpy.parseUrl('/dashboard');
    routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

    const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
    const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

    expect(result).toBe(dummyUrlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/dashboard');
  });

  it('deve permitir o acesso quando o usuário possui papel permitido', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);
    authServiceSpy.perfilAtual.mockReturnValue('ADMINISTRADOR');
    authServiceSpy.getRole.mockReturnValue('ADMINISTRADOR');

    const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
    const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

    expect(result).toBe(true);
    expect(routerSpy.parseUrl).not.toHaveBeenCalled();
  });

  it('deve redirecionar para /dashboard quando o usuário for ESTUDANTE e tentar rota restrita', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);
    authServiceSpy.perfilAtual.mockReturnValue('ESTUDANTE');
    authServiceSpy.getRole.mockReturnValue('ESTUDANTE');
    const dummyUrlTree = routerSpy.parseUrl('/dashboard');
    routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

    const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
    const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

    expect(result).toBe(dummyUrlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/dashboard');
  });

  it('deve redirecionar para /avaliacao/solicitacoes quando o usuário for AVALIADOR e tentar rota de estudante', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);
    authServiceSpy.perfilAtual.mockReturnValue('AVALIADOR');
    authServiceSpy.getRole.mockReturnValue('AVALIADOR');
    const dummyUrlTree = routerSpy.parseUrl('/avaliacao/solicitacoes');
    routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

    const guard = roleGuard(['ESTUDANTE']);
    const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

    expect(result).toBe(dummyUrlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/avaliacao/solicitacoes');
  });
});
