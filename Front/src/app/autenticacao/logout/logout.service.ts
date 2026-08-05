import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LogoutResponse {
  message: string;
  success: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class LogoutService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'https://api.ufape.edu.br/sgac/v1/auth/logout';

  logout(): Observable<LogoutResponse> {
    return this.http.post<LogoutResponse>(this.apiUrl, {});
  }
}