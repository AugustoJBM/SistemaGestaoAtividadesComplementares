import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { DashboardComponent } from './dashboard.component';
import { ProgressoService } from '../atividades/progresso/progresso.service';
import { ProgressoCargaHorariaDTO } from '../atividades/progresso/progresso.model';
import { API_BASE_URL } from '../api.config';

const PROGRESSO_URL = `${API_BASE_URL}/atividades/progresso`;

// Payloads no formato cru do backend: exercitam tambem o mapeamento
// DTO -> dominio feito pelo ProgressoService (horasRestantes derivada, ?? 0).
const dtoCompleto: ProgressoCargaHorariaDTO = {
  acc: { horasAcumuladas: 30, horasPendentes: 5, horasExigidas: 60, percentualConcluido: 50 },
  acex: { horasAcumuladas: 20, horasPendentes: 0, horasExigidas: 40, percentualConcluido: 50 },
};

const dtoForaDaFaixa: ProgressoCargaHorariaDTO = {
  acc: { horasAcumuladas: 30, horasPendentes: 0, horasExigidas: 60, percentualConcluido: 140 },
  acex: { horasAcumuladas: 20, horasPendentes: 0, horasExigidas: 40, percentualConcluido: -20 },
};

describe('DashboardComponent', () => {
  let fixture: ComponentFixture<DashboardComponent>;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    // Isola este spec de vazamento de TestBed deixado por specs anteriores na
    // mesma execução (ver issue #62); sem isso a ordem de execução contamina.
    TestBed.resetTestingModule();

    TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        ProgressoService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });

    fixture = TestBed.createComponent(DashboardComponent);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // Dispara o ngOnInit e, com ele, a requisicao de progresso.
  function iniciar(): void {
    fixture.detectChanges();
  }

  function responderCom(dto: ProgressoCargaHorariaDTO): void {
    httpMock.expectOne(PROGRESSO_URL).flush(dto);
    fixture.detectChanges();
  }

  it('deve criar o componente com sucesso', () => {
    // Arrange & Act: componente criado no beforeEach, sem disparar ngOnInit.
    // Assert
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('deve renderizar o título Dashboard', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    expect(elemento.querySelector('h1')?.textContent).toContain('Dashboard');
  });

  it('deve exibir o spinner enquanto o progresso está carregando', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    const requisicao = httpMock.expectOne(PROGRESSO_URL);

    // Assert
    expect(fixture.componentInstance.carregando()).toBe(true);
    expect(elemento.querySelector('svg.animate-spin')).toBeTruthy();
    expect(elemento.textContent).toContain('Carregando seu progresso');

    requisicao.flush(dtoCompleto);
  });

  it('deve exibir o resumo de ACC e ACEX mapeado a partir do payload da API', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    const titulos = Array.from(elemento.querySelectorAll('h2')).map((h) => h.textContent?.trim());
    expect(fixture.componentInstance.carregando()).toBe(false);
    expect(titulos).toContain('ACC');
    expect(titulos).toContain('ACEX');
    expect(elemento.textContent).toContain('de 60h exigidas');
    expect(elemento.textContent).toContain('de 40h exigidas');
    // horasRestantes nao vem no payload: e derivada pelo service.
    expect(elemento.textContent).toContain('Faltam 30h');
    expect(elemento.textContent).toContain('Faltam 20h');
  });

  it('deve sinalizar as horas pendentes quando existirem', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    expect(elemento.textContent).toContain('5h em análise');
  });

  it('deve informar quando o estudante ainda não possui atividades', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act: payload sem modalidades, como o backend devolve para quem nao registrou nada.
    iniciar();
    responderCom({ acc: null, acex: null });

    // Assert
    expect(fixture.componentInstance.semAtividades()).toBe(true);
    expect(elemento.textContent).toContain('ainda não possui atividades registradas');
  });

  it('deve limitar o percentual exibido a 0..100 mesmo com valores fora da faixa', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoForaDaFaixa);

    // Assert
    const barras = Array.from(elemento.querySelectorAll('[role="progressbar"]'));
    expect(barras.map((b) => b.getAttribute('aria-valuenow'))).toEqual(['100', '0']);
    expect(elemento.textContent).not.toContain('140%');
    expect(elemento.textContent).not.toContain('-20%');
  });

  it('deve rotular cada barra de progresso com a modalidade correspondente', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    const rotulos = Array.from(elemento.querySelectorAll('[role="progressbar"]')).map((b) =>
      b.getAttribute('aria-label'),
    );
    expect(rotulos).toEqual(['Progresso de ACC', 'Progresso de ACEX']);
  });

  it('deve exibir a mensagem de erro traduzida pelo service quando a sessão expira', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    httpMock.expectOne(PROGRESSO_URL).flush({}, { status: 401, statusText: 'Unauthorized' });
    fixture.detectChanges();

    // Assert
    const alerta = elemento.querySelector('[role="alert"]');
    expect(fixture.componentInstance.carregando()).toBe(false);
    expect(alerta?.textContent).toContain('Sessão expirada. Faça login novamente.');
    expect(elemento.querySelector('svg.animate-spin')).toBeFalsy();
  });

  it('deve refazer a busca ao clicar em tentar novamente', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;
    iniciar();
    httpMock.expectOne(PROGRESSO_URL).error(new ProgressEvent('error'), { status: 0 });
    fixture.detectChanges();
    expect(elemento.textContent).toContain('Não foi possível conectar ao servidor');

    // Act
    elemento.querySelector('button')?.click();
    responderCom(dtoCompleto);

    // Assert
    expect(fixture.componentInstance.mensagemErro()).toBeNull();
    expect(elemento.textContent).toContain('de 60h exigidas');
  });

  it('deve dar acesso à tela de cadastro de atividade', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    expect(elemento.querySelector('a[routerlink="/atividades/cadastro"]')).toBeTruthy();
  });

  it('deve dar acesso à tela de acompanhamento da carga horária', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    expect(elemento.querySelector('a[routerlink="/progresso"]')).toBeTruthy();
  });

  it('deve expor um link de saída apontando para /logout', () => {
    // Arrange
    const elemento: HTMLElement = fixture.nativeElement;

    // Act
    iniciar();
    responderCom(dtoCompleto);

    // Assert
    expect(elemento.querySelector('a[routerlink="/logout"]')).toBeTruthy();
  });
});
