import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { StatusSolicitacao } from '../solicitacao/solicitacao.model';
import {
  AvaliacaoRequest,
  DecisaoAvaliacao,
  SolicitacaoAvaliadorDetalhe,
  SolicitacaoAvaliadorResumo,
} from './avaliacao.model';

@Injectable({
  providedIn: 'root',
})
export class AvaliacaoService {
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

  listarPendentes(): Observable<SolicitacaoAvaliadorResumo[]> {
    return this.consultar();
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

  avaliar(
    id: number,
    decisao: DecisaoAvaliacao,
    justificativa?: string,
  ): Observable<SolicitacaoAvaliadorDetalhe> {
    const payload: AvaliacaoRequest = {
      decisao,
      justificativa: justificativa?.trim() || undefined,
    };

    return this.http
      .patch<SolicitacaoAvaliadorDetalhe>(`${this.apiUrl}/${id}/avaliacao`, payload)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErroAvaliacao(error))),
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

  private traduzirErroAvaliacao(error: HttpErrorResponse): string {
    const comum = this.traduzirErroComum(error);
    if (comum) return comum;
    if (error.status === 409) {
      return (
        this.mensagemDoBackend(error) ??
        'Esta solicitação já foi avaliada ou seu status foi alterado por outro usuário.'
      );
    }
    if (error.status === 400) {
      return this.mensagemDoBackend(error) ?? 'Dados da avaliação inválidos.';
    }
    if (error.status === 404) {
      return this.mensagemDoBackend(error) ?? 'Solicitação não encontrada.';
    }
    return (
      this.mensagemDoBackend(error) ??
      'Não foi possível registrar a decisão da solicitação. Tente novamente.'
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
