import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, afterEach, vi, type Mock } from 'vitest';

import { AutenticacaoService } from '../autenticacao.service';
import { LogoutService } from './logout.service';
import { LogoutComponent } from './logout.component';

const createStorageMock = () => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => { store[key] = String(value); },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; },
    get length() { return Object.keys(store).length; }
  };
};

describe('Componente de Logout', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;
  let authService: AutenticacaoService;
  let logoutServiceSpy: { logout: Mock };
  let router: Router;

  // Apenas a fronteira HTTP e dublada. O AutenticacaoService roda de verdade,
  // para que residuo de qualquer chave de sessao seja detectado pelo teste.
  const configurar = async () => {
    await TestBed.configureTestingModule({
      imports: [LogoutComponent, RouterTestingModule.withRoutes([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LogoutService, useValue: logoutServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AutenticacaoService);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    authService.saveToken('dummy-jwt-token', 'Bearer');
    sessionStorage.setItem('user-data', JSON.stringify({ nome: 'Teste' }));
    fixture.detectChanges();
  };

  beforeEach(async () => {
    vi.stubGlobal('localStorage', createStorageMock());
    vi.stubGlobal('sessionStorage', createStorageMock());
    logoutServiceSpy = { logout: vi.fn(() => of(void 0)) };
    await configurar();
  });

  afterEach(() => {
    localStorage.clear();
    sessionStorage.clear();
  });

  it('não deve deixar informação residual de autenticação no navegador', () => {
    // Arrange: token e dados de sessão semeados no beforeEach

    // Act
    component.onConfirmLogout();

    // Assert
    expect(logoutServiceSpy.logout).toHaveBeenCalled();
    expect(localStorage.length).toBe(0);
    expect(sessionStorage.length).toBe(0);
    expect(authService.isAuthenticated()).toBeFalsy();
  });

  it('deve redirecionar para /login substituindo a entrada no histórico', () => {
    // Arrange: estado semeado no beforeEach

    // Act
    component.onConfirmLogout();

    // Assert
    expect(router.navigate).toHaveBeenCalledWith(['/login'], { replaceUrl: true });
  });

  it('deve encerrar a sessão mesmo quando a chamada de logout falha', async () => {
    // Arrange
    TestBed.resetTestingModule();
    logoutServiceSpy = { logout: vi.fn(() => throwError(() => new Error('falha de rede'))) };
    await configurar();

    // Act
    component.onConfirmLogout();

    // Assert
    expect(localStorage.length).toBe(0);
    expect(sessionStorage.length).toBe(0);
    expect(router.navigate).toHaveBeenCalledWith(['/login'], { replaceUrl: true });
  });
});
