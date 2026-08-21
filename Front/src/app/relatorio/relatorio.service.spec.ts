import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RelatorioService } from './relatorio.service';
import { RelatorioAtividades } from './relatorio.model';
import { API_BASE_URL } from '../api.config';

describe('RelatorioService', () => {
  let service: RelatorioService;
  let httpMock: HttpTestingController;
  const url = `${API_BASE_URL}/relatorios/atividades`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RelatorioService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(RelatorioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('busca o relatorio do estudante autenticado', () => {
    const esperado: RelatorioAtividades = {
      estudanteEmail: 'estudante@ufape.edu.br',
      naturezas: [
        {
          natureza: 'ACC',
          totalHoras: 15,
          categorias: [
            {
              categoria: 'PESQUISA',
              totalHoras: 15,
              atividades: [
                {
                  id: 1,
                  titulo: 'Iniciacao Cientifica',
                  instituicaoResponsavel: 'UFAPE',
                  dataRealizacao: '2026-03-10',
                  cargaHorariaEmHoras: 15
                }
              ]
            }
          ]
        }
      ],
      totalHorasAcc: 15,
      totalHorasAcex: 0,
      totalHorasGeral: 15
    };

    let recebido: RelatorioAtividades | undefined;
    service.obterRelatorio().subscribe((relatorio) => (recebido = relatorio));

    const req = httpMock.expectOne(url);
    expect(req.request.method).toBe('GET');
    req.flush(esperado);

    expect(recebido).toEqual(esperado);
  });

  it('traduz 401 para mensagem de sessao expirada', () => {
    let erro: Error | undefined;
    service.obterRelatorio().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
  });

  it('traduz 403 para mensagem de perfil sem permissao', () => {
    let erro: Error | undefined;
    service.obterRelatorio().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).flush(null, { status: 403, statusText: 'Forbidden' });

    expect(erro?.message).toBe('Apenas estudantes podem emitir o relatório de atividades.');
  });

  it('traduz falha de conexao', () => {
    let erro: Error | undefined;
    service.obterRelatorio().subscribe({ error: (e: Error) => (erro = e) });

    httpMock.expectOne(url).error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });

    expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
  });
});
