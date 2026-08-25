import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AutenticacaoService } from './autenticacao.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
    return () => {
        const authService = inject(AutenticacaoService);
        const router = inject(Router);

        if (!authService.isAuthenticated()) {
            return router.parseUrl('/login');
        }

        const role = authService.getRole();

        // Se a role não existir no token ou não estiver na lista permitida, bloqueia o acesso
        if (!role || !allowedRoles.includes(role)) {
            return router.parseUrl('/dashboard');
        }

        return true;
    };
};