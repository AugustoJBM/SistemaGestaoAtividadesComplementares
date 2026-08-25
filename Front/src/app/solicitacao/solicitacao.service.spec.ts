import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SolicitacaoService } from './solicitacao.service';
import { SolicitacaoDetalhe, SolicitacaoResumo } from './solicitacao.model';
import { API_BASE_URL } from '../api.config';

const detalheMock: SolicitacaoDetalhe = {
  id: 7,
  status: 'SUBMETIDA',
  dataSubmissao: '2026-08-20T10:30:00',
  totalAtividades: 2,
  itens: [
    { atividadeId: 1, titulo: 'Iniciacao Cientifica', cargaHoraria: 15, natureza: 'ACC' },
    { atividadeId: 2, titulo: 'Projeto de Extensao', cargaHoraria: 20, natureza: 'ACEX' }
  ]
};

describe('SolicitacaoService', () => {
  let service: SolicitacaoService;
  let httpMock: HttpTestingController;
  const url = `${API_BASE_URL}/solicitacoes`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SolicitacaoService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(SolicitacaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('submete o relatorio para validacao via POST', () => {
    let recebido: SolicitacaoDetalhe | undefined;
    service.submeter().subscribe((detalhe) => (recebido = detalhe));

    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('POST');
    req.flush(detalheMock);

    expect(recebido).toEqual(detalheMock);
  });

  it('traduz 409 para mensagem de solicitacao ja em aberto', () => {
    let erro: Error | undefined;
    service.submeter().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 409, statusText: 'Conflict' });

    expect(erro?.message).toBe('Você já possui uma solicitação em aberto. Acompanhe o andamento antes de enviar outra.');
  });

  it('traduz 422 para mensagem de relatorio sem atividades', () => {
    let erro: Error | undefined;
    service.submeter().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 422, statusText: 'Unprocessable Entity' });

    expect(erro?.message).toBe('Cadastre ao menos uma atividade antes de enviar o relatório para validação.');
  });

  it('traduz 400 com a mesma mensagem de relatorio sem atividades', () => {
    let erro: Error | undefined;
    service.submeter().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 400, statusText: 'Bad Request' });

    expect(erro?.message).toBe('Cadastre ao menos uma atividade antes de enviar o relatório para validação.');
  });

  it('prioriza a mensagem enviada pelo backend na submissao', () => {
    let erro: Error | undefined;
    service.submeter().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush({ message: 'Regulamento vigente ausente.' }, { status: 409, statusText: 'Conflict' });

    expect(erro?.message).toBe('Regulamento vigente ausente.');
  });

  it('lista as solicitacoes do estudante', () => {
    const esperado: SolicitacaoResumo[] = [
      { id: 7, status: 'SUBMETIDA', dataSubmissao: '2026-08-20T10:30:00', totalAtividades: 2 }
    ];

    let recebido: SolicitacaoResumo[] | undefined;
    service.listar().subscribe((lista) => (recebido = lista));

    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush(esperado);

    expect(recebido).toEqual(esperado);
  });

  it('devolve lista vazia quando o backend responde null', () => {
    let recebido: SolicitacaoResumo[] | undefined;
    service.listar().subscribe((lista) => (recebido = lista));

    httpMock.expectOne(url).flush(null);

    expect(recebido).toEqual([]);
  });

  it('detalha uma solicitacao pelo id', () => {
    let recebido: SolicitacaoDetalhe | undefined;
    service.detalhar(7).subscribe((detalhe) => (recebido = detalhe));

    const req = httpMock.expectOne(`${url}/7`);
    expect(req.request.method).toBe('GET');
    req.flush(detalheMock);

    expect(recebido).toEqual(detalheMock);
  });

  it('traduz 404 do detalhe para mensagem de solicitacao inexistente', () => {
    let erro: Error | undefined;
    service.detalhar(99).subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(`${url}/99`).flush(null, { status: 404, statusText: 'Not Found' });

    expect(erro?.message).toBe('Solicitação não encontrada.');
  });

  it('traduz 401 para mensagem de sessao expirada', () => {
    let erro: Error | undefined;
    service.listar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
  });

  it('traduz falha de conexao', () => {
    let erro: Error | undefined;
    service.listar().subscribe({ error: (e: Error) => (erro = e) });

    httpMock
      .expectOne(url)
      .error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
  });
});
