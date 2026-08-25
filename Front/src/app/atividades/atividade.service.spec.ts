import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AtividadeService } from './atividade.service';
import { Atividade, AtividadeRequest, Categoria, Natureza } from './atividade.model';
import { AtividadeEdicaoRequest } from './edicao/edicao-atividade.model';
import { API_BASE_URL } from '../api.config';

const ATIVIDADES_URL = `${API_BASE_URL}/atividades`;

describe('AtividadeService', () => {
  let service: AtividadeService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [
        AtividadeService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AtividadeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('cadastrar', () => {
    const arquivoMock = new File(['conteudo'], 'certificado.pdf', { type: 'application/pdf' });
    const requestMock: AtividadeRequest = {
      titulo: 'Curso de Angular',
      instituicaoResponsavel: 'UFAPE',
      dataRealizacao: '2026-05-10',
      cargaHoraria: 20,
      natureza: Natureza.ACC,
      categoria: Categoria.ENSINO,
      arquivo: arquivoMock
    };

    it('deve enviar POST com FormData para a URL de atividades', () => {
      service.cadastrar(requestMock).subscribe();
      const req = httpMock.expectOne(ATIVIDADES_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeInstanceOf(FormData);
      req.flush({
        id: 1,
        titulo: 'Curso de Angular',
        instituicaoResponsavel: 'UFAPE',
        dataRealizacao: '2026-05-10',
        cargaHorariaEmHoras: 20,
        natureza: 'ACC',
        categoria: 'ENSINO'
      });
    });

    it('deve traduzir erro 400 com mensagem em texto puro do backend para Error de domínio', () => {
      let erro: Error | undefined;
      service.cadastrar(requestMock).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(ATIVIDADES_URL);
      req.flush('Certificado inválido. Aceitos: PDF, PNG ou JPEG', {
        status: 400,
        statusText: 'Bad Request'
      });
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Certificado inválido. Aceitos: PDF, PNG ou JPEG');
    });

    it('deve traduzir erro 401 para mensagem de sessão expirada', () => {
      let erro: Error | undefined;
      service.cadastrar(requestMock).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(ATIVIDADES_URL);
      req.flush('', { status: 401, statusText: 'Unauthorized' });
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
    });

    it('deve traduzir erro de rede (status 0) para mensagem de conexão', () => {
      let erro: Error | undefined;
      service.cadastrar(requestMock).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(ATIVIDADES_URL);
      req.error(new ProgressEvent('error'), { status: 0 });
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
    });

    it('deve usar mensagem genérica quando o backend retornar 500 sem mensagem', () => {
      let erro: Error | undefined;
      service.cadastrar(requestMock).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(ATIVIDADES_URL);
      req.flush('', { status: 500, statusText: 'Internal Server Error' });
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Não foi possível cadastrar a atividade. Tente novamente.');
    });
  });

  describe('atualizar', () => {
    const edicaoSemArquivo: AtividadeEdicaoRequest = {
      titulo: 'Título Editado',
      instituicaoResponsavel: 'UFAPE',
      dataRealizacao: '2026-06-01',
      cargaHoraria: 40,
      natureza: Natureza.ACC,
      categoria: Categoria.PESQUISA,
      arquivo: null
    };

    it('deve enviar PUT com FormData para a URL de atividade com ID', () => {
      service.atualizar(10, edicaoSemArquivo).subscribe();
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/10`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toBeInstanceOf(FormData);
      req.flush({
        id: 10,
        titulo: 'Título Editado',
        instituicaoResponsavel: 'UFAPE',
        dataRealizacao: '2026-06-01',
        cargaHorariaEmHoras: 40,
        natureza: 'ACC',
        categoria: 'PESQUISA'
      });
    });

    it('deve incluir o arquivo no FormData quando fornecido na edição', () => {
      const arquivoNovo = new File(['novo'], 'novo_certificado.pdf', { type: 'application/pdf' });
      const edicaoComArquivo: AtividadeEdicaoRequest = {
        ...edicaoSemArquivo,
        arquivo: arquivoNovo
      };
      service.atualizar(10, edicaoComArquivo).subscribe();
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/10`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body.get('arquivo')).toBeTruthy();
      req.flush({ id: 10 });
    });

    it('deve traduzir erro 403 para mensagem de permissão negada', () => {
      let erro: Error | undefined;
      service.atualizar(10, edicaoSemArquivo).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/10`);
      req.flush('Você não tem permissão para editar esta atividade.', { status: 403, statusText: 'Forbidden' });
      expect(erro?.message).toBe('Você não tem permissão para editar esta atividade.');
    });

    it('deve traduzir erro 404 para mensagem de atividade não encontrada', () => {
      let erro: Error | undefined;
      service.atualizar(999, edicaoSemArquivo).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/999`);
      req.flush('', { status: 404, statusText: 'Not Found' });
      expect(erro?.message).toBe('Atividade não encontrada.');
    });
  });

  describe('buscarPorId', () => {
    it('deve retornar a atividade quando o ID existir na listagem', () => {
      let resultado: Atividade | undefined;
      service.buscarPorId(5).subscribe((atv) => (resultado = atv));
      const req = httpMock.expectOne(ATIVIDADES_URL);
      expect(req.request.method).toBe('GET');
      req.flush([
        {
          id: 5,
          titulo: 'Projeto de Extensão',
          instituicaoResponsavel: 'UFAPE',
          dataRealizacao: '2026-01-01',
          cargaHorariaEmHoras: 30,
          natureza: 'ACEX',
          categoria: 'EXTENSAO',
          dataCadastro: null,
          status: 'PENDENTE'
        }
      ]);
      expect(resultado).toEqual({
        id: 5,
        titulo: 'Projeto de Extensão',
        instituicaoResponsavel: 'UFAPE',
        dataRealizacao: '2026-01-01',
        cargaHorariaEmHoras: 30,
        natureza: 'ACEX',
        categoria: 'EXTENSAO',
        dataCadastro: null,
        status: 'PENDENTE'
      });
    });

    it('deve lançar erro quando o ID não for encontrado na listagem', () => {
      let erro: Error | undefined;
      service.buscarPorId(99).subscribe({
        error: (falha: Error) => (erro = falha)
      });
      const req = httpMock.expectOne(ATIVIDADES_URL);
      expect(req.request.method).toBe('GET');
      req.flush([]);
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Atividade não encontrada.');
    });
  });

  describe('obterCertificado', () => {
    it('deve solicitar o blob do certificado por id', () => {
      let blobRetornado: Blob | undefined;
      service.obterCertificado(5).subscribe((b) => (blobRetornado = b));
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/5/certificado`);
      expect(req.request.method).toBe('GET');
      expect(req.request.responseType).toBe('blob');
      const blobMock = new Blob(['dummy'], { type: 'application/pdf' });
      req.flush(blobMock);
      expect(blobRetornado).toBeTruthy();
    });
  });

  describe('listar', () => {
    it('deve chamar GET na URL derivada de API_BASE_URL, sem query params quando não há filtro', () => {
      service.listar().subscribe();
      const req = httpMock.expectOne(ATIVIDADES_URL);
      expect(req.request.method).toBe('GET');
      expect(req.request.params.keys()).toEqual([]);
      req.flush([]);
    });

    it('deve retornar array vazio quando o estudante não possui atividades', () => {
      let atividades: Atividade[] | undefined;
      service.listar().subscribe((resultado) => (atividades = resultado));
      httpMock.expectOne(ATIVIDADES_URL).flush([]);
      expect(atividades).toEqual([]);
    });

    it('deve mapear o payload da API para o modelo de domínio', () => {
      let atividades: Atividade[] | undefined;
      service.listar().subscribe((resultado) => (atividades = resultado));
      httpMock.expectOne(ATIVIDADES_URL).flush([
        {
          id: 7,
          titulo: 'Monitoria de Algoritmos',
          instituicaoResponsavel: 'UFAPE',
          dataRealizacao: '2026-03-10',
          cargaHorariaEmHoras: 30,
          natureza: 'ACC',
          categoria: 'ENSINO',
          dataCadastro: '2026-03-11T08:00:00',
          estudanteEmail: 'estudante@ufape.edu.br',
          status: 'PENDENTE'
        }
      ]);
      expect(atividades).toEqual([
        {
          id: 7,
          titulo: 'Monitoria de Algoritmos',
          instituicaoResponsavel: 'UFAPE',
          dataRealizacao: '2026-03-10',
          cargaHorariaEmHoras: 30,
          natureza: 'ACC',
          categoria: 'ENSINO',
          dataCadastro: '2026-03-11T08:00:00',
          status: 'PENDENTE'
        }
      ]);
    });

    it('deve montar a query string esperada quando os filtros são informados', () => {
      service.listar({ natureza: Natureza.ACEX, categoria: Categoria.EXTENSAO }).subscribe();
      const req = httpMock.expectOne(
        (candidato) =>
          candidato.url === ATIVIDADES_URL &&
          candidato.params.get('natureza') === 'ACEX' &&
          candidato.params.get('categoria') === 'EXTENSAO'
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('deve enviar apenas o filtro informado quando o outro é omitido', () => {
      service.listar({ natureza: Natureza.ACC }).subscribe();
      const req = httpMock.expectOne((candidato) => candidato.url === ATIVIDADES_URL);
      expect(req.request.params.get('natureza')).toBe('ACC');
      expect(req.request.params.has('categoria')).toBe(false);
      req.flush([]);
    });

    it('deve aplicar valores default seguros quando a resposta traz campos nulos', () => {
      let atividades: Atividade[] | undefined;
      service.listar().subscribe((resultado) => (atividades = resultado));
      httpMock.expectOne(ATIVIDADES_URL).flush([
        {
          id: 9,
          titulo: null,
          instituicaoResponsavel: null,
          dataRealizacao: null,
          cargaHorariaEmHoras: null,
          natureza: null,
          categoria: null,
          dataCadastro: null,
          status: null
        }
      ]);
      expect(atividades).toEqual([
        {
          id: 9,
          titulo: '',
          instituicaoResponsavel: '',
          dataRealizacao: '',
          cargaHorariaEmHoras: 0,
          natureza: '',
          categoria: '',
          dataCadastro: null,
          status: 'PENDENTE'
        }
      ]);
    });

    it('deve traduzir 401 em erro de sessão expirada', () => {
      let erro: Error | undefined;
      service.listar().subscribe({ error: (falha: Error) => (erro = falha) });
      httpMock.expectOne(ATIVIDADES_URL).flush('', { status: 401, statusText: 'Unauthorized' });
      expect(erro).toBeInstanceOf(Error);
      expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
    });

    it('deve traduzir falha de conexão em mensagem de rede', () => {
      let erro: Error | undefined;
      service.listar().subscribe({ error: (falha: Error) => (erro = falha) });
      httpMock.expectOne(ATIVIDADES_URL).error(new ProgressEvent('error'), { status: 0 });
      expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
    });

    it('deve usar a mensagem de texto puro devolvida pelo backend', () => {
      let erro: Error | undefined;
      service.listar().subscribe({ error: (falha: Error) => (erro = falha) });
      httpMock
        .expectOne(ATIVIDADES_URL)
        .flush('Estudante não encontrado', { status: 404, statusText: 'Not Found' });
      expect(erro?.message).toBe('Estudante não encontrado');
    });

    it('deve usar mensagem genérica quando o backend não informa detalhe', () => {
      let erro: Error | undefined;
      service.listar().subscribe({ error: (falha: Error) => (erro = falha) });
      httpMock.expectOne(ATIVIDADES_URL).flush('', { status: 500, statusText: 'Server Error' });
      expect(erro?.message).toBe('Não foi possível carregar suas atividades. Tente novamente.');
    });
  });

  describe('excluir', () => {
    it('envia DELETE para o id informado e completa sem corpo', () => {
      let completou = false;
      service.excluir(7).subscribe({ complete: () => (completou = true) });
      const req = httpMock.expectOne(`${ATIVIDADES_URL}/7`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
      expect(completou).toBe(true);
    });

    it('traduz 403 para mensagem de propriedade da atividade', () => {
      let erro: Error | undefined;
      service.excluir(7).subscribe({ error: (e: Error) => (erro = e) });
      httpMock.expectOne(`${ATIVIDADES_URL}/7`).flush(null, { status: 403, statusText: 'Forbidden' });
      expect(erro?.message).toBe('Você só pode excluir suas próprias atividades.');
    });

    it('traduz 401 para sessao expirada', () => {
      let erro: Error | undefined;
      service.excluir(7).subscribe({ error: (e: Error) => (erro = e) });
      httpMock.expectOne(`${ATIVIDADES_URL}/7`).flush(null, { status: 401, statusText: 'Unauthorized' });
      expect(erro?.message).toBe('Sessão expirada. Faça login novamente.');
    });

    it('traduz 404 para atividade nao encontrada', () => {
      let erro: Error | undefined;
      service.excluir(7).subscribe({ error: (e: Error) => (erro = e) });
      httpMock.expectOne(`${ATIVIDADES_URL}/7`).flush(null, { status: 404, statusText: 'Not Found' });
      expect(erro?.message).toBe('Atividade não encontrada.');
    });

    it('prioriza a mensagem enviada pelo backend no 403', () => {
      let erro: Error | undefined;
      service.excluir(7).subscribe({ error: (e: Error) => (erro = e) });
      httpMock
        .expectOne(`${ATIVIDADES_URL}/7`)
        .flush({ message: 'Atividade pertence a outro estudante' }, { status: 403, statusText: 'Forbidden' });
      expect(erro?.message).toBe('Atividade pertence a outro estudante');
    });

    it('traduz falha de conexao', () => {
      let erro: Error | undefined;
      service.excluir(7).subscribe({ error: (e: Error) => (erro = e) });
      httpMock
        .expectOne(`${ATIVIDADES_URL}/7`)
        .error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
      expect(erro?.message).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
    });
  });
});