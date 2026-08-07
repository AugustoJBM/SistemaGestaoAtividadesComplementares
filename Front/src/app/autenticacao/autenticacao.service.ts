import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Credenciais, LoginRequest, LoginResponse } from './autenticacao.model';
import { API_BASE_URL } from '../api.config';

const TIPO_TOKEN_PADRAO = 'Bearer';

@Injectable({
  providedIn: 'root'
})
export class AutenticacaoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/auth`;
  private readonly TOKEN_KEY = 'sgac_auth_token';
  private readonly TOKEN_TYPE_KEY = 'sgac_auth_token_type';

  login(credenciais: Credenciais): Observable<LoginResponse> {
    const payload: LoginRequest = {
      usuario: credenciais.email,
      senha: credenciais.senha
    };

    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload).pipe(
      tap((response) => this.salvarSessao(response)),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErro(error))))
    );
  }

  saveToken(token: string, tipo: string = TIPO_TOKEN_PADRAO): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.TOKEN_TYPE_KEY, tipo);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getTokenType(): string {
    return localStorage.getItem(this.TOKEN_TYPE_KEY) ?? TIPO_TOKEN_PADRAO;
  }

  // Fonte unica do encerramento de sessao: usada tanto no logout manual
  // quanto na expiracao de token detectada pelo interceptor.
  encerrarSessao(): void {
    this.limparToken();
    // A aplicacao nao grava nada em sessionStorage hoje; limpar tudo garante
    // que nenhum residuo de sessao sobreviva a saida.
    sessionStorage.clear();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private limparToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.TOKEN_TYPE_KEY);
  }

  private salvarSessao(response: LoginResponse): void {
    if (response?.token) {
      this.saveToken(response.token, response.tipo || TIPO_TOKEN_PADRAO);
    }
  }

  // Traduz o erro de transporte para uma mensagem de dominio, para que os
  // componentes visuais nao precisem conhecer status HTTP.
  private traduzirErro(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Credenciais inválidas.';
    }

    // status 0 indica que a requisicao nao chegou ao servidor.
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }

    return error.error?.message || 'Ocorreu um erro ao realizar o login. Tente novamente.';
  }
}
