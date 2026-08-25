import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { RegulamentoService, IngestaoNormativaResponse, RegulamentoChunk } from './regulamento.service';

@Component({
    selector: 'app-gestao-regulamentos',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './gestao-regulamentos.component.html'
})
export class GestaoRegulamentosComponent implements OnInit {
    private readonly regulamentoService = inject(RegulamentoService);

    readonly arquivoSelecionado = signal<File | null>(null);
    readonly substituirExistentes = signal<boolean>(false);
    readonly processando = signal<boolean>(false);
    readonly resultado = signal<IngestaoNormativaResponse | null>(null);
    readonly mensagemErro = signal<string | null>(null);
    readonly arrastando = signal<boolean>(false);

    // Lista persistente do banco
    readonly regrasAtivas = signal<RegulamentoChunk[]>([]);
    readonly carregandoRegras = signal<boolean>(true);

    ngOnInit(): void {
        this.carregarRegulamentos();
    }

    carregarRegulamentos(): void {
        this.carregandoRegras.set(true);
        this.regulamentoService.listarRegulamentos().subscribe({
            next: (regras) => {
                this.regrasAtivas.set(regras);
                this.carregandoRegras.set(false);
            },
            error: () => {
                this.carregandoRegras.set(false);
            }
        });
    }

    aoSelecionarArquivo(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.validarEDefinirArquivo(input.files[0]);
        }
    }

    onDragOver(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.arrastando.set(true);
    }

    onDragLeave(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.arrastando.set(false);
    }

    onDrop(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.arrastando.set(false);
        if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
            this.validarEDefinirArquivo(event.dataTransfer.files[0]);
        }
    }

    removerArquivo(): void {
        this.arquivoSelecionado.set(null);
        this.resultado.set(null);
        this.mensagemErro.set(null);
    }

    processarDocumento(): void {
        const arquivo = this.arquivoSelecionado();
        if (!arquivo) {
            this.mensagemErro.set('Selecione um arquivo PDF ou de texto antes de processar.');
            return;
        }

        this.processando.set(true);
        this.mensagemErro.set(null);
        this.resultado.set(null);

        this.regulamentoService.ingerirDocumento(arquivo, this.substituirExistentes()).subscribe({
            next: (res) => {
                this.processando.set(false);
                this.resultado.set(res);
                this.arquivoSelecionado.set(null);
                // Atualiza a listagem com os novos registros do banco
                this.carregarRegulamentos();
            },
            error: (err) => {
                this.processando.set(false);
                this.mensagemErro.set(err.error?.message || err.message || 'Falha ao processar e vetorizar o documento.');
            }
        });
    }

    formatarTamanho(bytes: number): string {
        if (bytes < 1024) return bytes + ' B';
        const kb = bytes / 1024;
        if (kb < 1024) return kb.toFixed(1) + ' KB';
        return (kb / 1024).toFixed(2) + ' MB';
    }

    private validarEDefinirArquivo(arquivo: File): void {
        const extensoesPermitidas = ['.pdf', '.txt'];
        const nome = arquivo.name.toLowerCase();
        const valido = extensoesPermitidas.some(ext => nome.endsWith(ext));

        if (!valido) {
            this.mensagemErro.set('Formato de arquivo inválido. Apenas PDF ou TXT são aceitos.');
            return;
        }

        this.arquivoSelecionado.set(arquivo);
        this.mensagemErro.set(null);
        this.resultado.set(null);
    }
}