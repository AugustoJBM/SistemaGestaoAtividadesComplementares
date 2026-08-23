import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { LoginComponent } from './login.component';
import { AutenticacaoService } from '../autenticacao.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let authServiceSpy: {
    login: ReturnType<typeof vi.fn>;
    getRole: ReturnType<typeof vi.fn>;
    getToken: ReturnType<typeof vi.fn>;
    estaAutenticado: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    authServiceSpy = {
      login: vi.fn(),
      getRole: vi.fn().mockReturnValue('ESTUDANTE'),
      getToken: vi.fn().mockReturnValue('token-fake'),
      estaAutenticado: vi.fn().mockReturnValue(true)
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        { provide: AutenticacaoService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('deve criar o componente com sucesso', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário de login com campos vazios e inválidos', () => {
    expect(component.loginForm.valid).toBeFalsy();
    expect(component.loginControl?.value).toBe('');
    expect(component.passwordControl?.value).toBe('');
  });

  it('deve alternar a visibilidade da senha', () => {
    expect(component.showPassword()).toBe(false);
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBe(true);
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBe(false);
  });

  it('deve marcar o formulário como tocado se submeter com dados inválidos', () => {
    component.onSubmit();
    expect(component.loginForm.touched).toBeTruthy();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve realizar o login com sucesso e navegar para o dashboard quando for ESTUDANTE', () => {
    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    authServiceSpy.login.mockReturnValue(of({ token: 'fake-token', tipo: 'Bearer' }));
    authServiceSpy.getRole.mockReturnValue('ESTUDANTE');

    component.loginForm.setValue({
      login: 'aluno@ufape.edu.br',
      password: 'senha1234',
      rememberMe: false
    });

    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      email: 'aluno@ufape.edu.br',
      senha: 'senha1234'
    });
    expect(component.isLoading()).toBeFalsy();
    expect(spyRouter).toHaveBeenCalledWith(['/dashboard']);
  });

  it('deve realizar login e navegar para /regulamentos/gestao quando for AVALIADOR ou ADMINISTRADOR', () => {
    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    authServiceSpy.login.mockReturnValue(of({ token: 'fake-token', tipo: 'Bearer' }));
    authServiceSpy.getRole.mockReturnValue('AVALIADOR');

    component.loginForm.setValue({
      login: 'avaliador@ufape.edu.br',
      password: 'senha1234',
      rememberMe: false
    });

    component.onSubmit();

    expect(spyRouter).toHaveBeenCalledWith(['/regulamentos/gestao']);
  });

  it('deve exibir a mensagem de erro fornecida pelo service', () => {
    authServiceSpy.login.mockReturnValue(throwError(() => new Error('Credenciais inválidas.')));

    component.loginForm.setValue({
      login: 'usuario@ufape.edu.br',
      password: 'senhaIncorreta',
      rememberMe: false
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toBe('Credenciais inválidas.');
  });
});