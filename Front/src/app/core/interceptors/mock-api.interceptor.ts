import { HttpHandlerFn, HttpInterceptorFn, HttpRequest, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { NOTIFICACOES_MOCK } from '../../notificacao/notificacao.mock';
import { ContagemNaoLidas, Notificacao } from '../../notificacao/notificacao.model';

export const mockApiInterceptor: HttpInterceptorFn = (
    req: HttpRequest<unknown>,
    next: HttpHandlerFn,
) => {
    // Verifica se o mock está ativado no environment ou se foi forçado no navegador
    const overrideRuntime = typeof window !== 'undefined' && localStorage.getItem('sgac_use_mocks');
    const mocksAtivos = overrideRuntime !== null ? overrideRuntime === 'true' : environment.useMocks;

    // Se os mocks estiverem desativados, repassa a chamada diretamente para a API real
    if (!mocksAtivos) {
        return next(req);
    }

    // GET /api/v1/notificacoes/contagem-nao-lidas
    if (req.url.endsWith('/notificacoes/contagem-nao-lidas') && req.method === 'GET') {
        const naoLidas = NOTIFICACOES_MOCK.filter((n) => !n.lida).length;
        const body: ContagemNaoLidas = { naoLidas };
        return of(new HttpResponse({ status: 200, body })).pipe(delay(250));
    }

    // PATCH /api/v1/notificacoes/{id}/leitura
    const matchItemLeitura = req.url.match(/\/notificacoes\/(\d+)\/leitura$/);
    if (matchItemLeitura && req.method === 'PATCH') {
        const id = Number(matchItemLeitura[1]);
        const index = NOTIFICACOES_MOCK.findIndex((n) => n.id === id);

        if (index !== -1) {
            NOTIFICACOES_MOCK[index] = { ...NOTIFICACOES_MOCK[index], lida: true };
            return of(new HttpResponse({ status: 200, body: NOTIFICACOES_MOCK[index] })).pipe(delay(200));
        }
        return of(new HttpResponse({ status: 404, body: { message: 'Notificação não encontrada.' } }));
    }

    // PATCH /api/v1/notificacoes/leitura (todas)
    if (req.url.endsWith('/notificacoes/leitura') && req.method === 'PATCH') {
        NOTIFICACOES_MOCK.forEach((n) => (n.lida = true));
        return of(new HttpResponse<void>({ status: 204 })).pipe(delay(300));
    }

    // GET /api/v1/notificacoes
    if (req.url.includes('/notificacoes') && req.method === 'GET') {
        const apenasNaoLidas = req.params.get('apenasNaoLidas') === 'true';
        const lista = apenasNaoLidas
            ? NOTIFICACOES_MOCK.filter((n) => !n.lida)
            : [...NOTIFICACOES_MOCK];

        return of(new HttpResponse<Notificacao[]>({ status: 200, body: lista })).pipe(delay(350));
    }

    return next(req);
};