import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, Subject, of, throwError } from 'rxjs';
import { describe, it, expect, vi } from 'vitest';
import { ListagemAtividadesComponent } from './listagem-atividades.component';
import { Atividade, Categoria, FiltroAtividades, Natureza } from '../atividade.model';
import { AtividadeService } from '../atividade.service';

const atividades: Atividade[] = [
  {
    id: 1,
    titulo: 'Monitoria de Algoritmos',
    instituicaoResponsavel: 'UFAPE',
    dataRealizacao: '2026-03-10',
    cargaHorariaEmHoras: 30,
    natureza: 'ACC',
    categoria: 'ENSINO',
    dataCadastro: '2026-03-11T08:00:00'
  },
  {
    id: 2,
    titulo: 'Feira de Ciências',
    instituicaoResponsavel: 'Escola Municipal',
    dataRealizacao: '2026-04-22',
    cargaHorariaEmHoras: 12,
    natureza: 'ACEX',
    categoria: 'EXTENSAO',
    dataCadastro: null
  }
];

describe('ListagemAtividadesComponent', () => {
  let fixture: ComponentFixture<ListagemAtividadesComponent>;
  let atividadeServiceDuble: { listar: (filtro?: FiltroAtividades) => Observable<Atividade[]> };

  const configurarComponente = async (): Promise<void> => {
    await TestBed.configureTestingModule({
      imports: [ListagemAtividadesComponent],
      providers: [
        provideRouter([]),
        { provide: AtividadeService, useValue: atividadeServiceDuble }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(ListagemAtividadesComponent);
  };

  it('deve exibir o estado de carregando antes da resposta do service', async () => {
    const listagemNaoResolvida = new Subject<Atividade[]>();
    atividadeServiceDuble = { listar: () => listagemNaoResolvida.asObservable() };
    await configurarComponente();
    fixture.detectChanges();
    const texto = fixture.nativeElement.textContent as string;
    expect(fixture.componentInstance.carregando()).toBeTruthy();
    expect(texto).toContain('Carregando suas atividades...');
  });

  it('deve renderizar as atividades retornadas pelo service', async () => {
    atividadeServiceDuble = { listar: () => of(atividades) };
    await configurarComponente();
    fixture.detectChanges();
    const itens = fixture.nativeElement.querySelectorAll('li');
    const texto = fixture.nativeElement.textContent as string;
    expect(fixture.componentInstance.carregando()).toBeFalsy();
    expect(itens.length).toBe(2);
    expect(texto).toContain('Monitoria de Algoritmos');
    expect(texto).toContain('UFAPE');
    expect(texto).toContain('30h');
    expect(texto).toContain('10/03/2026');
    expect(texto).toContain('Ensino');
    expect(texto).toContain('Feira de Ciências');
    expect(texto).toContain('Extensão');
  });

  it('deve exibir o link de edicao apontando para a rota correta de cada atividade', async () => {
    atividadeServiceDuble = { listar: () => of(atividades) };
    await configurarComponente();
    fixture.detectChanges();

    const linksEdicao = fixture.nativeElement.querySelectorAll('a[href*="/atividades/edicao"]');
    expect(linksEdicao.length).toBe(2);
    expect(linksEdicao[0].getAttribute('href')).toContain('/atividades/edicao/1');
    expect(linksEdicao[1].getAttribute('href')).toContain('/atividades/edicao/2');
  });

  it('deve exibir o empty state quando o estudante não possui atividades e não há filtro ativo', async () => {
    atividadeServiceDuble = { listar: () => of([]) };
    await configurarComponente();
    fixture.detectChanges();
    const itens = fixture.nativeElement.querySelectorAll('li');
    const texto = fixture.nativeElement.textContent as string;
    expect(fixture.componentInstance.semAtividades()).toBeTruthy();
    expect(itens.length).toBe(0);
    expect(texto).toContain('Você ainda não cadastrou atividades');
  });

  it('deve exibir a mensagem de erro devolvida pelo service em um alerta acessível', async () => {
    atividadeServiceDuble = {
      listar: () => throwError(() => new Error('Não foi possível carregar suas atividades. Tente novamente.'))
    };
    await configurarComponente();
    fixture.detectChanges();
    const alerta = fixture.nativeElement.querySelector('[role="alert"]') as HTMLElement;
    expect(fixture.componentInstance.carregando()).toBeFalsy();
    expect(alerta.textContent).toContain('Não foi possível carregar suas atividades. Tente novamente.');
  });

  it('deve recarregar a listagem ao acionar "Tentar novamente" após uma falha', async () => {
    let tentativas = 0;
    atividadeServiceDuble = {
      listar: () => {
        tentativas += 1;
        return tentativas === 1
          ? throwError(() => new Error('Não foi possível conectar ao servidor. Verifique sua conexão.'))
          : of(atividades);
      }
    };
    await configurarComponente();
    fixture.detectChanges();
    const botao = fixture.nativeElement.querySelector('[role="alert"] button') as HTMLButtonElement;
    botao.click();
    fixture.detectChanges();
    const texto = fixture.nativeElement.textContent as string;
    expect(tentativas).toBe(2);
    expect(fixture.componentInstance.mensagemErro()).toBeNull();
    expect(texto).toContain('Monitoria de Algoritmos');
  });

  it('deve filtrar por natureza chamando o service com o parâmetro correto', async () => {
    const spyListar = vi.fn().mockReturnValue(of([atividades[0]]));
    atividadeServiceDuble = { listar: spyListar };
    await configurarComponente();
    fixture.detectChanges();
    const selectNatureza = fixture.nativeElement.querySelector('#filtro-natureza') as HTMLSelectElement;
    selectNatureza.value = Natureza.ACC;
    selectNatureza.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    expect(spyListar).toHaveBeenLastCalledWith({ natureza: Natureza.ACC });
    expect(fixture.componentInstance.filtroNatureza()).toBe(Natureza.ACC);
  });

  it('deve filtrar por categoria chamando o service com o parâmetro correto', async () => {
    const spyListar = vi.fn().mockReturnValue(of([atividades[0]]));
    atividadeServiceDuble = { listar: spyListar };
    await configurarComponente();
    fixture.detectChanges();
    const selectCategoria = fixture.nativeElement.querySelector('#filtro-categoria') as HTMLSelectElement;
    selectCategoria.value = Categoria.ENSINO;
    selectCategoria.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    expect(spyListar).toHaveBeenLastCalledWith({ categoria: Categoria.ENSINO });
    expect(fixture.componentInstance.filtroCategoria()).toBe(Categoria.ENSINO);
  });

  it('deve combinar os filtros de natureza e categoria na mesma busca', async () => {
    const spyListar = vi.fn().mockReturnValue(of([atividades[0]]));
    atividadeServiceDuble = { listar: spyListar };
    await configurarComponente();
    fixture.detectChanges();
    const selectNatureza = fixture.nativeElement.querySelector('#filtro-natureza') as HTMLSelectElement;
    selectNatureza.value = Natureza.ACC;
    selectNatureza.dispatchEvent(new Event('change'));
    const selectCategoria = fixture.nativeElement.querySelector('#filtro-categoria') as HTMLSelectElement;
    selectCategoria.value = Categoria.ENSINO;
    selectCategoria.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    expect(spyListar).toHaveBeenLastCalledWith({
      natureza: Natureza.ACC,
      categoria: Categoria.ENSINO
    });
  });

  it('deve limpar os filtros e retornar para a busca sem parâmetros', async () => {
    const spyListar = vi.fn().mockReturnValue(of(atividades));
    atividadeServiceDuble = { listar: spyListar };
    await configurarComponente();
    fixture.detectChanges();
    fixture.componentInstance.filtroNatureza.set(Natureza.ACC);
    fixture.componentInstance.filtroCategoria.set(Categoria.ENSINO);
    fixture.detectChanges();
    const botaoLimpar = fixture.nativeElement.querySelector('#btn-limpar-filtros') as HTMLButtonElement;
    expect(botaoLimpar).toBeTruthy();
    botaoLimpar.click();
    fixture.detectChanges();
    expect(fixture.componentInstance.filtroNatureza()).toBe('');
    expect(fixture.componentInstance.filtroCategoria()).toBe('');
    expect(spyListar).toHaveBeenLastCalledWith({});
  });

  it('deve exibir mensagem de empty state específica quando o filtro não retornar resultados', async () => {
    atividadeServiceDuble = { listar: () => of([]) };
    await configurarComponente();
    fixture.detectChanges();
    const selectNatureza = fixture.nativeElement.querySelector('#filtro-natureza') as HTMLSelectElement;
    selectNatureza.value = Natureza.ACEX;
    selectNatureza.dispatchEvent(new Event('change'));
    fixture.detectChanges();
    const texto = fixture.nativeElement.textContent as string;
    expect(texto).toContain('Nenhuma atividade encontrada com os filtros selecionados');
    expect(texto).toContain('Tente alterar ou limpar os filtros');
  });

  describe('exclusao de atividade', () => {
    function montarComExclusao(excluirDuble: (id: number) => Observable<void>) {
      const duble = {
        listar: () => of(atividades),
        excluir: vi.fn(excluirDuble)
      };
      TestBed.configureTestingModule({
        imports: [ListagemAtividadesComponent],
        providers: [provideRouter([]), { provide: AtividadeService, useValue: duble }]
      });
      const fixture = TestBed.createComponent(ListagemAtividadesComponent);
      fixture.detectChanges();
      return { fixture, duble };
    }

    it('abre o dialogo de confirmacao sem chamar a API', () => {
      const { fixture, duble } = montarComExclusao(() => of(void 0));

      const listItem = fixture.nativeElement.querySelector('li');
      const botaoExcluir = listItem.querySelector('button');
      botaoExcluir.click();
      fixture.detectChanges();

      const dialogo = fixture.nativeElement.querySelector('[role="dialog"]') as HTMLElement;
      expect(dialogo).toBeTruthy();
      expect(dialogo.textContent).toContain('Excluir atividade');
      expect(dialogo.textContent).toContain('Esta ação não pode ser desfeita');
      expect(duble.excluir).not.toHaveBeenCalled();
    });

    it('cancelar a confirmacao nao chama a API e fecha o dialogo', () => {
      const { fixture, duble } = montarComExclusao(() => of(void 0));

      const listItem = fixture.nativeElement.querySelector('li');
      const botaoExcluirLista = listItem.querySelector('button');
      botaoExcluirLista.click();
      fixture.detectChanges();

      const dialogo = fixture.nativeElement.querySelector('[role="dialog"]') as HTMLElement;
      const botoesDialogo = dialogo.querySelectorAll('button');
      const botaoCancelar = Array.from(botoesDialogo).find((btn) => (btn as HTMLElement).textContent?.trim() === 'Cancelar') as HTMLButtonElement;
      botaoCancelar.click();
      fixture.detectChanges();

      expect(duble.excluir).not.toHaveBeenCalled();
      const dialogoAposCancel = fixture.nativeElement.querySelector('[role="dialog"]');
      expect(dialogoAposCancel).toBeFalsy();
    });

    it('confirmar chama o service e remove o item da lista', () => {
      const { fixture, duble } = montarComExclusao(() => of(void 0));

      fixture.componentInstance.solicitarExclusao(atividades[0]);
      fixture.componentInstance.confirmarExclusao();
      fixture.detectChanges();

      expect(duble.excluir).toHaveBeenCalledWith(1);
      expect(fixture.componentInstance.atividades().map((a) => a.id)).toEqual([2]);
      expect(fixture.componentInstance.atividadeParaExcluir()).toBeNull();
      expect(fixture.componentInstance.mensagemSucesso()).toContain('excluída');
    });

    it('erro do backend exibe mensagem em role=alert e mantem o item na lista', () => {
      const { fixture } = montarComExclusao(() =>
        throwError(() => new Error('Você só pode excluir suas próprias atividades.'))
      );

      fixture.componentInstance.solicitarExclusao(atividades[0]);
      fixture.componentInstance.confirmarExclusao();
      fixture.detectChanges();

      expect(fixture.componentInstance.atividades().map((a) => a.id)).toEqual([1, 2]);
      expect(fixture.componentInstance.mensagemErroExclusao())
        .toBe('Você só pode excluir suas próprias atividades.');

      const alertas = fixture.nativeElement.querySelectorAll('[role="alert"]');
      const textos = Array.from(alertas).map((el) => (el as HTMLElement).textContent ?? '');
      expect(textos.some((t) => t.includes('suas próprias atividades'))).toBe(true);
    });

    it('marca estado de carregamento enquanto a exclusao esta em andamento', () => {
      const { fixture } = montarComExclusao(() => new Observable<void>(() => { }));

      fixture.componentInstance.solicitarExclusao(atividades[0]);
      fixture.componentInstance.confirmarExclusao();
      fixture.detectChanges();

      expect(fixture.componentInstance.excluindo()).toBe(true);

      const dialogo = fixture.nativeElement.querySelector('[role="dialog"]') as HTMLElement;
      expect(dialogo).toBeTruthy();
      const botoes = dialogo.querySelectorAll('button');
      const todosDesabilitados = Array.from(botoes).every((btn) => (btn as HTMLButtonElement).disabled);
      expect(todosDesabilitados).toBe(true);
    });

    it('limpa a mensagem de sucesso ao mudar filtros apos uma exclusao bem-sucedida', () => {
      const { fixture, duble } = montarComExclusao(() => of(void 0));

      fixture.componentInstance.solicitarExclusao(atividades[0]);
      fixture.componentInstance.confirmarExclusao();
      fixture.detectChanges();

      expect(fixture.componentInstance.mensagemSucesso()).toContain('excluída');

      fixture.componentInstance.aoAlterarNatureza({
        target: { value: 'ACC' }
      } as unknown as Event);
      fixture.detectChanges();

      expect(fixture.componentInstance.mensagemSucesso()).toBeNull();
      const bannerSucesso = fixture.nativeElement.querySelector('[role="status"]');
      expect(bannerSucesso).toBeFalsy();
    });
  });
});