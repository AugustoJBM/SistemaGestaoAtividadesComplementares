import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import {
  Atividade,
  AtividadeListagemDTO,
  AtividadeRequest,
  AtividadeResponse,
  FiltroAtividades
} from './atividade.model';

@Injectable({
  providedIn: 'root'
})
export class AtividadeService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/atividades`;

  cadastrar(request: AtividadeRequest): Observable<AtividadeResponse> {
    const formData = new FormData();
    formData.append('titulo', request.titulo);
    formData.append('instituicaoResponsavel', request.instituicaoResponsavel);
    formData.append('dataRealizacao', request.dataRealizacao);
    formData.append('cargaHoraria', request.cargaHoraria.toString());
    formData.append('natureza', request.natureza);
    formData.append('categoria', request.categoria);
    formData.append('arquivo', request.arquivo);

    return this.http.post<AtividadeResponse>(this.apiUrl, formData).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroCadastro(error))))
    );
  }

  listar(filtro: FiltroAtividades = {}): Observable<Atividade[]> {
    let params = new HttpParams();
    if (filtro.natureza) {
      params = params.set('natureza', filtro.natureza);
    }
    if (filtro.categoria) {
      params = params.set('categoria', filtro.categoria);
    }

    return this.http.get<AtividadeListagemDTO[]>(this.apiUrl, { params }).pipe(
      map((dtos) => (dtos ?? []).map((dto) => this.paraAtividade(dto))),
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroListagem(error))))
    );
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErroExclusao(error))))
    );
  }

  private paraAtividade(dto: AtividadeListagemDTO): Atividade {
    return {
      id: dto?.id ?? 0,
      titulo: dto?.titulo ?? '',
      instituicaoResponsavel: dto?.instituicaoResponsavel ?? '',
      dataRealizacao: dto?.dataRealizacao ?? '',
      cargaHorariaEmHoras: dto?.cargaHorariaEmHoras ?? 0,
      natureza: dto?.natureza ?? '',
      categoria: dto?.categoria ?? '',
      dataCadastro: dto?.dataCadastro ?? null
    };
  }

  private traduzirErroCadastro(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    if (error.status === 403) {
      return this.mensagemDoBackend(error) ?? 'Apenas estudantes podem cadastrar atividades.';
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível cadastrar a atividade. Tente novamente.';
  }

  private traduzirErroListagem(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    if (error.status === 403) {
      return this.mensagemDoBackend(error) ?? 'Apenas estudantes podem consultar suas atividades.';
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível carregar suas atividades. Tente novamente.';
  }

  private traduzirErroExclusao(error: HttpErrorResponse): string {
    if (error.status === 401) {
      return 'Sessão expirada. Faça login novamente.';
    }
    if (error.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    }
    if (error.status === 403) {
      return this.mensagemDoBackend(error) ?? 'Você só pode excluir suas próprias atividades.';
    }
    if (error.status === 404) {
      return this.mensagemDoBackend(error) ?? 'Atividade não encontrada.';
    }
    return this.mensagemDoBackend(error) ?? 'Não foi possível excluir a atividade. Tente novamente.';
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