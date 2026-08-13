import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api.config';
import { AtividadeRequest, AtividadeResponse } from './atividade.model';

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

    return this.http.post<AtividadeResponse>(this.apiUrl, formData);
  }
}