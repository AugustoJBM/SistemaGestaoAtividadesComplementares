import '@angular/compiler';
import { EnvironmentInjector, Injector, runInInjectionContext } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { describe, it, expect, beforeEach, vi, type Mock } from 'vitest';
import { roleGuard } from './role.guard';
import { AutenticacaoService } from './autenticacao.service';

describe('roleGuard', () => {
    let authServiceSpy: { isAuthenticated: Mock; getRole: Mock };
    let routerSpy: { parseUrl: Mock };
    let testInjector: EnvironmentInjector;

    beforeEach(() => {
        authServiceSpy = { isAuthenticated: vi.fn(), getRole: vi.fn() };
        routerSpy = { parseUrl: vi.fn() };
        testInjector = Injector.create({
            providers: [
                { provide: AutenticacaoService, useValue: authServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        }) as EnvironmentInjector;
    });

    it('deve redirecionar para /login se não estiver autenticado', () => {
        authServiceSpy.isAuthenticated.mockReturnValue(false);
        const dummyUrlTree = {} as UrlTree;
        routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

        const guard = roleGuard(['ADMINISTRADOR']);
        const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

        expect(result).toBe(dummyUrlTree);
        expect(routerSpy.parseUrl).toHaveBeenCalledWith('/login');
    });

    it('deve redirecionar para /dashboard quando a role estiver ausente no token', () => {
        authServiceSpy.isAuthenticated.mockReturnValue(true);
        authServiceSpy.getRole.mockReturnValue(null);
        const dummyUrlTree = {} as UrlTree;
        routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

        const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
        const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

        expect(result).toBe(dummyUrlTree);
        expect(routerSpy.parseUrl).toHaveBeenCalledWith('/dashboard');
    });

    it('deve permitir o acesso quando o usuário possui papel permitido', () => {
        authServiceSpy.isAuthenticated.mockReturnValue(true);
        authServiceSpy.getRole.mockReturnValue('ADMINISTRADOR');

        const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
        const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

        expect(result).toBe(true);
        expect(routerSpy.parseUrl).not.toHaveBeenCalled();
    });

    it('deve redirecionar para /dashboard quando o usuário possui papel não permitido', () => {
        authServiceSpy.isAuthenticated.mockReturnValue(true);
        authServiceSpy.getRole.mockReturnValue('ESTUDANTE');
        const dummyUrlTree = {} as UrlTree;
        routerSpy.parseUrl.mockReturnValue(dummyUrlTree);

        const guard = roleGuard(['ADMINISTRADOR', 'AVALIADOR']);
        const result = runInInjectionContext(testInjector, () => guard({} as any, {} as any));

        expect(result).toBe(dummyUrlTree);
        expect(routerSpy.parseUrl).toHaveBeenCalledWith('/dashboard');
    });
});