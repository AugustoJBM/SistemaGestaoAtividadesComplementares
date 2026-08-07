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
    const spyAuth = vi.spyOn(authService, 'login').mockReturnValue(of({ token: 'fake-token', tipo: 'Bearer' }));
    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.loginForm.setValue({
      login: 'aluno@ufape.edu.br',
      password: 'password123',
      rememberMe: false
    });

    component.onSubmit();

    expect(spyAuth).toHaveBeenCalledWith({
      email: 'aluno@ufape.edu.br',
      senha: 'password123'
    });
    expect(component.isLoading()).toBeFalsy();
    expect(spyRouter).toHaveBeenCalledWith(['/dashboard']);
  });

  it('deve exibir a mensagem de erro fornecida pelo service', () => {
    vi.spyOn(authService, 'login').mockReturnValue(
      throwError(() => new Error('Credenciais inválidas.'))
    );
    component.loginForm.setValue({
      login: 'errado@ufape.edu.br',
      password: 'wrongpassword',
      rememberMe: false
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toBe('Credenciais inválidas.');
  });

});
