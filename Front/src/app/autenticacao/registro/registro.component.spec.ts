import '@angular/compiler';
import { EnvironmentInjector, Injector, runInInjectionContext } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi, type Mock } from 'vitest';
import { RegistroComponent } from './registro.component';
import { RegistroService } from './registro.service';

describe('RegistroComponent', () => {
  let component: RegistroComponent;
  let registroServiceSpy: { register: Mock };
  let routerSpy: { navigate: Mock };
  let testInjector: EnvironmentInjector;

  beforeEach(() => {
    registroServiceSpy = { register: vi.fn() };
    routerSpy = { navigate: vi.fn().mockResolvedValue(true) };

    testInjector = Injector.create({
      providers: [
        FormBuilder,
        { provide: RegistroService, useValue: registroServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }) as EnvironmentInjector;

    component = runInInjectionContext(testInjector, () => new RegistroComponent());
  });

  it('deve ser criado com formulario invalido inicialmente', () => {
    expect(component).toBeTruthy();
    expect(component.registerForm.valid).toBeFalsy();
  });

  it('nao deve submeter o formulario quando estiver invalido', () => {
    component.onSubmit();

    expect(component.registerForm.touched).toBeTruthy();
    expect(registroServiceSpy.register).not.toHaveBeenCalled();
    expect(component.isLoading()).toBeFalsy();
  });

  it('deve chamar o service e redirecionar para /login em caso de submissao valida', () => {
    registroServiceSpy.register.mockReturnValue(of(void 0));

    component.registerForm.setValue({
      fullName: 'Nome Teste',
      emailOrRegistration: 'teste@email.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(registroServiceSpy.register).toHaveBeenCalledWith({
      fullName: 'Nome Teste',
      emailOrRegistration: 'teste@email.com',
      password: 'password123'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('deve exibir mensagem de erro quando o registro falhar com texto customizado', () => {
    const errorResponse = { error: 'E-mail ja cadastrado' };
    registroServiceSpy.register.mockReturnValue(throwError(() => errorResponse));

    component.registerForm.setValue({
      fullName: 'Nome Teste',
      emailOrRegistration: 'teste@email.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toBe('E-mail ja cadastrado');
  });

  it('deve exibir mensagem de erro padrao quando o registro falhar sem corpo de texto', () => {
    registroServiceSpy.register.mockReturnValue(throwError(() => ({})));

    component.registerForm.setValue({
      fullName: 'Nome Teste',
      emailOrRegistration: 'teste@email.com',
      password: 'password123',
      confirmPassword: 'password123'
    });

    component.onSubmit();

    expect(component.isLoading()).toBeFalsy();
    expect(component.errorMessage()).toBe('Erro ao cadastrar. Tente novamente.');
  });

  it('deve alternar a visibilidade das senhas', () => {
    expect(component.showPassword()).toBeFalsy();
    component.togglePasswordVisibility('password');
    expect(component.showPassword()).toBeTruthy();

    expect(component.showConfirmPassword()).toBeFalsy();
    component.togglePasswordVisibility('confirmPassword');
    expect(component.showConfirmPassword()).toBeTruthy();
  });
});
