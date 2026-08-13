import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import { CadastroAtividadeComponent } from './cadastro-atividade.component';
import { AtividadeService } from '../atividade.service';
import { ComponentFixture, TestBed } from '@angular/core/testing';

describe('CadastroAtividadeComponent', () => {
    let component: CadastroAtividadeComponent;
    let fixture: ComponentFixture<CadastroAtividadeComponent>;
    let atividadeServiceSpy: { cadastrar: ReturnType<typeof vi.fn> };

    beforeEach(async () => {
        atividadeServiceSpy = { cadastrar: vi.fn(() => of({})) };

        await TestBed.configureTestingModule({
            imports: [CadastroAtividadeComponent],
            providers: [
                provideRouter([]),
                { provide: AtividadeService, useValue: atividadeServiceSpy }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(CadastroAtividadeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('deve criar o componente', () => {
        expect(component).toBeTruthy();
    });

    it('deve iniciar com formulário inválido e botão de envio desabilitado', () => {
        expect(component.activityForm.valid).toBeFalsy();
        expect(component.isFormularioInvalido()).toBeTruthy();
    });

    it('deve rejeitar arquivos com formato não permitido', () => {
        const arquivoInvalido = new File(['dummy content'], 'teste.exe', { type: 'application/x-msdownload' });
        const event = { target: { files: [arquivoInvalido] } } as unknown as Event;

        component.onFileSelected(event);

        expect(component.arquivoAnexado()).toBeNull();
        expect(component.erroArquivo()).toContain('Tipo de arquivo inválido');
    });

    it('deve aceitar arquivo PDF dentro do limite de tamanho', () => {
        const arquivoValido = new File(['dummy content'], 'certificado.pdf', { type: 'application/pdf' });
        const event = { target: { files: [arquivoValido] } } as unknown as Event;

        component.onFileSelected(event);

        expect(component.arquivoAnexado()).toEqual(arquivoValido);
        expect(component.erroArquivo()).toBeNull();
    });

    it('deve habilitar o formulário quando todos os campos obrigatórios e comprovante forem preenchidos', () => {
        component.activityForm.setValue({
            titulo: 'Minicurso de Python para Análise de Dados',
            instituicao: 'UFAPE',
            data: '2026-05-10',
            natureza: 'ACC',
            categoria: 'cursos',
            cargaHoraria: '20'
        });

        const arquivoValido = new File(['dummy content'], 'certificado.png', { type: 'image/png' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        expect(component.activityForm.valid).toBeTruthy();
        expect(component.isFormularioInvalido()).toBeFalsy();
    });

    it('deve enviar o formulário e limpar os campos em caso de sucesso', () => {
        component.activityForm.setValue({
            titulo: 'Minicurso Python',
            instituicao: 'Sebrae',
            data: '2026-06-01',
            natureza: 'ACEX',
            categoria: 'palestras',
            cargaHoraria: '10'
        });

        const arquivoValido = new File(['dummy content'], 'certificado.jpg', { type: 'image/jpeg' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        component.onSubmit();

        expect(atividadeServiceSpy.cadastrar).toHaveBeenCalled();
        expect(component.mensagemSucesso()).toBeTruthy();
        expect(component.activityForm.get('titulo')?.value).toBeNull();
        expect(component.arquivoAnexado()).toBeNull();
    });

    it('deve exibir mensagem de erro devolvida pela API em caso de falha', () => {
        atividadeServiceSpy.cadastrar.mockReturnValue(
            throwError(() => new Error('A carga horária informada excede o limite da categoria.'))
        );

        component.activityForm.setValue({
            titulo: 'Projeto de Pesquisa',
            instituicao: 'UFAPE',
            data: '2026-01-15',
            natureza: 'ACC',
            categoria: 'pesquisa',
            cargaHoraria: '100'
        });

        const arquivoValido = new File(['dummy content'], 'comprovante.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        component.onSubmit();

        expect(component.mensagemErro()).toBe('A carga horária informada excede o limite da categoria.');
        expect(component.carregando()).toBeFalsy();
    });
});