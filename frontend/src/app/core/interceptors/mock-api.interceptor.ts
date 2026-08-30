import { HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { NOTIFICACOES_MOCK } from '../../notificacao/notificacao.mock';
import { ContagemNaoLidas, Notificacao } from '../../notificacao/notificacao.model';
import {
  ATIVIDADES_MOCK,
  REGULAMENTOS_MOCK,
  SOLICITACOES_AVALIADOR_MOCK,
  gerarTokenMock,
  obterProgressoCalculado,
  obterRelatorioCalculado,
} from '../mocks/mock-data';
import { Atividade, Natureza, Categoria } from '../../atividades/atividade.model';
import {
  SolicitacaoAvaliadorDetalhe,
  SolicitacaoAvaliadorResumo,
} from '../../avaliacao/avaliacao.model';
import { SolicitacaoDetalhe, SolicitacaoResumo } from '../../solicitacao/solicitacao.model';

export const mockApiInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
) => {
  const overrideRuntime = typeof window !== 'undefined' && localStorage.getItem('sgac_use_mocks');
  const mocksAtivos = overrideRuntime !== null ? overrideRuntime === 'true' : environment.useMocks;

  if (!mocksAtivos) {
    return next(req);
  }

  const url = req.url.split('?')[0].replace(/\/+$/, '');
  const method = req.method;

  // 1. RELATÓRIOS (Processado antes para evitar colisão com /atividades)
  if (url.endsWith('/relatorios/atividades') && method === 'GET') {
    return of(
      new HttpResponse({ status: 200, body: obterRelatorioCalculado('aluno1@ufape.edu.br') }),
    ).pipe(delay(200));
  }

  // 2. AUTENTICAÇÃO (/auth)
  if (url.endsWith('/auth/login') && method === 'POST') {
    const body = req.body as { usuario?: string; senha?: string };
    const email = body?.usuario?.toLowerCase() ?? '';
    let role: 'ESTUDANTE' | 'AVALIADOR' | 'ADMINISTRADOR' = 'ESTUDANTE';

    if (email.includes('avaliador') || email.includes('professor')) {
      role = 'AVALIADOR';
    } else if (email.includes('admin')) {
      role = 'ADMINISTRADOR';
    }

    const token = gerarTokenMock(email || 'usuario@ufape.edu.br', role);
    return of(new HttpResponse({ status: 200, body: { token, tipo: 'Bearer' } })).pipe(delay(200));
  }

  if (url.endsWith('/auth/cadastro') && method === 'POST') {
    const body = req.body as { nome?: string; email?: string };
    return of(
      new HttpResponse({
        status: 201,
        body: {
          id: Date.now(),
          nome: body?.nome ?? 'Novo Estudante',
          email: body?.email ?? 'novo@ufape.edu.br',
          role: 'ESTUDANTE',
        },
      }),
    ).pipe(delay(250));
  }

  if (url.endsWith('/auth/logout') && method === 'POST') {
    return of(new HttpResponse({ status: 200, body: { success: true } })).pipe(delay(100));
  }

  // 3. ATIVIDADES & PROGRESSO (/atividades)
  if (url.endsWith('/atividades/progresso') && method === 'GET') {
    return of(new HttpResponse({ status: 200, body: obterProgressoCalculado() })).pipe(delay(150));
  }

  if (url.endsWith('/atividades/extrair-certificado') && method === 'POST') {
    return of(
      new HttpResponse({
        status: 200,
        body: {
          titulo: 'Minicurso Prático de Inteligência Artificial e LLMs',
          instituicaoResponsavel: 'UFAPE',
          dataRealizacao: new Date().toISOString().split('T')[0],
          cargaHoraria: 20,
          natureza: 'ACC',
          categoria: 'ENSINO',
        },
      }),
    ).pipe(delay(350));
  }

  const matchParecer = url.match(/\/atividades\/(\d+)\/parecer$/);
  if (matchParecer && method === 'GET') {
    return of(
      new HttpResponse({
        status: 200,
        body: {
          id: Date.now(),
          atividadeId: Number(matchParecer[1]),
          naturezaSugerida: 'ACC',
          categoriaSugerida: 'ENSINO',
          cargaHorariaAproveitavel: 30,
          artigoRegulamento: 'Art. 12 do Regulamento de ACC',
          justificativaTecnica: 'Atividade em conformidade com o PPC.',
          scoreConfianca: 0.95,
          decisaoIA: 'DEFERIDO',
          tempoProcessamentoMs: 120,
        },
      }),
    ).pipe(delay(200));
  }

  const matchCertificado = url.match(/\/atividades\/(\d+)\/certificado$/);
  if (matchCertificado && method === 'GET') {
    const dummyBlob = new Blob(['%PDF-1.4 Mocked Document Content'], { type: 'application/pdf' });
    return of(new HttpResponse({ status: 200, body: dummyBlob })).pipe(delay(100));
  }

  const matchAtividadeId = url.match(/\/atividades\/(\d+)$/);
  if (matchAtividadeId && method === 'PUT') {
    const id = Number(matchAtividadeId[1]);
    const index = ATIVIDADES_MOCK.findIndex((a) => a.id === id);
    if (index !== -1) {
      return of(new HttpResponse({ status: 200, body: ATIVIDADES_MOCK[index] })).pipe(delay(200));
    }
    return of(new HttpResponse({ status: 200, body: { id, status: 'PENDENTE' } })).pipe(delay(200));
  }

  if (matchAtividadeId && method === 'DELETE') {
    const id = Number(matchAtividadeId[1]);
    const index = ATIVIDADES_MOCK.findIndex((a) => a.id === id);
    if (index !== -1) {
      ATIVIDADES_MOCK.splice(index, 1);
    }
    return of(new HttpResponse({ status: 204, body: null })).pipe(delay(150));
  }

  if (url.endsWith('/atividades') && method === 'POST') {
    const novaAtividade: Atividade = {
      id: Date.now(),
      titulo: 'Atividade Registrada',
      instituicaoResponsavel: 'UFAPE',
      dataRealizacao: new Date().toISOString().split('T')[0],
      cargaHorariaEmHoras: 20,
      natureza: 'ACC',
      categoria: 'ENSINO',
      dataCadastro: new Date().toISOString(),
      status: 'PENDENTE',
    };
    ATIVIDADES_MOCK.unshift(novaAtividade);
    return of(new HttpResponse({ status: 201, body: novaAtividade })).pipe(delay(250));
  }

  if (url.endsWith('/atividades') && method === 'GET') {
    const natureza = req.params.get('natureza');
    const categoria = req.params.get('categoria');

    let filtradas = [...ATIVIDADES_MOCK];
    if (natureza) filtradas = filtradas.filter((a) => a.natureza === (natureza as Natureza));
    if (categoria) filtradas = filtradas.filter((a) => a.categoria === (categoria as Categoria));

    return of(new HttpResponse({ status: 200, body: filtradas })).pipe(delay(150));
  }

  // 4. SOLICITAÇÕES E AVALIAÇÃO (/solicitacoes)
  const matchAvaliacaoDecisao = url.match(/\/solicitacoes\/(\d+)\/avaliacao$/);
  if (matchAvaliacaoDecisao && method === 'PATCH') {
    const id = Number(matchAvaliacaoDecisao[1]);
    const body = req.body as { decisao: string; justificativa?: string };
    const item = SOLICITACOES_AVALIADOR_MOCK.find((s) => s.id === id);
    if (item) {
      item.status = body.decisao as any;
      item.justificativa = body.justificativa;
      item.dataAvaliacao = new Date().toISOString();

      if (body.decisao === 'APROVADA') {
        item.itens.forEach((it) => {
          const atv = ATIVIDADES_MOCK.find((a) => a.id === it.atividadeId);
          if (atv) atv.status = 'APROVADA';
        });
      }

      return of(new HttpResponse({ status: 200, body: item })).pipe(delay(200));
    }
    return of(
      new HttpResponse({ status: 404, body: { message: 'Solicitação não encontrada.' } }),
    ).pipe(delay(150));
  }

  if (matchAvaliacaoDecisao && method === 'GET') {
    const id = Number(matchAvaliacaoDecisao[1]);
    const item = SOLICITACOES_AVALIADOR_MOCK.find((s) => s.id === id);
    if (item) {
      return of(new HttpResponse({ status: 200, body: item })).pipe(delay(150));
    }
    return of(
      new HttpResponse({ status: 404, body: { message: 'Solicitação não encontrada.' } }),
    ).pipe(delay(150));
  }

  if (url.endsWith('/solicitacoes/avaliacao') && method === 'GET') {
    const status = req.params.get('status');
    let lista: SolicitacaoAvaliadorResumo[] = SOLICITACOES_AVALIADOR_MOCK.map((s) => ({
      id: s.id,
      estudanteNome: s.estudanteNome,
      dataSubmissao: s.dataSubmissao,
      status: s.status,
      dataAvaliacao: s.dataAvaliacao,
      totalAtividades: s.itens.length,
      cargaHorariaTotal: s.cargaHorariaTotal,
    }));

    if (status) {
      lista = lista.filter((s) => s.status === status);
    }
    return of(new HttpResponse({ status: 200, body: lista })).pipe(delay(200));
  }

  const matchSolicitacaoEstudanteId = url.match(/\/solicitacoes\/(\d+)$/);
  if (matchSolicitacaoEstudanteId && method === 'GET') {
    const id = Number(matchSolicitacaoEstudanteId[1]);
    const item = SOLICITACOES_AVALIADOR_MOCK.find((s) => s.id === id);
    if (item) {
      const detalheEstudante: SolicitacaoDetalhe = {
        id: item.id,
        status: item.status,
        dataSubmissao: item.dataSubmissao,
        dataAvaliacao: item.dataAvaliacao,
        justificativa: item.justificativa,
        totalAtividades: item.itens.length,
        itens: item.itens,
      };
      return of(new HttpResponse({ status: 200, body: detalheEstudante })).pipe(delay(150));
    }
    return of(
      new HttpResponse({ status: 404, body: { message: 'Solicitação não encontrada.' } }),
    ).pipe(delay(150));
  }

  if (url.endsWith('/solicitacoes') && method === 'POST') {
    const novaSolicitacao: SolicitacaoAvaliadorDetalhe = {
      id: Date.now(),
      estudanteNome: 'Lucas Gabriel Silva',
      estudanteEmail: 'aluno1@ufape.edu.br',
      dataSubmissao: new Date().toISOString(),
      status: 'SUBMETIDA',
      cargaHorariaTotal: ATIVIDADES_MOCK.reduce((acc, cur) => acc + cur.cargaHorariaEmHoras, 0),
      itens: ATIVIDADES_MOCK.map((a) => ({
        atividadeId: a.id,
        titulo: a.titulo,
        cargaHoraria: a.cargaHorariaEmHoras,
        natureza: a.natureza,
      })),
    };
    SOLICITACOES_AVALIADOR_MOCK.unshift(novaSolicitacao);

    const detalheEstudante: SolicitacaoDetalhe = {
      id: novaSolicitacao.id,
      status: novaSolicitacao.status,
      dataSubmissao: novaSolicitacao.dataSubmissao,
      totalAtividades: novaSolicitacao.itens.length,
      itens: novaSolicitacao.itens,
    };
    return of(new HttpResponse({ status: 201, body: detalheEstudante })).pipe(delay(250));
  }

  if (url.endsWith('/solicitacoes') && method === 'GET') {
    const listaEstudante: SolicitacaoResumo[] = SOLICITACOES_AVALIADOR_MOCK.map((s) => ({
      id: s.id,
      status: s.status,
      dataSubmissao: s.dataSubmissao,
      dataAvaliacao: s.dataAvaliacao,
      totalAtividades: s.itens.length,
    }));
    return of(new HttpResponse({ status: 200, body: listaEstudante })).pipe(delay(150));
  }

  // 5. REGULAMENTOS (/regulamentos)
  if (url.endsWith('/regulamentos/ingerir') && method === 'POST') {
    return of(
      new HttpResponse({
        status: 200,
        body: {
          nomeDocumento: 'Regulamento_PPC_2026.pdf',
          totalChunksExtraidos: 4,
          status: 'SUCESSO',
          mensagem: 'Regulamento processado e vetorizado com sucesso via mock.',
        },
      }),
    ).pipe(delay(300));
  }

  if (url.endsWith('/regulamentos') && method === 'GET') {
    return of(new HttpResponse({ status: 200, body: REGULAMENTOS_MOCK })).pipe(delay(150));
  }

  // 6. NOTIFICAÇÕES (/notificacoes)
  if (url.endsWith('/notificacoes/contagem-nao-lidas') && method === 'GET') {
    const naoLidas = NOTIFICACOES_MOCK.filter((n) => !n.lida).length;
    const body: ContagemNaoLidas = { naoLidas };
    return of(new HttpResponse({ status: 200, body })).pipe(delay(100));
  }

  if (url.endsWith('/notificacoes/leitura') && method === 'PATCH') {
    NOTIFICACOES_MOCK.forEach((n) => (n.lida = true));
    return of(new HttpResponse<void>({ status: 204 })).pipe(delay(150));
  }

  const matchItemLeitura = url.match(/\/notificacoes\/(\d+)\/leitura$/);
  if (matchItemLeitura && method === 'PATCH') {
    const id = Number(matchItemLeitura[1]);
    const index = NOTIFICACOES_MOCK.findIndex((n) => n.id === id);
    if (index !== -1) {
      NOTIFICACOES_MOCK[index] = { ...NOTIFICACOES_MOCK[index], lida: true };
      return of(new HttpResponse({ status: 200, body: NOTIFICACOES_MOCK[index] })).pipe(delay(100));
    }
    return of(new HttpResponse({ status: 404, body: { message: 'Notificação não encontrada.' } }));
  }

  if (url.endsWith('/notificacoes') && method === 'GET') {
    const apenasNaoLidas = req.params.get('apenasNaoLidas') === 'true';
    const lista = apenasNaoLidas
      ? NOTIFICACOES_MOCK.filter((n) => !n.lida)
      : [...NOTIFICACOES_MOCK];
    return of(new HttpResponse<Notificacao[]>({ status: 200, body: lista })).pipe(delay(150));
  }

  return next(req);
};
