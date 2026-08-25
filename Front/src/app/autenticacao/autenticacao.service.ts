import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { Credenciais, LoginResponse } from './autenticacao.model';
import { RegistroRequest, RegistroResponse } from './registro/registro.model';

const CHAVE_TOKEN = 'sgac_token';
const CHAVE_TIPO_TOKEN = 'sgac_token_tipo';

@Injectable({
  providedIn: 'root'
})
export class AutenticacaoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/auth`;

  cadastrar(request: RegistroRequest): Observable<RegistroResponse> {
    return this.http.post<RegistroResponse>(`${this.apiUrl}/cadastro`, request).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroCadastro(error))))
    );
  }

  login(credenciais: Credenciais): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, {
      usuario: credenciais.email,
      senha: credenciais.senha
    }).pipe(
      tap((resposta) => this.saveToken(resposta.token, resposta.tipo || 'Bearer')),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroLogin(error))))
    );
  }

  saveToken(token: string, tipo: string = 'Bearer'): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.setItem(CHAVE_TOKEN, token);
      localStorage.setItem(CHAVE_TIPO_TOKEN, tipo);
    }
  }

  salvarSessao(resposta: LoginResponse): void {
    this.saveToken(resposta.token, resposta.tipo || 'Bearer');
  }

  getToken(): string | null {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem(CHAVE_TOKEN);
  }

  getTokenType(): string {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
      return 'Bearer';
    }
    return localStorage.getItem(CHAVE_TIPO_TOKEN) ?? 'Bearer';
  }

  getRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payloadBase64 = token.split('.')[1];
      if (!payloadBase64) return null;
      const json = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      const parsed = JSON.parse(json);
      return parsed.role ?? parsed.roles?.[0] ?? null;
    } catch {
      return null;
    }
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  estaAutenticado(): boolean {
    return this.isAuthenticated();
  }

  encerrarSessao(): void {
    if (typeof window !== 'undefined') {
      if (typeof localStorage !== 'undefined') {
        localStorage.clear();
      }
      if (typeof sessionStorage !== 'undefined') {
        sessionStorage.clear();
      }
    }
  }

  private traduzirErroCadastro(error: HttpErrorResponse): string {
    if (error.status === 400 && error.error?.message) return error.error.message;
    if (error.status === 400 && typeof error.error === 'string') return error.error;
    if (error.status === 409) return 'Este e-mail já está cadastrado.';
    if (error.status === 0) return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    return 'Não foi possível realizar o cadastro. Tente novamente mais tarde.';
  }

  private traduzirErroLogin(error: HttpErrorResponse): string {
    if (error.status === 401) return 'Credenciais inválidas.';
    if (error.status === 0) return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    return 'Ocorreu um erro ao realizar o login. Tente novamente.';
  }
}