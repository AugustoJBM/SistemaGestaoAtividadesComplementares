import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { ProgressoService } from './progresso.service';
import { API_BASE_URL } from '../../api.config';

const PROGRESSO_URL = `${API_BASE_URL}/atividades/progresso`;

describe('ProgressoService', () => {
  let service: ProgressoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    // Isola este spec de vazamento de TestBed deixado por specs anteriores na
    // mesma execução (ver issue #62); sem isso a ordem de execução contamina.
    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      providers: [
        ProgressoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ProgressoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve chamar GET na URL correta derivada de API_BASE_URL', () => {
    service.obterProgresso().subscribe();

    const req = httpMock.expectOne(PROGRESSO_URL);
    expect(req.request.method).toBe('GET');
    req.flush({
      acc: { horasAcumuladas: 10, horasExigidas: 20, percentualConcluido: 50 },
      acex: { horasAcumuladas: 5, horasExigidas: 15, percentualConcluido: 33 }
    });
  });

  it('deve mapear o payload da API para o objeto de domínio e derivar horasRestantes', () => {
    let progresso: unknown;
    service.obterProgresso().subscribe((resultado) => (progresso = resultado));

    httpMock.expectOne(PROGRESSO_URL).flush({
      acc: { horasAcumuladas: 10, horasPendentes: 4, horasExigidas: 20, percentualConcluido: 50 },
      acex: { horasAcumuladas: 5, horasPendentes: 2, horasExigidas: 15, percentualConcluido: 33 }
    });

    expect(progresso).toEqual({
      acc: { horasAcumuladas: 10, horasPendentes: 4, horasExigidas: 20, horasRestantes: 10, percentualConcluido: 50 },
      acex: { horasAcumuladas: 5, horasPendentes: 2, horasExigidas: 15, horasRestantes: 10, percentualConcluido: 33 }
    });
  });

  it('deve retornar zeros para um estudante sem atividades', () => {
    let progresso: unknown;
    service.obterProgresso().subscribe((resultado) => (progresso = resultado));

    httpMock.expectOne(PROGRESSO_URL).flush({ acc: null, acex: null });

    expect(progresso).toEqual({
      acc: { horasAcumuladas: 0, horasPendentes: 0, horasExigidas: 0, horasRestantes: 0, percentualConcluido: 0 },
      acex: { horasAcumuladas: 0, horasPendentes: 0, horasExigidas: 0, horasRestantes: 0, percentualConcluido: 0 }
    });
  });

  it('deve traduzir o status 401 para mensagem de sessão expirada', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock.expectOne(PROGRESSO_URL).flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(mensagem).toBe('Sessão expirada. Faça login novamente.');
  });

  it('deve traduzir o status 0 para mensagem de falha de conexão', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock.expectOne(PROGRESSO_URL).error(new ProgressEvent('error'), { status: 0 });

    expect(mensagem).toContain('Não foi possível conectar ao servidor');
  });


  it('deve traduzir o status 403 usando a mensagem de texto puro do backend', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock
      .expectOne(PROGRESSO_URL)
      .flush('Apenas estudantes podem consultar o progresso de atividades.', {
        status: 403,
        statusText: 'Forbidden'
      });

    expect(mensagem).toBe('Apenas estudantes podem consultar o progresso de atividades.');
  });

  it('deve usar mensagem própria no 403 quando o backend não informar motivo', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock.expectOne(PROGRESSO_URL).flush('', { status: 403, statusText: 'Forbidden' });

    expect(mensagem).toBe('Apenas estudantes podem consultar o progresso de atividades.');
  });

  it('deve aproveitar a mensagem de erro em texto puro do backend', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock
      .expectOne(PROGRESSO_URL)
      .flush('Falha ao consultar progresso.', { status: 500, statusText: 'Server Error' });

    expect(mensagem).toBe('Falha ao consultar progresso.');
  });

  it('não deve exibir corpo de erro em formato inesperado como mensagem', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock
      .expectOne(PROGRESSO_URL)
      .flush({ detalhe: { codigo: 42 } }, { status: 500, statusText: 'Server Error' });

    expect(mensagem).toBe('Não foi possível carregar seu progresso. Tente novamente.');
    expect(mensagem).not.toContain('object');
  });

  it('deve usar mensagem genérica quando o backend não informar detalhe', () => {
    let mensagem = '';

    service.obterProgresso().subscribe({
      error: (erro: Error) => (mensagem = erro.message)
    });

    httpMock.expectOne(PROGRESSO_URL).flush({}, { status: 500, statusText: 'Server Error' });

    expect(mensagem).toBe('Não foi possível carregar seu progresso. Tente novamente.');
  });
});
