import { inject } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AutenticacaoService } from './autenticacao.service';

export const AuthInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
  const authService = inject(AutenticacaoService);
  const router = inject(Router);

  const token = authService.getToken();
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `${authService.getTokenType()} ${token}` } })
    : req;

  const isPublicAuthRoute = req.url.includes('/auth/cadastro') || req.url.includes('/auth/login');

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Apenas 401 (sessão inválida/expirada) deve deslogar e limpar a sessão
      if (error.status === 401 && !isPublicAuthRoute) {
        authService.encerrarSessao();
        void router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};