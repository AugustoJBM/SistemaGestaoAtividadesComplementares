import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticacaoService } from './autenticacao.service';
import { Role } from './autenticacao.model';

export const roleGuard = (allowedRoles: Role[]): CanActivateFn => {
  return () => {
    const authService = inject(AutenticacaoService);
    const router = inject(Router);

    if (!authService.isAuthenticated()) {
      return router.parseUrl('/login');
    }

    const role = authService.perfilAtual();

    if (!role || !allowedRoles.includes(role)) {
      if (role === 'AVALIADOR' || role === 'ADMINISTRADOR') {
        return router.parseUrl('/avaliacao/solicitacoes');
      }
      return router.parseUrl('/dashboard');
    }

    return true;
  };
};
