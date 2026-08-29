import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { NavbarComponent } from './navbar.component';
import { AutenticacaoService } from '../../../autenticacao/autenticacao.service';
import { Role } from '../../../autenticacao/autenticacao.model';

describe('NavbarComponent', () => {
  afterEach(() => {
    TestBed.resetTestingModule();
  });

  const montar = (autenticado: boolean, perfil: Role | null) => {
    const authServiceSpy = {
      isAuthenticated: vi.fn().mockReturnValue(autenticado),
      perfilAtual: vi.fn().mockReturnValue(perfil),
      getRole: vi.fn().mockReturnValue(perfil),
    };

    TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [provideRouter([]), { provide: AutenticacaoService, useValue: authServiceSpy }],
    });

    const fixture = TestBed.createComponent(NavbarComponent);
    fixture.detectChanges();
    return fixture;
  };

  it('deve renderizar links de estudante para perfil ESTUDANTE', () => {
    const fixture = montar(true, 'ESTUDANTE');
    const texto = fixture.nativeElement.textContent as string;

    expect(texto).toContain('Dashboard');
    expect(texto).toContain('Atividades');
    expect(texto).toContain('Progresso');
    expect(texto).toContain('Relatório');
    expect(texto).toContain('Minhas Solicitações');
    expect(texto).toContain('ESTUDANTE');
    expect(texto).not.toContain('Fila de Avaliação');
  });

  it('deve renderizar links de avaliador para perfil AVALIADOR', () => {
    const fixture = montar(true, 'AVALIADOR');
    const texto = fixture.nativeElement.textContent as string;

    expect(texto).toContain('Fila de Avaliação');
    expect(texto).toContain('Regulamentos');
    expect(texto).toContain('AVALIADOR');
    expect(texto).not.toContain('Minhas Solicitações');
    expect(texto).not.toContain('Relatório');
  });

  it('deve ocultar links restritos para visitante anonimo', () => {
    const fixture = montar(false, null);
    const texto = fixture.nativeElement.textContent as string;

    expect(texto).toContain('Entrar');
    expect(texto).not.toContain('Dashboard');
    expect(texto).not.toContain('Fila de Avaliação');
    expect(texto).not.toContain('Sair');
  });
});
