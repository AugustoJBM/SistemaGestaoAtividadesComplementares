import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { vi, type Mock } from 'vitest';

import { AutenticacaoService } from '../autenticacao.service';
import { LogoutService } from './logout.service';
import { LogoutComponent } from './logout.component';

describe('Componente de Logout', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;
  let authServiceSpy: { clearToken: Mock };
  let logoutServiceSpy: { logout: Mock };
  let router: Router;

  beforeEach(async () => {
    authServiceSpy = {
      clearToken: vi.fn(() => localStorage.removeItem('sgac_auth_token'))
    };
    logoutServiceSpy = {
      logout: vi.fn(() => of(void 0))
    };

    await TestBed.configureTestingModule({
      imports: [LogoutComponent, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: AutenticacaoService, useValue: authServiceSpy },
        { provide: LogoutService, useValue: logoutServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);

    localStorage.setItem('sgac_auth_token', 'dummy-jwt-token');
    sessionStorage.setItem('user-data', JSON.stringify({ name: 'Test User' }));
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
    sessionStorage.clear();
  });

  it('deve remover o token JWT e limpar o sessionStorage quando o logout for confirmado', () => {
    component.onConfirmLogout();

    expect(logoutServiceSpy.logout).toHaveBeenCalled();
    expect(authServiceSpy.clearToken).toHaveBeenCalled();
    expect(localStorage.getItem('sgac_auth_token')).toBeNull();
    expect(sessionStorage.length).toBe(0);
  });

  it('deve redirecionar para /login após o fluxo de logout ser concluído', () => {
    component.onConfirmLogout();

    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});