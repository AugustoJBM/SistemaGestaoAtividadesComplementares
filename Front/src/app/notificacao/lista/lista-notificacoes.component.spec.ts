import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { describe, it, expect, afterEach, vi } from 'vitest';
import { ListaNotificacoesComponent } from './lista-notificacoes.component';
import { NotificacaoService } from '../notificacao.service';
import { Notificacao } from '../notificacao.model';

const notificacoesMock: Notificacao[] = [
  {
    id: 1,
    tipo: 'SOLICITACAO_SUBMETIDA',
    titulo: 'Solicitação Enviada',
    mensagem: 'Sua solicitação de validação foi recebida.',
    solicitacaoId: 7,
    lida: false,
    dataCriacao: '2026-08-28T14:30:00',
  },
  {
    id: 2,
    tipo: 'SOLICITACAO_APROVADA',
    titulo: 'Solicitação Aprovada',
    mensagem: 'Todas as atividades foram homologadas.',
    solicitacaoId: null,
    lida: true,
    dataCriacao: '2026-08-25T11:00:00',
  },
];

function montar(duble: Partial<NotificacaoService>): ComponentFixture<ListaNotificacoesComponent> {
  TestBed.configureTestingModule({
    imports: [ListaNotificacoesComponent],
    providers: [provideRouter([]), { provide: NotificacaoService, useValue: duble }],
  });
  return TestBed.createComponent(ListaNotificacoesComponent);
}

describe('ListaNotificacoesComponent', () => {
  afterEach(() => TestBed.resetTestingModule());

  it('deve exibir o estado de carregando antes da resposta', () => {
    const fixture = montar({ listar: () => new Observable<Notificacao[]>(() => {}) });
    fixture.detectChanges();
    expect(fixture.componentInstance.carregando()).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Carregando notificações...');
  });

  it('deve renderizar a listagem de notificações com título, mensagem e data formatada', () => {
    const fixture = montar({ listar: () => of(notificacoesMock) });
    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent as string;
    expect(texto).toContain('Solicitação Enviada');
    expect(texto).toContain('Sua solicitação de validação foi recebida.');
    expect(texto).toContain('28/08/2026 às 14:30');
    expect(texto).toContain('Não lida');
  });

  it('deve exibir role="alert" em caso de erro na consulta', () => {
    const fixture = montar({
      listar: () => throwError(() => new Error('Falha ao carregar notificações.')),
    });
    fixture.detectChanges();

    const alerta = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alerta).toBeTruthy();
    expect(alerta.textContent).toContain('Falha ao carregar notificações.');
    expect(fixture.componentInstance.carregando()).toBe(false);
  });

  it('deve exibir empty state quando não houver notificações', () => {
    const fixture = montar({ listar: () => of([]) });
    fixture.detectChanges();

    expect(fixture.componentInstance.semNotificacoes()).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Você não possui notificações no momento.');
  });

  it('deve alternar o filtro e requisitar apenas não lidas ao backend', () => {
    const filtrosEnviados: (boolean | undefined)[] = [];
    const fixture = montar({
      listar: (apenasNaoLidas) => {
        filtrosEnviados.push(apenasNaoLidas);
        return of(notificacoesMock);
      },
    });
    fixture.detectChanges();

    const btnNaoLidas = fixture.nativeElement.querySelector(
      '[data-testid="filtro-nao-lidas"]',
    ) as HTMLButtonElement;
    btnNaoLidas.click();
    fixture.detectChanges();

    expect(filtrosEnviados).toEqual([undefined, true]);
    expect(fixture.componentInstance.apenasNaoLidas()).toBe(true);
  });

  it('deve marcar notificação individual como lida e atualizar o estado local', () => {
    const spyMarcar = vi.fn().mockReturnValue(of({ ...notificacoesMock[0], lida: true }));
    const fixture = montar({
      listar: () => of(notificacoesMock),
      marcarComoLida: spyMarcar,
    });
    fixture.detectChanges();

    fixture.componentInstance.marcarComoLida(notificacoesMock[0]);
    fixture.detectChanges();

    expect(spyMarcar).toHaveBeenCalledWith(1);
    const itemAtualizado = fixture.componentInstance.notificacoes().find((n) => n.id === 1);
    expect(itemAtualizado?.lida).toBe(true);
  });

  it('deve marcar todas como lidas e zerar não lidas na tela', () => {
    const spyMarcarTodas = vi.fn().mockReturnValue(of(void 0));
    const fixture = montar({
      listar: () => of(notificacoesMock),
      marcarTodasComoLidas: spyMarcarTodas,
    });
    fixture.detectChanges();

    const btnTodas = fixture.nativeElement.querySelector(
      '[data-testid="btn-marcar-todas"]',
    ) as HTMLButtonElement;
    btnTodas.click();
    fixture.detectChanges();

    expect(spyMarcarTodas).toHaveBeenCalled();
    const naoLidas = fixture.componentInstance.notificacoes().filter((n) => !n.lida);
    expect(naoLidas.length).toBe(0);
  });

  it('deve exibir link para /solicitacoes quando solicitacaoId estiver preenchido e ocultar quando nulo', () => {
    const fixture = montar({ listar: () => of(notificacoesMock) });
    fixture.detectChanges();

    const links = fixture.nativeElement.querySelectorAll('a[href="/solicitacoes"]');
    expect(links.length).toBe(1);
    expect(links[0].textContent).toContain('Ver solicitação');
  });
});
