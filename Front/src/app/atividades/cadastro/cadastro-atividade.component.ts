import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AtividadeService } from '../atividade.service';

const FORMATOS_PERMITIDOS = ['application/pdf', 'image/png', 'image/jpeg', 'image/jpg'];
const TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024; // 5MB

@Component({
    selector: 'app-cadastro-atividade',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './cadastro-atividade.component.html'
})
export class CadastroAtividadeComponent {
    private readonly fb = inject(FormBuilder);
    private readonly atividadeService = inject(AtividadeService);

    readonly carregando = signal<boolean>(false);
    readonly mensagemErro = signal<string | null>(null);
    readonly mensagemSucesso = signal<boolean>(false);
    readonly arquivoAnexado = signal<File | null>(null);
    readonly erroArquivo = signal<string | null>(null);
    readonly dragOver = signal<boolean>(false);

    readonly activityForm: FormGroup = this.fb.group({
        titulo: ['', [Validators.required]],
        instituicao: [''],
        data: ['', [Validators.required]],
        natureza: ['', [Validators.required]],
        categoria: ['', [Validators.required]],
        cargaHoraria: ['', [Validators.required, Validators.min(1)]]
    });

    isCampoInvalido(nomeCampo: string): boolean {
        const campo = this.activityForm.get(nomeCampo);
        return !!(campo && campo.invalid && (campo.touched || campo.dirty));
    }

    isFormularioInvalido(): boolean {
        return this.activityForm.invalid || !this.arquivoAnexado() || this.carregando();
    }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.validarEProcessarArquivo(input.files[0]);
        }
    }

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

        if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
            this.validarEProcessarArquivo(event.dataTransfer.files[0]);
        }
    }

    removerArquivo(): void {
        this.arquivoAnexado.set(null);
        this.erroArquivo.set(null);
    }

    formatarTamanhoArquivo(bytes: number): string {
        return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
    }

    onSubmit(): void {
        if (this.isFormularioInvalido()) {
            this.activityForm.markAllAsTouched();
            if (!this.arquivoAnexado()) {
                this.erroArquivo.set('O comprovante é obrigatório.');
            }
            return;
        }

        this.carregando.set(true);
        this.mensagemErro.set(null);

        const formValues = this.activityForm.value;
        const dados = {
            titulo: formValues.titulo,
            instituicao: formValues.instituicao,
            data: formValues.data,
            natureza: formValues.natureza,
            categoria: formValues.categoria,
            cargaHoraria: Number(formValues.cargaHoraria),
            comprovante: this.arquivoAnexado()!
        };

        this.atividadeService.cadastrar(dados).subscribe({
            next: () => {
                this.carregando.set(false);
                this.mensagemSucesso.set(true);
                this.resetarFormulario();
                window.scrollTo({ top: 0, behavior: 'smooth' });

                setTimeout(() => {
                    this.mensagemSucesso.set(false);
                }, 4000);
            },
            error: (erro: Error) => {
                this.carregando.set(false);
                this.mensagemErro.set(erro.message);
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        });
    }

    private validarEProcessarArquivo(file: File): void {
        this.erroArquivo.set(null);

        if (!FORMATOS_PERMITIDOS.includes(file.type)) {
            this.erroArquivo.set('Tipo de arquivo inválido. Apenas PDF, PNG ou JPEG são permitidos.');
            this.arquivoAnexado.set(null);
            return;
        }

        if (file.size > TAMANHO_MAXIMO_BYTES) {
            this.erroArquivo.set('O arquivo excede o limite máximo de 5MB.');
            this.arquivoAnexado.set(null);
            return;
        }

        this.arquivoAnexado.set(file);
    }

    private resetarFormulario(): void {
        this.activityForm.reset();
        this.arquivoAnexado.set(null);
        this.erroArquivo.set(null);
    }
}