import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

import { AutenticacaoService } from './autenticacao.service';

describe('AutenticacaoService', () => {
  let service: AutenticacaoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AutenticacaoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AutenticacaoService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve realizar o login com sucesso e salvar o token', () => {
    const mockCredentials = { usuario: '2023000123', senha: 'password123' };
    const mockResponse = { token: 'fake-jwt-token' };

    service.login(mockCredentials).subscribe((response) => {
      expect(response).toEqual(mockResponse);
      expect(service.getToken()).toBe('fake-jwt-token');
      expect(service.isAuthenticated()).toBeTruthy();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockCredentials);
    req.flush(mockResponse);
  });

  it('deve verificar o estado de autenticação corretamente', () => {
    expect(service.isAuthenticated()).toBeFalsy();

    // Utilizando o método de login simulado ou definindo o token pelo fluxo do serviço
    const mockCredentials = { usuario: '2023000123', senha: 'password123' };
    const mockResponse = { token: 'token-valido' };

    service.login(mockCredentials).subscribe(() => {
      expect(service.isAuthenticated()).toBeTruthy();
      service.logout();
      expect(service.isAuthenticated()).toBeFalsy();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/v1/auth/login');
    req.flush(mockResponse);
  });
});
