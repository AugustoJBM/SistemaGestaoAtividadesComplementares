import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { AutenticacaoService } from './autenticacao.service';
import { AuthInterceptor } from './auth.interceptor';

describe('AuthInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authService: AutenticacaoService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([AuthInterceptor])),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AutenticacaoService);
    router = TestBed.inject(Router);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('deve anexar o header Authorization usando o tipo de token salvo', () => {

    authService.saveToken('token-123', 'Bearer');
    http.get('http://localhost:8080/atividades').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/atividades');
    expect(req.request.headers.get('Authorization')).toBe('Bearer token-123');
    req.flush({});
  });

  it('não deve anexar o header Authorization quando não houver token', () => {

    http.get('http://localhost:8080/atividades').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/atividades');
    expect(req.request.headers.has('Authorization')).toBeFalsy();
    req.flush({});
  });

  it('deve limpar o token e redirecionar para /login ao receber 401 em rota protegida', () => {

    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);
    authService.saveToken('token-expirado', 'Bearer');


    http.get('http://localhost:8080/atividades').subscribe({
      error: () => undefined
    });
    const req = httpMock.expectOne('http://localhost:8080/atividades');
    req.flush({ message: 'expirado' }, { status: 401, statusText: 'Unauthorized' });


    expect(authService.getToken()).toBeNull();
    expect(spyRouter).toHaveBeenCalledWith(['/login']);
  });

  it('não deve redirecionar ao receber 401 na própria rota de login', () => {

    const spyRouter = vi.spyOn(router, 'navigate').mockResolvedValue(true);


    http.post('http://localhost:8080/auth/login', {}).subscribe({
      error: () => undefined
    });
    const req = httpMock.expectOne('http://localhost:8080/auth/login');
    req.flush({ message: 'credenciais inválidas' }, { status: 401, statusText: 'Unauthorized' });


    expect(spyRouter).not.toHaveBeenCalled();
  });
});
