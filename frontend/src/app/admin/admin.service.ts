import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api.config';
import { CadastroInstitucionalRequest, Curso, UsuarioAdmin } from './admin.model';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly adminUrl = `${API_BASE_URL}/admin`;

  listarUsuarios(role?: string, ativo?: boolean): Observable<UsuarioAdmin[]> {
    let params = new HttpParams();
    if (role) params = params.set('role', role);
    if (ativo !== undefined) params = params.set('ativo', ativo.toString());
    return this.http.get<UsuarioAdmin[]>(`${this.adminUrl}/usuarios`, { params });
  }

  cadastrarUsuarioInstitucional(data: CadastroInstitucionalRequest): Observable<UsuarioAdmin> {
    return this.http.post<UsuarioAdmin>(`${this.adminUrl}/usuarios`, data);
  }

  alternarStatusUsuario(id: number): Observable<UsuarioAdmin> {
    return this.http.patch<UsuarioAdmin>(`${this.adminUrl}/usuarios/${id}/status`, {});
  }

  listarCursos(): Observable<Curso[]> {
    return this.http.get<Curso[]>(`${this.adminUrl}/cursos`);
  }

  criarCurso(curso: Curso): Observable<Curso> {
    return this.http.post<Curso>(`${this.adminUrl}/cursos`, curso);
  }

  atualizarCurso(id: number, curso: Curso): Observable<Curso> {
    return this.http.put<Curso>(`${this.adminUrl}/cursos/${id}`, curso);
  }
}
