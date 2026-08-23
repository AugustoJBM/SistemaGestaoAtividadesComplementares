import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    FormBuilder,
    FormGroup,
    ReactiveFormsModule,
    Validators
} from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AtividadeService } from '../atividade.service';
import { AtividadeRequest } from '../atividade.model';

const FORMATOS_PERMITIDOS = [
    'application/pdf',
    'image/png',
    'image/jpeg',
    'image/jpg'
];

const TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024;

@Component({
    selector: 'app-cadastro-atividade',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './cadastro-atividade.component.html'
})
export class CadastroAtividadeComponent {

    private readonly fb = inject(FormBuilder);
    private readonly atividadeService = inject(AtividadeService);

    // ============================================================
    // ESTADOS
    // ============================================================

    readonly carregando = signal(false);

    readonly mensagemErro = signal<string | null>(null);

    readonly mensagemSucesso = signal(false);

    readonly arquivoAnexado = signal<File | null>(null);

    readonly erroArquivo = signal<string | null>(null);

    readonly extraindoComIA = signal(false);

    readonly erroExtracao = signal<string | null>(null);

    readonly dragOver = signal(false);

    // ============================================================
    // FORMULÁRIO
    // ============================================================

    /**
     * Os nomes dos campos abaixo precisam permanecer iguais aos
     * formControlName utilizados pelo HTML.
     */
    readonly activityForm: FormGroup = this.fb.group({

        titulo: [
            '',
            [Validators.required]
        ],

        instituicao: [
            ''
        ],

        data: [
            '',
            [Validators.required]
        ],

        natureza: [
            '',
            [Validators.required]
        ],

        categoria: [
            '',
            [Validators.required]
        ],

        cargaHoraria: [
            '',
            [
                Validators.required,
                Validators.min(1)
            ]
        ]
    });

    // ============================================================
    // VALIDAÇÃO
    // ============================================================

    isCampoInvalido(nomeCampo: string): boolean {

        const campo = this.activityForm.get(nomeCampo);

        return !!(
            campo &&
            campo.invalid &&
            (campo.touched || campo.dirty)
        );
    }

    isFormularioInvalido(): boolean {

        return (
            this.activityForm.invalid ||
            !this.arquivoAnexado() ||
            this.carregando() ||
            this.extraindoComIA()
        );
    }

    // ============================================================
    // SELEÇÃO DE ARQUIVO
    // ============================================================

    onFileSelected(event: Event): void {

        const input = event.target as HTMLInputElement;

        if (!input.files || input.files.length === 0) {
            return;
        }

        this.validarEProcessarArquivo(input.files[0]);
    }

    /**
     * Seleciona o certificado e tenta preencher os campos
     * automaticamente utilizando IA.
     */
    aoSelecionarArquivoComIA(event: Event): void {

        const input = event.target as HTMLInputElement;

        if (!input.files || input.files.length === 0) {
            return;
        }

        const arquivo = input.files[0];

        // Primeiro valida e anexa o arquivo.
        const arquivoValido = this.validarEProcessarArquivo(arquivo);

        if (!arquivoValido) {
            input.value = '';
            return;
        }

        // Depois tenta extrair os dados.
        this.extrairDadosComIA(arquivo);

        // Permite selecionar o mesmo arquivo novamente.
        input.value = '';
    }

    /**
     * Seleção manual normal.
     */
    aoSelecionarArquivo(event: Event): void {

        const input = event.target as HTMLInputElement;

        if (!input.files || input.files.length === 0) {
            return;
        }

        this.validarEProcessarArquivo(input.files[0]);

        input.value = '';
    }

    // ============================================================
    // DRAG AND DROP
    // ============================================================

    onDragOver(event: DragEvent): void {

        event.preventDefault();
        event.stopPropagation();

        this.dragOver.set(true);
    }

    onDragLeave(event: DragEvent): void {

        event.preventDefault();
        event.stopPropagation();

        this.dragOver.set(false);
    }

    onDrop(event: DragEvent): void {

        event.preventDefault();
        event.stopPropagation();

        this.dragOver.set(false);

        const files = event.dataTransfer?.files;

        if (!files || files.length === 0) {
            return;
        }

        this.validarEProcessarArquivo(files[0]);
    }

    // ============================================================
    // VALIDAÇÃO DO ARQUIVO
    // ============================================================

    private validarEProcessarArquivo(file: File): boolean {

        this.erroArquivo.set(null);
        this.erroExtracao.set(null);

        // ----------------------------------------------------------
        // Tipo
        // ----------------------------------------------------------

        if (!FORMATOS_PERMITIDOS.includes(file.type)) {

            this.arquivoAnexado.set(null);

            this.erroArquivo.set(
                'Tipo de arquivo inválido. Apenas PDF, PNG ou JPEG são permitidos.'
            );

            return false;
        }

        // ----------------------------------------------------------
        // Tamanho
        // ----------------------------------------------------------

        if (file.size > TAMANHO_MAXIMO_BYTES) {

            this.arquivoAnexado.set(null);

            this.erroArquivo.set(
                'O arquivo excede o limite máximo de 5MB.'
            );

            return false;
        }

        // ----------------------------------------------------------
        // Arquivo válido
        // ----------------------------------------------------------

        this.arquivoAnexado.set(file);

        return true;
    }

