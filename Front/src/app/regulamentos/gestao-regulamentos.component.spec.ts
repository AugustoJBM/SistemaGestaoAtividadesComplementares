import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { GestaoRegulamentosComponent } from './gestao-regulamentos.component';
import { RegulamentoService, RegulamentoChunk, IngestaoNormativaResponse } from './regulamento.service';

const chunksMock: RegulamentoChunk[] = [
    { id: 1, artigo: 'Art. 12', conteudoTexto: 'Monitoria vale até 40h' }
];

const ingestaoSucesso: IngestaoNormativaResponse = {
    nomeDocumento: 'regulamento.pdf',
    totalChunksExtraidos: 3,
    status: 'SUCESSO',
    mensagem: '3 normas extraídas com sucesso.'
};

describe('GestaoRegulamentosComponent', () => {
    let component: GestaoRegulamentosComponent;
    let fixture: ComponentFixture<GestaoRegulamentosComponent>;
    let serviceSpy: {
        listarRegulamentos: ReturnType<typeof vi.fn>;
        ingerirDocumento: ReturnType<typeof vi.fn>;
    };

    beforeEach(async () => {
        serviceSpy = {
            listarRegulamentos: vi.fn().mockReturnValue(of(chunksMock)),
            ingerirDocumento: vi.fn().mockReturnValue(of(ingestaoSucesso))
        };

        await TestBed.configureTestingModule({
            imports: [GestaoRegulamentosComponent],
            providers: [
                provideRouter([]),
                { provide: RegulamentoService, useValue: serviceSpy }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(GestaoRegulamentosComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('deve criar o componente e carregar as normas ativas', () => {
        expect(component).toBeTruthy();
        expect(serviceSpy.listarRegulamentos).toHaveBeenCalled();
        expect(component.regrasAtivas().length).toBe(1);
        expect(fixture.nativeElement.textContent).toContain('Art. 12');
    });

    it('deve selecionar um arquivo válido PDF/TXT', () => {
        const file = new File(['texto'], 'norma.pdf', { type: 'application/pdf' });
        component.aoSelecionarArquivo({ target: { files: [file] } } as unknown as Event);

        expect(component.arquivoSelecionado()).toEqual(file);
        expect(component.mensagemErro()).toBeNull();
    });

    it('deve rejeitar arquivo com formato inválido', () => {
        const file = new File(['exe'], 'virus.exe', { type: 'application/x-msdownload' });
        component.aoSelecionarArquivo({ target: { files: [file] } } as unknown as Event);

        expect(component.arquivoSelecionado()).toBeNull();
        expect(component.mensagemErro()).toContain('Formato de arquivo inválido');
    });

    it('deve processar o documento selecionado e recarregar a lista', () => {
        const file = new File(['texto'], 'norma.pdf', { type: 'application/pdf' });
        component.aoSelecionarArquivo({ target: { files: [file] } } as unknown as Event);

        component.processarDocumento();

        expect(serviceSpy.ingerirDocumento).toHaveBeenCalledWith(file, false);
        expect(component.resultado()).toEqual(ingestaoSucesso);
        expect(component.processando()).toBe(false);
    });
});