import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AvaliacaoService } from './avaliacao.service';
import { SolicitacaoAvaliadorDetalhe, SolicitacaoAvaliadorResumo } from './avaliacao.model';
import { API_BASE_URL } from '../api.config';

const resumoMock: SolicitacaoAvaliadorResumo[] = [
  {
    id: 7,
    estudanteNome: 'Ana Souza',
    dataSubmissao: '2026-08-20T10:30:00',
    status: 'SUBMETIDA',
    totalAtividades: 2,
    cargaHorariaTotal: 35,
  },
];

const detalheMock: SolicitacaoAvaliadorDetalhe = {
  id: 7,
  estudanteNome: 'Ana Souza',
  estudanteEmail: 'ana.souza@ufape.edu.br',
  dataSubmissao: '2026-08-20T10:30:00',
  status: 'SUBMETIDA',
  cargaHorariaTotal: 35,
  itens: [
    { atividadeId: 1, titulo: 'Iniciacao Cientifica', cargaHoraria: 15, natureza: 'ACC' },
    { atividadeId: 2, titulo: 'Projeto de Extensao', cargaHoraria: 20, natureza: 'ACEX' },
  ],
};

describe('AvaliacaoService', () => {
  let service: AvaliacaoService;
  let httpMock: HttpTestingController;
  const url = `${API_BASE_URL}/solicitacoes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AvaliacaoService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AvaliacaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('consulta as solicitacoes sem filtro de status', () => {
    let recebido: SolicitacaoAvaliadorResumo[] | undefined;
    service.consultar().subscribe((lista) => (recebido = lista));

    const req = httpMock.expectOne(`${url}/avaliacao`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.has('status')).toBe(false);
    req.flush(resumoMock);

    expect(recebido).toEqual(resumoMock);
  });

  it('consulta as solicitacoes com filtro de status', () => {
    let recebido: SolicitacaoAvaliadorResumo[] | undefined;
    service.consultar('REJEITADA').subscribe((lista) => (recebido = lista));

    const req = httpMock.expectOne(
      (requisicao) => requisicao.url === `${url}/avaliacao` && requisicao.method === 'GET',
    );
    expect(req.request.params.get('status')).toBe('REJEITADA');
    req.flush(resumoMock);

    expect(recebido).toEqual(resumoMock);
  });

  it('detalha uma solicitacao pelo id', () => {
    let recebido: SolicitacaoAvaliadorDetalhe | undefined;
    service.detalhar(7).subscribe((detalhe) => (recebido = detalhe));

    const req = httpMock.expectOne(`${url}/7/avaliacao`);
    expect(req.request.method).toBe('GET');
    req.flush(detalheMock);

    expect(recebido).toEqual(detalheMock);
  });

  it('deve enviar decisao de avaliacao via PATCH', () => {
    let recebido: SolicitacaoAvaliadorDetalhe | undefined;
    service.avaliar(7, 'APROVADA').subscribe((res) => (recebido = res));

    const req = httpMock.expectOne(`${url}/7/avaliacao`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ decisao: 'APROVADA', justificativa: undefined });
    req.flush({ ...detalheMock, status: 'APROVADA' });

    expect(recebido?.status).toBe('APROVADA');
  });

  it('traduz 409 para mensagem de conflito de avaliacao', () => {
    let erro: Error | undefined;
    service.avaliar(7, 'APROVADA').subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/7/avaliacao`).flush(null, { status: 409, statusText: 'Conflict' });
    expect(erro?.message).toBe(
      'Esta solicitação já foi avaliada ou seu status foi alterado por outro usuário.',
    );
  });

  it('traduz 403 para mensagem de permissao', () => {
    let erro: Error | undefined;
    service.consultar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/avaliacao`).flush(null, { status: 403, statusText: 'Forbidden' });
    expect(erro?.message).toBe('Apenas avaliadores podem consultar as solicitações de validação.');
  });
});
