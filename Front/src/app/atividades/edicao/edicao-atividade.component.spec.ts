import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { EdicaoAtividadeComponent } from './edicao-atividade.component';
import { AtividadeService } from '../atividade.service';
import { Atividade, Categoria, Natureza } from '../atividade.model';

const atividadeMock: Atividade = {
    id: 1,
    titulo: 'Monitoria de Algoritmos',
    instituicaoResponsavel: 'UFAPE',
    dataRealizacao: '2026-03-10',
    cargaHorariaEmHoras: 30,
    natureza: 'ACC',
    categoria: 'ENSINO',
    dataCadastro: '2026-03-11T08:00:00'
};

describe('EdicaoAtividadeComponent', () => {
    let component: EdicaoAtividadeComponent;
    let fixture: ComponentFixture<EdicaoAtividadeComponent>;
    let atividadeService: AtividadeService;
    let router: Router;

    beforeEach(async () => {
        TestBed.resetTestingModule();
        await TestBed.configureTestingModule({
            imports: [EdicaoAtividadeComponent],
            providers: [
                AtividadeService,
                provideHttpClient(),
                provideHttpClientTesting(),
                provideRouter([]),
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            paramMap: {
                                get: (key: string) => (key === 'id' ? '1' : null)
                            }
                        }
                    }
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(EdicaoAtividadeComponent);
        component = fixture.componentInstance;
        atividadeService = TestBed.inject(AtividadeService);
        router = TestBed.inject(Router);
    });

    it('deve listar o certificado cadastrado atual com a tag Atual', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));

        fixture.detectChanges();

        expect(component.carregandoDados()).toBeFalsy();
        expect(component.temCertificadoValido()).toBeTruthy();

        const texto = fixture.nativeElement.textContent as string;
        expect(texto).toContain('Certificado Cadastrado');
        expect(texto).toContain('Atual');
    });

    it('deve abrir modal de visualização ao clicar em visualizar o certificado atual', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        const dummyBlob = new Blob(['pdf-content'], { type: 'application/pdf' });
        vi.spyOn(atividadeService, 'obterCertificado').mockReturnValue(of(dummyBlob));

        fixture.detectChanges();

        component.visualizarCertificadoAtual();
        fixture.detectChanges();

        expect(component.modalVisualizacaoAberto()).toBeTruthy();
        expect(component.tipoPrevia()).toBe('pdf');
        expect(component.urlPrevia()).toBeTruthy();
    });

    it('deve exibir mensagem de erro dentro do modal caso o certificado atual falhe ao carregar', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        vi.spyOn(atividadeService, 'obterCertificado').mockReturnValue(
            throwError(() => new Error('Falha'))
        );

        fixture.detectChanges();

        component.visualizarCertificadoAtual();
        fixture.detectChanges();

        expect(component.modalVisualizacaoAberto()).toBeTruthy();
        expect(component.erroPrevia()).toContain('Não foi possível carregar o arquivo');
    });

    it('deve abrir modal de visualização para novo arquivo selecionado', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        fixture.detectChanges();

        const novoArquivo = new File(['conteudo'], 'novo.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [novoArquivo] } } as unknown as Event);

        component.visualizarNovoArquivo();
        fixture.detectChanges();

        expect(component.modalVisualizacaoAberto()).toBeTruthy();
        expect(component.tituloPrevia()).toBe('novo.pdf');
    });

    it('deve fechar o modal de visualização ao acionar fecharModalVisualizacao', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        fixture.detectChanges();

        const novoArquivo = new File(['conteudo'], 'novo.pdf', { type: 'application/pdf' });
        component.onFileSelected({ target: { files: [novoArquivo] } } as unknown as Event);
        component.visualizarNovoArquivo();
        expect(component.modalVisualizacaoAberto()).toBeTruthy();

        component.fecharModalVisualizacao();
        expect(component.modalVisualizacaoAberto()).toBeFalsy();
        expect(component.urlPrevia()).toBeNull();
    });

    it('deve permitir remover o certificado atual e exigir novo upload antes de submeter', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        const spyAtualizar = vi.spyOn(atividadeService, 'atualizar');

        fixture.detectChanges();

        component.removerCertificadoAtual();
        fixture.detectChanges();

        expect(component.certificadoAtualRemovido()).toBeTruthy();
        expect(component.temCertificadoValido()).toBeFalsy();
        expect(component.isFormularioInvalido()).toBeTruthy();

        component.onSubmit();
        expect(spyAtualizar).not.toHaveBeenCalled();
        expect(component.erroArquivo()).toContain('O comprovante é obrigatório');
    });

    it('deve submeter com sucesso quando o certificado atual é mantido', () => {
        vi.spyOn(atividadeService, 'buscarPorId').mockReturnValue(of(atividadeMock));
        const spyAtualizar = vi.spyOn(atividadeService, 'atualizar').mockReturnValue(of({} as any));

        fixture.detectChanges();
        component.onSubmit();

        expect(spyAtualizar).toHaveBeenCalledWith(1, {
            titulo: 'Monitoria de Algoritmos',
            instituicaoResponsavel: 'UFAPE',
            dataRealizacao: '2026-03-10',
            natureza: Natureza.ACC,
            categoria: Categoria.ENSINO,
            cargaHoraria: 30,
            arquivo: null
        });
        expect(component.mensagemSucesso()).toBeTruthy();
    });
});