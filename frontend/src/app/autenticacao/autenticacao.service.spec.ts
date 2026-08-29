import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { AutenticacaoService } from './autenticacao.service';
import { API_BASE_URL } from '../api.config';

const LOGIN_URL = `${API_BASE_URL}/auth/login`;

const createStorageMock = () => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] ?? null,
    setItem: (key: string, value: string) => {
      store[key] = String(value);
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
    get length() {
      return Object.keys(store).length;
    },
  };
};

describe('AutenticacaoService', () => {
  let service: AutenticacaoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestingModule();
    vi.stubGlobal('localStorage', createStorageMock());
    vi.stubGlobal('sessionStorage', createStorageMock());

    TestBed.configureTestingModule({
      providers: [AutenticacaoService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AutenticacaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
    sessionStorage.clear();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve converter as credenciais de domínio para o contrato do backend', () => {
    const credenciais = { email: 'aluno@ufape.edu.br', senha: 'password123' };

    service.login(credenciais).subscribe();

    const req = httpMock.expectOne(LOGIN_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ usuario: 'aluno@ufape.edu.br', senha: 'password123' });
    req.flush({ token: 'fake-jwt-token', tipo: 'Bearer' });
  });

  it('deve salvar o token e o tipo devolvidos pelo backend', () => {
    const credenciais = { email: 'aluno@ufape.edu.br', senha: 'password123' };
    service.login(credenciais).subscribe();
    httpMock.expectOne(LOGIN_URL).flush({ token: 'fake-jwt-token', tipo: 'Bearer' });
    expect(service.getToken()).toBe('fake-jwt-token');
    expect(service.getTokenType()).toBe('Bearer');
    expect(service.isAuthenticated()).toBeTruthy();
  });

  it('não deve deixar resíduo de autenticação no navegador ao encerrar a sessão', () => {
    service.saveToken('token-valido', 'Bearer');
    sessionStorage.setItem('user-data', JSON.stringify({ nome: 'Teste' }));

    service.encerrarSessao();

    expect(localStorage.length).toBe(0);
    expect(sessionStorage.length).toBe(0);
    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBeFalsy();
  });

  it('deve traduzir o status 401 para mensagem de credenciais inválidas', () => {
    let mensagem = '';
    service.login({ email: 'errado@ufape.edu.br', senha: 'wrongpassword' }).subscribe({
      error: (erro: Error) => (mensagem = erro.message),
    });
    httpMock.expectOne(LOGIN_URL).flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(mensagem).toBe('Credenciais inválidas.');
  });

  it('deve traduzir o status 0 para mensagem de falha de conexão', () => {
    let mensagem = '';
    service.login({ email: 'aluno@ufape.edu.br', senha: 'password123' }).subscribe({
      error: (erro: Error) => (mensagem = erro.message),
    });
    httpMock.expectOne(LOGIN_URL).error(new ProgressEvent('error'), { status: 0 });
    expect(mensagem).toContain('Não foi possível conectar ao servidor');
  });

  it('deve usar mensagem genérica quando o backend não informar detalhe', () => {
    let mensagem = '';
    service.login({ email: 'aluno@ufape.edu.br', senha: 'password123' }).subscribe({
      error: (erro: Error) => (mensagem = erro.message),
    });
    httpMock.expectOne(LOGIN_URL).flush({}, { status: 500, statusText: 'Server Error' });
    expect(mensagem).toBe('Ocorreu um erro ao realizar o login. Tente novamente.');
  });
});
