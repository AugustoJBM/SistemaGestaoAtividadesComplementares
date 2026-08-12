import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {
  ProgressoCargaHoraria,
  ProgressoCargaHorariaDTO,
  ProgressoModalidade,
  ProgressoModalidadeDTO
} from './progresso.model';
import { API_BASE_URL } from '../api.config';

@Injectable({
  providedIn: 'root'
})
export class ProgressoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/atividades`;

  obterProgresso(): Observable<ProgressoCargaHoraria> {
    return this.http.get<ProgressoCargaHorariaDTO>(`${this.apiUrl}/progresso`).pipe(
      map((dto) => ({
        acc: this.paraProgressoModalidade(dto?.acc),
        acex: this.paraProgressoModalidade(dto?.acex)
      })),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErro(error))))
    );
  }

  // Modalidade ausente/nula (estudante sem atividades) vira zeros, nunca undefined/NaN.
  private paraProgressoModalidade(modalidade: ProgressoModalidadeDTO | null | undefined): ProgressoModalidade {
    const horasAcumuladas = modalidade?.horasAcumuladas ?? 0;
    const horasExigidas = modalidade?.horasExigidas ?? 0;
    const percentualConcluido = modalidade?.percentualConcluido ?? 0;

    return {
      horasAcumuladas,
      horasExigidas,
      horasRestantes: Math.max(0, horasExigidas - horasAcumuladas),
      percentualConcluido
    };
  }

  // Traduz o erro de transporte para uma mensagem de dominio, para que os
  // componentes visuais nao precisem conhecer status HTTP.
  private traduzirErro(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }

    // status 0 indica que a requisicao nao chegou ao servidor.
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }

    return error.error?.message || 'Não foi possível carregar seu progresso. Tente novamente.';
  }
}
