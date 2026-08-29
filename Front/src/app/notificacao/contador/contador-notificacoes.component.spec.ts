import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContadorNotificacoesComponent } from './contador-notificacoes.component';
import { NotificacaoService } from '../notificacao.service';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { beforeEach, describe, expect, it, vi } from 'vitest';

describe('ContadorNotificacoesComponent', () => {
  let component: ContadorNotificacoesComponent;
  let fixture: ComponentFixture<ContadorNotificacoesComponent>;
  let notificacaoServiceMock: { contarNaoLidas: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    notificacaoServiceMock = {
      contarNaoLidas: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ContadorNotificacoesComponent],
      providers: [
        provideRouter([]),
        { provide: NotificacaoService, useValue: notificacaoServiceMock },
      ],
    }).compileComponents();
  });

  it('deve exibir a badge com contagem > 0', () => {
    notificacaoServiceMock.contarNaoLidas.mockReturnValue(of({ naoLidas: 3 }));
    fixture = TestBed.createComponent(ContadorNotificacoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const badge = fixture.debugElement.query(By.css('span.absolute'));
    expect(badge).toBeTruthy();
    expect(badge.nativeElement.textContent.trim()).toBe('3');
    expect(component.naoLidas()).toBe(3);
  });

  it('não deve exibir a badge com contagem 0, mas deve manter o aria-label correto', () => {
    notificacaoServiceMock.contarNaoLidas.mockReturnValue(of({ naoLidas: 0 }));
    fixture = TestBed.createComponent(ContadorNotificacoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    const badge = fixture.debugElement.query(By.css('span.absolute'));
    expect(badge).toBeNull();

    const link = fixture.debugElement.query(By.css('a'));
    expect(link.attributes['aria-label']).toBe('Nenhuma notificação não lida');
    expect(component.naoLidas()).toBe(0);
  });

  it('deve falhar silenciosamente em caso de erro de rede sem quebrar o componente', () => {
    notificacaoServiceMock.contarNaoLidas.mockReturnValue(
      throwError(() => new Error('Erro de rede'))
    );
    fixture = TestBed.createComponent(ContadorNotificacoesComponent);
    component = fixture.componentInstance;

    expect(() => fixture.detectChanges()).not.toThrow();
    expect(component.naoLidas()).toBe(0);
  });
});
