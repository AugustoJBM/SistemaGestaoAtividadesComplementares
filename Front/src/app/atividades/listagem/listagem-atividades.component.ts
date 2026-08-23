import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Atividade, Categoria, FiltroAtividades, Natureza, ParecerResponseDTO } from '../atividade.model';
import { AtividadeService } from '../atividade.service';
import { ParecerCardComponent } from '../parecer/parecer-conformidade/parecer-card.component';

const ROTULOS_NATUREZA: Record<string, string> = {
  ACC: 'ACC',
  ACEX: 'ACEX'
};

const ROTULOS_CATEGORIA: Record<string, string> = {
  PESQUISA: 'Pesquisa',
  EXTENSAO: 'Extensão',
  ENSINO: 'Ensino',
  EVENTOS: 'Eventos'
};

@Component({
  selector: 'app-listagem-atividades',
  standalone: true,
  imports: [CommonModule, RouterLink, ParecerCardComponent],
  templateUrl: './listagem-atividades.component.html'
})
export class ListagemAtividadesComponent implements OnInit {
  private readonly atividadeService = inject(AtividadeService);

  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly atividades = signal<Atividade[]>([]);
  readonly semAtividades = computed<boolean>(() => this.atividades().length === 0);

  readonly filtroNatureza = signal<Natureza | ''>('');
  readonly filtroCategoria = signal<Categoria | ''>('');
  readonly temFiltroAtivo = computed<boolean>(
    () => this.filtroNatureza() !== '' || this.filtroCategoria() !== ''
  );

  readonly atividadeParaExcluir = signal<Atividade | null>(null);
  readonly excluindo = signal(false);
  readonly mensagemSucesso = signal<string | null>(null);
  readonly mensagemErroExclusao = signal<string | null>(null);

  // Estados do Modal de Parecer IA
  readonly modalParecerAberto = signal<boolean>(false);
  readonly carregandoParecer = signal<boolean>(false);
  readonly parecerSelecionado = signal<ParecerResponseDTO | null>(null);
  readonly erroParecer = signal<string | null>(null);

  readonly opcoesNatureza = [
    { valor: '', rotulo: 'Todas as Naturezas' },
    { valor: Natureza.ACC, rotulo: 'ACC' },
    { valor: Natureza.ACEX, rotulo: 'ACEX' }
  ];

  readonly opcoesCategoria = [
    { valor: '', rotulo: 'Todas as Categorias' },
    { valor: Categoria.PESQUISA, rotulo: 'Pesquisa' },
    { valor: Categoria.EXTENSAO, rotulo: 'Extensão' },
    { valor: Categoria.ENSINO, rotulo: 'Ensino' },
    { valor: Categoria.EVENTOS, rotulo: 'Eventos' }
  ];

  ngOnInit(): void {
    this.buscarAtividades();
  }

  buscarAtividades(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.mensagemSucesso.set(null);
    this.mensagemErroExclusao.set(null);

    const filtro: FiltroAtividades = {};
    const natureza = this.filtroNatureza();
    const categoria = this.filtroCategoria();

    if (natureza) filtro.natureza = natureza;
    if (categoria) filtro.categoria = categoria;

    this.atividadeService.listar(filtro).subscribe({
      next: (atividades) => {
        this.atividades.set(atividades);
        this.carregando.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErro.set(erro.message);
        this.carregando.set(false);
      }
    });
  }

  aoAlterarNatureza(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filtroNatureza.set((select.value as Natureza) || '');
    this.buscarAtividades();
  }

  aoAlterarCategoria(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.filtroCategoria.set((select.value as Categoria) || '');
    this.buscarAtividades();
  }

  limparFiltros(): void {
    this.filtroNatureza.set('');
    this.filtroCategoria.set('');
    this.buscarAtividades();
  }

  solicitarExclusao(atividade: Atividade): void {
    this.mensagemSucesso.set(null);
    this.mensagemErroExclusao.set(null);
    this.atividadeParaExcluir.set(atividade);
  }

  cancelarExclusao(): void {
    this.atividadeParaExcluir.set(null);
  }

  confirmarExclusao(): void {
    const atividade = this.atividadeParaExcluir();
    if (!atividade) return;

    this.excluindo.set(true);
    this.atividadeService.excluir(atividade.id).subscribe({
      next: () => {
        this.atividades.update((atuais) => atuais.filter((item) => item.id !== atividade.id));
        this.mensagemSucesso.set(`Atividade "${atividade.titulo}" excluída com sucesso.`);
        this.atividadeParaExcluir.set(null);
        this.excluindo.set(false);
      },
      error: (erro: Error) => {
        this.mensagemErroExclusao.set(erro.message);
        this.atividadeParaExcluir.set(null);
        this.excluindo.set(false);
      }
    });
  }

  dataFormatada(dataIso: string): string {
    const partes = dataIso.split('-');
    if (partes.length !== 3) return '';
    const [ano, mes, dia] = partes;
    return `${dia}/${mes}/${ano}`;
  }

  rotuloNatureza(natureza: string): string {
    return ROTULOS_NATUREZA[natureza] ?? '';
  }

  rotuloCategoria(categoria: string): string {
    return ROTULOS_CATEGORIA[categoria] ?? '';
  }

  abrirModalParecer(atividade: Atividade): void {
    this.modalParecerAberto.set(true);
    this.carregandoParecer.set(true);
    this.parecerSelecionado.set(null);
    this.erroParecer.set(null);

    this.atividadeService.obterParecer(atividade.id).subscribe({
      next: (parecer) => {
        this.parecerSelecionado.set(parecer);
        this.carregandoParecer.set(false);
      },
      error: (err: Error) => {
        this.carregandoParecer.set(false);
        this.erroParecer.set(err.message || 'Erro ao consultar parecer da IA.');
      }
    });
  }

  fecharModalParecer(): void {
    this.modalParecerAberto.set(false);
    this.parecerSelecionado.set(null);
    this.erroParecer.set(null);
  }
}