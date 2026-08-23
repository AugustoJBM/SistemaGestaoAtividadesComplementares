import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, it, expect, beforeEach } from 'vitest';
import { ParecerCardComponent } from './parecer-card.component';
import { ParecerResponseDTO } from '../../atividade.model';

const parecerMock: ParecerResponseDTO = {
    id: 1,
    atividadeId: 10,
    naturezaSugerida: 'ACC',
    categoriaSugerida: 'ENSINO',
    cargaHorariaAproveitavel: 40,
    artigoRegulamento: 'Art. 12',
    justificativaTecnica: 'Atividade de monitoria válida conforme regulamento.',
    scoreConfianca: 0.95,
    decisaoIA: 'DEFERIDO',
    tempoProcessamentoMs: 120
};

describe('ParecerCardComponent', () => {
    let component: ParecerCardComponent;
    let fixture: ComponentFixture<ParecerCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ParecerCardComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(ParecerCardComponent);
        component = fixture.componentInstance;
        fixture.componentRef.setInput('parecer', parecerMock);
        fixture.detectChanges();
    });

    it('deve criar o componente com sucesso', () => {
        expect(component).toBeTruthy();
    });

    it('deve renderizar os dados do parecer corretamente', () => {
        const elemento = fixture.nativeElement as HTMLElement;
        expect(elemento.textContent).toContain('DEFERIDO');
        expect(elemento.textContent).toContain('Art. 12');
        expect(elemento.textContent).toContain('40h (Teto)');
        expect(elemento.textContent).toContain('95%');
        expect(elemento.textContent).toContain('Atividade de monitoria válida');
    });
});