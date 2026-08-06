import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { LoginComponent } from './login.component';
import { AutenticacaoService } from '../autenticacao.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AutenticacaoService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AutenticacaoService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('deve criar o componente com sucesso', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário de login com campos vazios e inválidos', () => {
    expect(component.loginForm.get('login')?.value).toBe('');
    expect(component.loginForm.get('password')?.value).toBe('');
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('deve alternar a visibilidade da senha', () => {
    expect(component.showPassword()).toBeFalsy();
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBeTruthy();
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBeFalsy();
  });

  it('deve marcar o formulário como tocado se submeter com dados inválidos', () => {
    vi.spyOn(authService, 'login');
    component.onSubmit();
    expect(component.loginForm.touched).toBeTruthy();
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('deve realizar o login com sucesso e navegar para o dashboard', () => {
    const spyAuth = vi.spyOn(authService, 'login').mockReturnValue(of({ token: 'fake-token' }));
    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.loginForm.setValue({
      login: '2023000123',
      password: 'password123',
      rememberMe: false
    });

    component.onSubmit();

    expect(spyAuth).toHaveBeenCalledWith({
      usuario: '2023000123',
      senha: 'password123'
    });
    expect(component.isLoading()).toBeFalsy();
    expect(spyRouter).toHaveBeenCalledWith(['/dashboard']);
  });

  it('deve definir mensagem de erro amigável quando receber status 401 (Credenciais inválidas)', () => {
    const errorResponse = { status: 401 };
    vi.spyOn(authService, 'login').mockReturnValue(throwError(() => errorResponse));

    component.loginForm.setValue({
      login: 'errado@ufape.edu.br',
      password: 'wrongpassword',
      rememberMe: false
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toContain('Credenciais inválidas');
  });

  it('deve definir mensagem de erro de conexão quando receber status 0', () => {
    const errorResponse = { status: 0 };
    vi.spyOn(authService, 'login').mockReturnValue(throwError(() => errorResponse));

    component.loginForm.setValue({
      login: 'teste@ufape.edu.br',
      password: 'password123',
      rememberMe: false
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toContain('Não foi possível conectar ao servidor');
  });
});
