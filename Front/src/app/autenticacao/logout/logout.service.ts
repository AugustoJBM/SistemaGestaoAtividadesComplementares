import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../api.config';

export interface LogoutResponse {
  message: string;
  success: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class LogoutService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/auth/logout`;

  logout(): Observable<void> {
    return this.http.post<void>(this.apiUrl, {});
  }
}