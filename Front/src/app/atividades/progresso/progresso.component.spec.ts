import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, Subject, of, throwError } from 'rxjs';
import { describe, it, expect } from 'vitest';

import { ProgressoComponent } from './progresso.component';
import { ProgressoCargaHoraria } from './progresso.model';
import { ProgressoService } from './progresso.service';

const progressoComAtividades: ProgressoCargaHoraria = {
  acc: {
    horasAcumuladas: 30,
    horasPendentes: 0,
    horasExigidas: 60,
    horasRestantes: 30,
    percentualConcluido: 50
  },
  acex: {
    horasAcumuladas: 20,
    horasPendentes: 0,
    horasExigidas: 40,
    horasRestantes: 20,
    percentualConcluido: 50
  }
};

const progressoSemAtividades: ProgressoCargaHoraria = {
  acc: {
    horasAcumuladas: 0,
    horasPendentes: 0,
    horasExigidas: 60,
    horasRestantes: 60,
    percentualConcluido: 0
  },
  acex: {
    horasAcumuladas: 0,
    horasPendentes: 0,
    horasExigidas: 40,
    horasRestantes: 40,
    percentualConcluido: 0
  }
};

describe('ProgressoComponent', () => {
  let fixture: ComponentFixture<ProgressoComponent>;
  let progressoServiceDuble: { obterProgresso: () => Observable<ProgressoCargaHoraria> };

  const configurarComponente = async (): Promise<void> => {
    await TestBed.configureTestingModule({
      imports: [ProgressoComponent],
      providers: [
        provideRouter([]),
        { provide: ProgressoService, useValue: progressoServiceDuble }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgressoComponent);
  };

  it('deve exibir o estado de carregando antes da resposta do service', async () => {
    const progressoNaoResolvido = new Subject<ProgressoCargaHoraria>();
    progressoServiceDuble = { obterProgresso: () => progressoNaoResolvido.asObservable() };
    await configurarComponente();

    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent as string;
    expect(fixture.componentInstance.carregando()).toBeTruthy();
    expect(texto).toContain('Carregando seu progresso...');
  });

  it('deve renderizar horas e percentual de ACC e ACEX após sucesso', async () => {
    progressoServiceDuble = { obterProgresso: () => of(progressoComAtividades) };
    await configurarComponente();

    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent as string;
    expect(fixture.componentInstance.carregando()).toBeFalsy();
    expect(texto).toContain('ACC');
    expect(texto).toContain('ACEX');
    expect(texto).toContain('30h');
    expect(texto).toContain('60h');
    expect(texto).toContain('50% concluído');
  });

  it('deve exibir a mensagem de erro devolvida pelo service quando a chamada falha', async () => {
    progressoServiceDuble = {
      obterProgresso: () => throwError(() => new Error('Não foi possível carregar seu progresso. Tente novamente.'))
    };
    await configurarComponente();

    fixture.detectChanges();

    const alerta = fixture.nativeElement.querySelector('[role="alert"]') as HTMLElement;
    expect(fixture.componentInstance.carregando()).toBeFalsy();
    expect(alerta.textContent).toContain('Não foi possível carregar seu progresso. Tente novamente.');
  });

  it('deve exibir 0h e 0% para estudante sem atividades sem quebrar a tela', async () => {
    progressoServiceDuble = { obterProgresso: () => of(progressoSemAtividades) };
    await configurarComponente();

    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent as string;
    const cards = fixture.nativeElement.querySelectorAll('[role="progressbar"]');
    expect(fixture.componentInstance.semAtividades()).toBeTruthy();
    expect(cards.length).toBe(2);
    expect(texto).toContain('0h');
    expect(texto).toContain('0% concluído');
  });
});
