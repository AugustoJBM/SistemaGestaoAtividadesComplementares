import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RegistroRequest, RegistroResponse } from './registro.model';
import { API_BASE_URL } from '../../api.config';

@Injectable({
  providedIn: 'root',
})
export class RegistroService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/auth`;

  register(data: RegistroRequest): Observable<RegistroResponse> {
    const payload = {
      nome: data.fullName,
      email: data.emailOrRegistration,
      senha: data.password,
      role: 'ESTUDANTE',
    };

    return this.http
      .post<RegistroResponse>(`${this.apiUrl}/cadastro`, payload)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          throwError(() => new Error(this.traduzirErro(error))),
        ),
      );
  }

  private traduzirErro(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    return this.mensagemDoBackend(error) ?? 'Erro ao cadastrar. Tente novamente.';
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
