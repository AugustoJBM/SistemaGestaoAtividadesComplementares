import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { describe, it, expect, beforeEach } from 'vitest';
import { LandingComponent } from './landing.component';

describe('LandingComponent', () => {
    let component: LandingComponent;
    let fixture: ComponentFixture<LandingComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [LandingComponent],
            providers: [provideRouter([])]
        }).compileComponents();

        fixture = TestBed.createComponent(LandingComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('deve ser criado com sucesso', () => {
        expect(component).toBeTruthy();
    });

    it('deve conter as modalidades ACC e ACEX com suas respectivas cargas horárias exigidas', () => {
        const texto = fixture.nativeElement.textContent as string;
        expect(texto).toContain('ACC');
        expect(texto).toContain('90h');
        expect(texto).toContain('ACEX');
        expect(texto).toContain('320h');
    });

    it('deve conter as etapas do ciclo de validação de certificados', () => {
        const texto = fixture.nativeElement.textContent as string;
        expect(texto).toContain('Cadastro e Upload');
        expect(texto).toContain('Análise Institucional');
        expect(texto).toContain('Cálculo e Integralização');
        expect(texto).toContain('Emissão Institucional');
    });

    it('deve conter links de navegação para login e registro', () => {
        const linksLogin = fixture.nativeElement.querySelectorAll('a[href="/login"]');
        const linksRegistro = fixture.nativeElement.querySelectorAll('a[href="/registro"]');
        expect(linksLogin.length).toBeGreaterThan(0);
        expect(linksRegistro.length).toBeGreaterThan(0);
    });
});