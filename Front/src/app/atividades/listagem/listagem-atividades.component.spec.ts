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
});