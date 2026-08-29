import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { NotificacaoService } from './notificacao.service';
import { ContagemNaoLidas, Notificacao } from './notificacao.model';
import { API_BASE_URL } from '../api.config';

const notificacoesMock: Notificacao[] = [
  {
    id: 1,
    tipo: 'SOLICITACAO_APROVADA',
    titulo: 'Solicitação Aprovada',
    mensagem: 'Sua solicitação #7 foi aprovada.',
    solicitacaoId: 7,
    lida: false,
    dataCriacao: '2026-08-28T10:00:00',
  },
  {
    id: 2,
    tipo: 'SOLICITACAO_COM_PENDENCIAS',
    titulo: 'Solicitação com Pendências',
    mensagem: 'Ajuste os certificados pendentes.',
    solicitacaoId: 8,
    lida: true,
    dataCriacao: '2026-08-27T09:00:00',
  },
];

describe('NotificacaoService', () => {
  let service: NotificacaoService;
  let httpMock: HttpTestingController;
  const url = `${API_BASE_URL}/notificacoes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NotificacaoService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(NotificacaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve listar todas as notificações sem filtro', () => {
    let recebido: Notificacao[] | undefined;
    service.listar().subscribe((res) => (recebido = res));

    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.has('apenasNaoLidas')).toBe(false);
    req.flush(notificacoesMock);
    expect(recebido).toEqual(notificacoesMock);
  });

  it('deve listar notificações com o parâmetro apenasNaoLidas', () => {
    let recebido: Notificacao[] | undefined;
    service.listar(true).subscribe((res) => (recebido = res));

    const req = httpMock.expectOne(`${url}?apenasNaoLidas=true`);
    expect(req.request.method).toBe('GET');
    req.flush([notificacoesMock[0]]);
    expect(recebido).toEqual([notificacoesMock[0]]);
  });

  it('deve contar notificações não lidas', () => {
    const contagemMock: ContagemNaoLidas = { naoLidas: 3 };
    let recebido: ContagemNaoLidas | undefined;
    service.contarNaoLidas().subscribe((res) => (recebido = res));

    const req = httpMock.expectOne(`${url}/contagem-nao-lidas`);
    expect(req.request.method).toBe('GET');
    req.flush(contagemMock);
    expect(recebido).toEqual(contagemMock);
  });

  it('deve marcar notificação individual como lida via PATCH', () => {
    const atualizada: Notificacao = { ...notificacoesMock[0], lida: true };
    let recebido: Notificacao | undefined;
    service.marcarComoLida(1).subscribe((res) => (recebido = res));

    const req = httpMock.expectOne(`${url}/1/leitura`);
    expect(req.request.method).toBe('PATCH');
    req.flush(atualizada);
    expect(recebido?.lida).toBe(true);
  });

  it('deve marcar todas as notificações como lidas via PATCH', () => {
    let finalizou = false;
    service.marcarTodasComoLidas().subscribe({
      complete: () => (finalizou = true),
    });

    const req = httpMock.expectOne(`${url}/leitura`);
    expect(req.request.method).toBe('PATCH');
    req.flush(null, { status: 204, statusText: 'No Content' });
    expect(finalizou).toBe(true);
  });

  it('deve traduzir 401 para erro de sessão expirada', () => {
    let erro: Error | undefined;
    service.listar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 401, statusText: 'Unauthorized' });
    expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
  });
});
