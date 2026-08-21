import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { RelatorioAtividades } from './relatorio.model';

@Injectable({
  providedIn: 'root'
})
export class RelatorioService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/relatorios/atividades`;

  obterRelatorio(): Observable<RelatorioAtividades> {
    return this.http.get<RelatorioAtividades>(this.apiUrl).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErro(error))))
    );
  }

  private traduzirErro(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    if (error.status === 403) {
      return this.mensagemDoBackend(error) ?? 'Apenas estudantes podem emitir o relatório de atividades.';
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível carregar seu relatório. Tente novamente.';
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
