import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { API_BASE_URL } from '../../api.config';
import { StatusSolicitacao } from '../solicitacao.model';
import { SolicitacaoAvaliadorDetalhe, SolicitacaoAvaliadorResumo } from './avaliacao.model';

@Injectable({
  providedIn: 'root',
})
export class AvaliacaoSolicitacaoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/solicitacoes`;

  consultar(status?: StatusSolicitacao): Observable<SolicitacaoAvaliadorResumo[]> {
    const params = status ? new HttpParams().set('status', status) : new HttpParams();
    return this.http.get<SolicitacaoAvaliadorResumo[]>(`${this.apiUrl}/avaliacao`, { params }).pipe(
      map((solicitacoes) => solicitacoes ?? []),
      catchError((error: HttpErrorResponse) =>
        throwError(() => new Error(this.traduzirErroConsulta(error))),
      ),
    );
  }

  detalhar(id: number): Observable<SolicitacaoAvaliadorDetalhe> {
    return this.http
      .get<SolicitacaoAvaliadorDetalhe>(`${this.apiUrl}/${id}/avaliacao`)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErroDetalhe(error))),
        ),
      );
  }

  private traduzirErroConsulta(error: HttpErrorResponse): string {
    const comum = this.traduzirErroComum(error);
    if (comum) return comum;
    return (
      this.mensagemDoBackend(error) ?? 'Não foi possível carregar as solicitações. Tente novamente.'
    );
  }

  private traduzirErroDetalhe(error: HttpErrorResponse): string {
    const comum = this.traduzirErroComum(error);
    if (comum) return comum;
    if (error.status === 404) {
      return this.mensagemDoBackend(error) ?? 'Solicitação não encontrada.';
    }
    return (
      this.mensagemDoBackend(error) ??
      'Não foi possível carregar os detalhes da solicitação. Tente novamente.'
    );
  }

  private traduzirErroComum(error: HttpErrorResponse): string | null {
    if (error.status === 401) return 'Sessão expirada. Faça login novamente.';
    if (error.status === 0) return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    if (error.status === 403)
      return (
        this.mensagemDoBackend(error) ??
        'Apenas avaliadores podem consultar as solicitações de validação.'
      );
    return null;
  }

  private mensagemDoBackend(error: HttpErrorResponse): string | null {
    const corpo: unknown = error.error;
    if (typeof corpo === 'string' && corpo.trim().length > 0) {
      return corpo.trim();
    }
    const mensagem = (corpo as { message?: unknown } | null)?.message;
    if (typeof mensagem === 'string' && mensagem.trim().length > 0) {
      return mensagem.trim();
    }
    return null;
  }
}