    // ============================================================
    // EXTRAÇÃO COM IA
    // ============================================================

    private extrairDadosComIA(file: File): void {

        this.extraindoComIA.set(true);

        this.erroExtracao.set(null);

        this.atividadeService
            .extrairDadosCertificado(file)
            .subscribe({

                next: (dados) => {

                    /**
                     * IMPORTANTE:
                     * Os nomes abaixo correspondem aos nomes do formulário
                     * utilizado pelo HTML.
                     */
                    this.activityForm.patchValue({

                        titulo: dados.titulo ?? '',

                        instituicao:
                            dados.instituicaoResponsavel ?? '',

                        data:
                            dados.dataRealizacao ?? '',

                        cargaHoraria:
                            dados.cargaHoraria ?? '',

                        natureza:
                            dados.natureza ?? '',

                        categoria:
                            dados.categoria ?? ''
                    });

                    this.extraindoComIA.set(false);
                },

                error: () => {

                    this.extraindoComIA.set(false);

                    /**
                     * O arquivo continua anexado mesmo que a IA falhe.
                     * O usuário pode preencher os campos manualmente.
                     */
                    this.erroExtracao.set(
                        'Não foi possível extrair os dados automaticamente, mas o arquivo foi anexado.'
                    );
                }
            });
    }

    // ============================================================
    // ARQUIVO
    // ============================================================

    removerArquivo(): void {

        this.arquivoAnexado.set(null);

        this.erroArquivo.set(null);

        this.erroExtracao.set(null);
    }

    formatarTamanhoArquivo(bytes: number): string {

        if (bytes < 1024) {
            return `${bytes} B`;
        }

        if (bytes < 1024 * 1024) {
            return `${(bytes / 1024).toFixed(2)} KB`;
        }

        return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
    }

    // ============================================================
    // SUBMIT
    // ============================================================

    onSubmit(): void {

        // Limpa mensagens anteriores.
        this.mensagemErro.set(null);

        this.erroArquivo.set(null);

        // ----------------------------------------------------------
        // Formulário inválido
        // ----------------------------------------------------------

        if (this.activityForm.invalid) {

            this.activityForm.markAllAsTouched();

            return;
        }

        // ----------------------------------------------------------
        // Arquivo obrigatório
        // ----------------------------------------------------------

        const arquivo = this.arquivoAnexado();

        if (!arquivo) {

            this.erroArquivo.set(
                'O comprovante é obrigatório.'
            );

            return;
        }

        // ----------------------------------------------------------
        // IA ainda processando
        // ----------------------------------------------------------

        if (this.extraindoComIA()) {

            this.mensagemErro.set(
                'Aguarde a conclusão da leitura do certificado.'
            );

            return;
        }

        // ----------------------------------------------------------
        // Evita duplo envio
        // ----------------------------------------------------------

        if (this.carregando()) {
            return;
        }

        this.carregando.set(true);

        // ----------------------------------------------------------
        // Dados do formulário
        // ----------------------------------------------------------

        const formValues = this.activityForm.getRawValue();

        const dados: AtividadeRequest = {

            titulo:
                formValues.titulo,

            instituicaoResponsavel:
                formValues.instituicao,

            dataRealizacao:
                formValues.data,

            natureza:
                formValues.natureza,

            categoria:
                formValues.categoria,

            cargaHoraria:
                Number(formValues.cargaHoraria),

            arquivo
        };

        // ----------------------------------------------------------
        // Cadastro
        // ----------------------------------------------------------

        this.atividadeService
            .cadastrar(dados)
            .subscribe({

                next: () => {

                    this.carregando.set(false);

                    this.mensagemErro.set(null);

                    this.mensagemSucesso.set(true);

                    this.resetarFormulario();

                    window.scrollTo({
                        top: 0,
                        behavior: 'smooth'
                    });

                    setTimeout(() => {

                        this.mensagemSucesso.set(false);

                    }, 4000);
                },

                error: (erro: unknown) => {

                    this.carregando.set(false);

                    this.mensagemErro.set(
                        this.obterMensagemErro(erro)
                    );

                    window.scrollTo({
                        top: 0,
                        behavior: 'smooth'
                    });
                }
            });
    }

    // ============================================================
    // RESET
    // ============================================================

    private resetarFormulario(): void {

        this.activityForm.reset();

        this.arquivoAnexado.set(null);

        this.erroArquivo.set(null);

        this.erroExtracao.set(null);

        this.extraindoComIA.set(false);

        this.dragOver.set(false);
    }

    // ============================================================
    // TRATAMENTO DE ERROS
    // ============================================================

    private obterMensagemErro(erro: unknown): string {

        if (
            typeof erro === 'object' &&
            erro !== null
        ) {

            const erroHttp = erro as {
                error?: {
                    message?: string;
                };

                message?: string;
            };

            if (erroHttp.error?.message) {
                return erroHttp.error.message;
            }

            if (erroHttp.message) {
                return erroHttp.message;
            }
        }

        return 'Não foi possível cadastrar a atividade. Tente novamente.';
    }
}