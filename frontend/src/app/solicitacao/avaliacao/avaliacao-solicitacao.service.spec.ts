import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AvaliacaoSolicitacaoService } from './avaliacao-solicitacao.service';
import { SolicitacaoAvaliadorDetalhe, SolicitacaoAvaliadorResumo } from './avaliacao.model';
import { API_BASE_URL } from '../../api.config';

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

describe('AvaliacaoSolicitacaoService', () => {
  let service: AvaliacaoSolicitacaoService;
  let httpMock: HttpTestingController;
  const url = `${API_BASE_URL}/solicitacoes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AvaliacaoSolicitacaoService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AvaliacaoSolicitacaoService);
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

  it('devolve lista vazia quando o backend responde null', () => {
    let recebido: SolicitacaoAvaliadorResumo[] | undefined;
    service.consultar().subscribe((lista) => (recebido = lista));

    httpMock.expectOne(`${url}/avaliacao`).flush(null);

    expect(recebido).toEqual([]);
  });

  it('detalha uma solicitacao pelo id', () => {
    let recebido: SolicitacaoAvaliadorDetalhe | undefined;
    service.detalhar(7).subscribe((detalhe) => (recebido = detalhe));

    const req = httpMock.expectOne(`${url}/7/avaliacao`);
    expect(req.request.method).toBe('GET');
    req.flush(detalheMock);

    expect(recebido).toEqual(detalheMock);
  });

  it('traduz 404 do detalhe para mensagem de solicitacao inexistente', () => {
    let erro: Error | undefined;
    service.detalhar(99).subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/99/avaliacao`).flush(null, { status: 404, statusText: 'Not Found' });

    expect(erro?.message).toBe('Solicitação não encontrada.');
  });

  it('traduz 401 para mensagem de sessao expirada', () => {
    let erro: Error | undefined;
    service.consultar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/avaliacao`).flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
  });

  it('traduz 403 para mensagem de permissao', () => {
    let erro: Error | undefined;
    service.consultar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/avaliacao`).flush(null, { status: 403, statusText: 'Forbidden' });

    expect(erro?.message).toBe('Apenas avaliadores podem consultar as solicitações de validação.');
  });

  it('traduz falha de conexao', () => {
    let erro: Error | undefined;
    service.consultar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock
      .expectOne(`${url}/avaliacao`)
      .error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
  });

  it('prioriza a mensagem enviada pelo backend na consulta', () => {
    let erro: Error | undefined;
    service.consultar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock
      .expectOne(`${url}/avaliacao`)
      .flush({ message: 'Status inválido.' }, { status: 400, statusText: 'Bad Request' });

    expect(erro?.message).toBe('Status inválido.');
  });
});
