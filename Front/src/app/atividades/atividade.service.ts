import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { API_BASE_URL } from '../api.config';
import { CadastroAtividadeRequest } from './atividade.model';

@Injectable({
    providedIn: 'root'
})
export class AtividadeService {
    private readonly http = inject(HttpClient);
    private readonly apiUrl = `${API_BASE_URL}/atividades`;

    cadastrar(dados: CadastroAtividadeRequest): Observable<unknown> {
        const formData = new FormData();

        formData.append('titulo', dados.titulo);
        formData.append('instituicaoResponsavel', dados.instituicao || '');
        formData.append('dataRealizacao', dados.data);
        formData.append('cargaHoraria', dados.cargaHoraria.toString());
        formData.append('natureza', dados.natureza); // "ACC" ou "ACEX"

        const categoriaMapeada = this.mapearCategoria(dados.categoria);
        formData.append('categoria', categoriaMapeada);

        formData.append('arquivo', dados.comprovante);

        return this.http.post(`${this.apiUrl}`, formData).pipe(
            catchError((error: HttpErrorResponse) => throwError(() => new Error(this.traduzirErro(error))))
        );
    }

    private mapearCategoria(categoriaFront: string): string {
        const mapa: Record<string, string> = {
            'cursos': 'ENSINO',
            'palestras': 'EVENTOS',
            'projetos': 'EXTENSAO',
            'pesquisa': 'PESQUISA',
            'monitoria': 'ENSINO'
        };
        return mapa[categoriaFront] || categoriaFront.toUpperCase();
    }

    private traduzirErro(error: HttpErrorResponse): string {
        if (error.status === 401) {
            return 'Sessão expirada. Faça login novamente.';
        }
        if (error.status === 0) {
            return 'Não foi possível conectar ao servidor. Verifique sua conexão.';
        }

        const corpo = error.error;
        if (typeof corpo === 'string' && corpo.trim().length > 0) {
            return corpo.trim();
        }
        if (corpo?.message && typeof corpo.message === 'string') {
            return corpo.message.trim();
        }

        return 'Erro ao cadastrar a atividade. Tente novamente mais tarde.';
    }
}