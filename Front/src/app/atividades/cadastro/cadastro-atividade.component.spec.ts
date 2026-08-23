import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { CadastroAtividadeComponent } from './cadastro-atividade.component';
import { AtividadeService } from '../atividade.service';
import { API_BASE_URL } from '../../api.config';

const ATIVIDADES_URL = `${API_BASE_URL}/atividades`;

describe('CadastroAtividadeComponent', () => {
    let component: CadastroAtividadeComponent;
    let fixture: ComponentFixture<CadastroAtividadeComponent>;
    let atividadeService: AtividadeService;
    let httpMock: HttpTestingController;

    beforeEach(async () => {
        TestBed.resetTestingModule();
        await TestBed.configureTestingModule({
            imports: [CadastroAtividadeComponent],
            providers: [
                AtividadeService,
                provideHttpClient(),
                provideHttpClientTesting(),
                provideRouter([])
            ]
        }).compileComponents();
        fixture = TestBed.createComponent(CadastroAtividadeComponent);
        component = fixture.componentInstance;
        atividadeService = TestBed.inject(AtividadeService);
        httpMock = TestBed.inject(HttpTestingController);
        fixture.detectChanges();
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('deve criar o componente', () => {
        expect(component).toBeTruthy();
    });

    it('deve iniciar com formulário inválido e botão de envio desabilitado', () => {
        expect(component.activityForm.valid).toBeFalsy();
        expect(component.isFormularioInvalido()).toBeTruthy();
    });

    it('deve autopreencher os campos ao selecionar arquivo com IA', () => {
        const arquivo = new File(['conteudo'], 'certificado.pdf', { type: 'application/pdf' });
        const extracaoMock = {
            titulo: 'Curso de IA Aplicada',
            instituicaoResponsavel: 'UFAPE',
            dataRealizacao: '2026-04-10',
            cargaHoraria: 40,
            natureza: 'ACC',
            categoria: 'PESQUISA'
        };

        vi.spyOn(atividadeService, 'extrairDadosCertificado').mockReturnValue(of(extracaoMock as any));

        component.aoSelecionarArquivoComIA({ target: { files: [arquivo] } } as unknown as Event);

        expect(component.arquivoAnexado()).toEqual(arquivo);
        expect(component.activityForm.value.titulo).toBe('Curso de IA Aplicada');
        expect(component.activityForm.value.instituicao).toBe('UFAPE');
        expect(component.activityForm.value.cargaHoraria).toBe(40);
        expect(component.extraindoComIA()).toBe(false);
    });

    it('deve manter o arquivo anexado mesmo se a extração de IA falhar', () => {
        const arquivo = new File(['conteudo'], 'certificado.pdf', { type: 'application/pdf' });

        vi.spyOn(atividadeService, 'extrairDadosCertificado').mockReturnValue(
            throwError(() => new Error('Falha de IA'))
        );

        component.aoSelecionarArquivoComIA({ target: { files: [arquivo] } } as unknown as Event);

        expect(component.arquivoAnexado()).toEqual(arquivo);
        expect(component.erroExtracao()).toContain('Não foi possível extrair os dados');
        expect(component.extraindoComIA()).toBe(false);
    });

    it('deve rejeitar arquivos com formato não permitido', () => {
        const arquivoInvalido = new File(['dummy content'], 'teste.exe', { type: 'application/x-msdownload' });
        const event = { target: { files: [arquivoInvalido] } } as unknown as Event;
        component.onFileSelected(event);
        expect(component.arquivoAnexado()).toBeNull();
        expect(component.erroArquivo()).toContain('Tipo de arquivo');
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
            categoria: 'PESQUISA',
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
            categoria: 'EXTENSAO',
            cargaHoraria: '10'
        });
        const arquivoValido = new File(['dummy content'], 'certificado.jpg', { type: 'image/jpeg' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);
        component.onSubmit();
        const req = httpMock.expectOne(ATIVIDADES_URL);
        expect(req.request.method).toBe('POST');
        req.flush({
            id: 1,
            titulo: 'Minicurso Python',
            instituicaoResponsavel: 'Sebrae',
            dataRealizacao: '2026-06-01',
            cargaHorariaEmHoras: 10,
            natureza: 'ACEX',
            categoria: 'EXTENSAO'
        });
        expect(component.mensagemSucesso()).toBeTruthy();
        expect(component.activityForm.get('titulo')?.value).toBeNull();
        expect(component.arquivoAnexado()).toBeNull();
        expect(component.carregando()).toBeFalsy();
    });

    it('deve exibir mensagem de erro devolvida pela API em caso de falha 400', () => {
        component.activityForm.setValue({
            titulo: 'Projeto de Pesquisa',
            instituicao: 'UFAPE',
            data: '2026-01-15',
            natureza: 'ACC',
            categoria: 'PESQUISA',
            cargaHoraria: '100'
        });
        const arquivoValido = new File(['dummy content'], 'comprovante.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        component.onSubmit();

        const req = httpMock.expectOne(ATIVIDADES_URL);
        req.flush('Certificado inválido. Aceitos: PDF, PNG ou JPEG', {
            status: 400,
            statusText: 'Bad Request'
        });

        expect(component.mensagemErro()).toBe('Certificado inválido. Aceitos: PDF, PNG ou JPEG');
        expect(component.carregando()).toBeFalsy();
    });

    it('deve exibir mensagem genérica amigável em caso de erro 500 do servidor', () => {
        component.activityForm.setValue({
            titulo: 'Projeto de Pesquisa',
            instituicao: 'UFAPE',
            data: '2026-01-15',
            natureza: 'ACC',
            categoria: 'PESQUISA',
            cargaHoraria: '20'
        });
        const arquivoValido = new File(['dummy content'], 'comprovante.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        component.onSubmit();

        const req = httpMock.expectOne(ATIVIDADES_URL);
        req.flush('', { status: 500, statusText: 'Internal Server Error' });

        expect(component.mensagemErro()).toBe('Não foi possível cadastrar a atividade. Tente novamente.');
        expect(component.carregando()).toBeFalsy();
    });

    it('deve exibir mensagem de indisponibilidade quando houver erro de rede (status 0)', () => {
        component.activityForm.setValue({
            titulo: 'Projeto de Pesquisa',
            instituicao: 'UFAPE',
            data: '2026-01-15',
            natureza: 'ACC',
            categoria: 'PESQUISA',
            cargaHoraria: '20'
        });
        const arquivoValido = new File(['dummy content'], 'comprovante.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [arquivoValido] } } as unknown as Event);

        component.onSubmit();

        const req = httpMock.expectOne(ATIVIDADES_URL);
        req.error(new ProgressEvent('error'), { status: 0 });

        expect(component.mensagemErro()).toBe('Não foi possível conectar ao servidor. Verifique sua conexão.');
        expect(component.carregando()).toBeFalsy();
    });
});