import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { SolicitacaoDetalhe, SolicitacaoResumo } from './solicitacao.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacaoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/solicitacoes`;

  submeter(): Observable<SolicitacaoDetalhe> {
    return this.http.post<SolicitacaoDetalhe>(this.apiUrl, {}).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroSubmissao(error))))
    );
  }

  listar(): Observable<SolicitacaoResumo[]> {
    return this.http.get<SolicitacaoResumo[]>(this.apiUrl).pipe(
      map((solicitacoes) => solicitacoes ?? []),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroLeitura(error))))
    );
  }

  detalhar(id: number): Observable<SolicitacaoDetalhe> {
    return this.http.get<SolicitacaoDetalhe>(`${this.apiUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroLeitura(error))))
    );
  }

  private traduzirErroSubmissao(error: HttpErrorResponse): string {
    const comum = this.traduzirErroComum(error);
    if (comum) {
      return comum;
    }
    if (error.status === 409) {
      return (
        this.mensagemDoBackend(error) ??
        'Você já possui uma solicitação em aberto. Acompanhe o andamento antes de enviar outra.'
      );
    }
    if (error.status === 422 || error.status === 400) {
      return (
        this.mensagemDoBackend(error) ??
        'Cadastre ao menos uma atividade antes de enviar o relatório para validação.'
      );
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível enviar o relatório para validação. Tente novamente.';
  }

  private traduzirErroLeitura(error: HttpErrorResponse): string {
    const comum = this.traduzirErroComum(error);
    if (comum) {
      return comum;
    }
    if (error.status === 404) {
      return this.mensagemDoBackend(error) ?? 'Solicitação não encontrada.';
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível carregar suas solicitações. Tente novamente.';
  }

  private traduzirErroComum(error: HttpErrorResponse): string | null {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    if (error.status === 403) {
      return this.mensagemDoBackend(error) ?? 'Apenas estudantes podem solicitar a validação de atividades.';
    }
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
