import '@angular/compiler';
import { EnvironmentInjector, Injector, runInInjectionContext } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { describe, it, expect, beforeEach, vi, type Mock } from 'vitest';
import { authGuard } from './auth.guard';
import { AutenticacaoService } from './autenticacao.service';

describe('authGuard', () => {
  let authServiceSpy: { isAuthenticated: Mock };
  let routerSpy: { parseUrl: Mock };
  let testInjector: EnvironmentInjector;

  beforeEach(() => {
    authServiceSpy = { isAuthenticated: vi.fn() };
    routerSpy = { parseUrl: vi.fn() };

    testInjector = Injector.create({
      providers: [
        { provide: AutenticacaoService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }) as EnvironmentInjector;
  });

  it('deve permitir o acesso quando o usuario estiver autenticado', () => {
    authServiceSpy.isAuthenticated.mockReturnValue(true);

    const result = runInInjectionContext(testInjector, () => authGuard({} as any, {} as any));

    expect(result).toBe(true);
    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(routerSpy.parseUrl).not.toHaveBeenCalled();
  });

  it('deve redirecionar para /login quando o usuario nao estiver autenticado', () => {
    const dummyUrlTree = {} as UrlTree;
    authServiceSpy.isAuthenticated.mockReturnValue(false);
    routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

    const result = runInInjectionContext(testInjector, () => authGuard({} as any, {} as any));

    expect(result).toBe(dummyUrlTree);
    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/login');
  });
});
