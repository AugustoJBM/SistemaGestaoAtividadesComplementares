import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegistroRequest, RegistroResponse } from './registro.model';

@Injectable({
  providedIn: 'root'
})
export class RegistroService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'https://api.ufape.edu.br/sgac/v1/auth/register';

  register(data: RegistroRequest): Observable<RegistroResponse> {
    return this.http.post<RegistroResponse>(this.apiUrl, data);
  }
}