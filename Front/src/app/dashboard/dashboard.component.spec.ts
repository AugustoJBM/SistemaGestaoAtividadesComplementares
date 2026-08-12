import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach } from 'vitest';

import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente com sucesso', () => {
    expect(component).toBeTruthy();
  });

  it('deve renderizar o título Dashboard', () => {
    const elemento: HTMLElement = fixture.nativeElement;
    const titulo = elemento.querySelector('h1');

    expect(titulo?.textContent).toContain('Dashboard');
  });

  it('deve expor um link de saída apontando para /logout', () => {
    const elemento: HTMLElement = fixture.nativeElement;
    const link = elemento.querySelector('a[routerlink="/logout"]');

    expect(link).toBeTruthy();
  });

  it('deve dar acesso à tela de acompanhamento da carga horária', () => {
    const elemento: HTMLElement = fixture.nativeElement;
    const link = elemento.querySelector('a[routerlink="/progresso"]');

    expect(link).toBeTruthy();
  });
});
