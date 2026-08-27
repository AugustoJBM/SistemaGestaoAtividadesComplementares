import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { RegistroService } from './registro.service';
import { RegistroRequest } from './registro.model';
import { API_BASE_URL } from '../../api.config';

const CADASTRO_URL = `${API_BASE_URL}/auth/cadastro`;

describe('RegistroService', () => {
  let service: RegistroService;
  let httpMock: HttpTestingController;

  const dadosRegistro: RegistroRequest = {
    fullName: 'Estudante Teste',
    emailOrRegistration: 'estudante@ufape.edu.br',
    password: 'senhaSegura123',
  };

  beforeEach(() => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [RegistroService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(RegistroService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado com sucesso', () => {
    expect(service).toBeTruthy();
  });

  it('deve enviar requisição POST com payload mapeado para o contrato do backend', () => {
    service.register(dadosRegistro).subscribe();

    const req = httpMock.expectOne(CADASTRO_URL);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      nome: 'Estudante Teste',
      email: 'estudante@ufape.edu.br',
      senha: 'senhaSegura123',
      role: 'ESTUDANTE',
    });
    req.flush({
      id: 1,
      nome: 'Estudante Teste',
      email: 'estudante@ufape.edu.br',
      role: 'ESTUDANTE',
    });
  });

  it('deve traduzir erro 409 com mensagem em texto puro do backend para Error de domínio', () => {
    let erro: Error | undefined;
    service.register(dadosRegistro).subscribe({
      error: (falha: Error) => (erro = falha),
    });

    const req = httpMock.expectOne(CADASTRO_URL);
    req.flush('Já existe um usuário cadastrado com o email: estudante@ufape.edu.br', {
      status: 409,
      statusText: 'Conflict',
    });

    expect(erro).toBeInstanceOf(Error);
    expect(erro?.message).toBe(
      'Já existe um usuário cadastrado com o email: estudante@ufape.edu.br',
    );
  });

  it('deve traduzir erro de rede (status 0) para mensagem de conexão', () => {
    let erro: Error | undefined;
    service.register(dadosRegistro).subscribe({
      error: (falha: Error) => (erro = falha),
    });

    const req = httpMock.expectOne(CADASTRO_URL);
    req.error(new ProgressEvent('error'), { status: 0 });

    expect(erro).toBeInstanceOf(Error);
    expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
  });

  it('deve usar mensagem genérica quando o backend retornar 500 sem corpo', () => {
    let erro: Error | undefined;
    service.register(dadosRegistro).subscribe({
      error: (falha: Error) => (erro = falha),
    });

    const req = httpMock.expectOne(CADASTRO_URL);
    req.flush('', { status: 500, statusText: 'Internal Server Error' });

    expect(erro).toBeInstanceOf(Error);
    expect(erro?.message).toBe('Erro ao cadastrar. Tente novamente.');
  });
});
