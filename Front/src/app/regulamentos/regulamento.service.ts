import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api.config';

export interface IngestaoNormativaResponse {
  nomeDocumento: string;
  totalChunksExtraidos: number;
  status: string;
  mensagem: string;
}

export interface RegulamentoChunk {
  id: number;
  artigo: string;
  conteudoTexto: string;
}

@Injectable({
  providedIn: 'root'
})
export class RegulamentoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/regulamentos`;

  ingerirDocumento(arquivo: File, substituirExistentes: boolean): Observable<IngestaoNormativaResponse> {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    return this.http.post<IngestaoNormativaResponse>(
      `${this.apiUrl}/ingerir?substituirExistentes=${substituirExistentes}`,
      formData
    );
  }

  listarRegulamentos(): Observable<RegulamentoChunk[]> {
    return this.http.get<RegulamentoChunk[]>(this.apiUrl);
  }
}