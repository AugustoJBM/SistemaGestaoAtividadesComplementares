import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { ContagemNaoLidas, Notificacao } from './notificacao.model';

@Injectable({
  providedIn: 'root',
})
export class NotificacaoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/notificacoes`;

  listar(apenasNaoLidas?: boolean): Observable<Notificacao[]> {
    let params = new HttpParams();
    if (apenasNaoLidas !== undefined) {
      params = params.set('apenasNaoLidas', apenasNaoLidas.toString());
    }

    return this.http.get<Notificacao[]>(this.apiUrl, { params }).pipe(
      map((lista) => lista ?? []),
      catchError((error: HttpErrorResponse) =>
        throwError(() => new Error(this.traduzirErro(error))),
      ),
    );
  }

  contarNaoLidas(): Observable<ContagemNaoLidas> {
    return this.http
      .get<ContagemNaoLidas>(`${this.apiUrl}/contagem-nao-lidas`)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErro(error))),
        ),
      );
  }

  marcarComoLida(id: number): Observable<Notificacao> {
    return this.http
      .patch<Notificacao>(`${this.apiUrl}/${id}/leitura`, {})
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErro(error))),
        ),
      );
  }

  marcarTodasComoLidas(): Observable<void> {
    return this.http
      .patch<void>(`${this.apiUrl}/leitura`, {})
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErro(error))),
        ),
      );
  }

  private traduzirErro(error: HttpErrorResponse): string {
    if (error.status === 401) return 'Sessão expirada. Faça login novamente.';
    if (error.status === 0) return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    if (error.status === 403) return 'Acesso negado às notificações.';
    if (error.status === 404) return this.mensagemDoBackend(error) ?? 'Notificação não encontrada.';
    return (
      this.mensagemDoBackend(error) ?? 'Não foi possível carregar as notificações. Tente novamente.'
    );
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
