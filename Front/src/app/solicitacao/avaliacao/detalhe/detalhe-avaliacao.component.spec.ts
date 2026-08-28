import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { DetalheAvaliacaoComponent } from './detalhe-avaliacao.component';
import { AvaliacaoSolicitacaoService } from '../avaliacao-solicitacao.service';
import { SolicitacaoAvaliadorDetalhe } from '../avaliacao.model';

const detalheMock: SolicitacaoAvaliadorDetalhe = {
  id: 7,
  estudanteNome: 'Ana Souza',
  estudanteEmail: 'ana.souza@ufape.edu.br',
  dataSubmissao: '2026-08-20T10:30:00',
  dataAvaliacao: '2026-08-22T09:00:00',
  status: 'REJEITADA',
  justificativa: 'Certificado ilegível.',
  cargaHorariaTotal: 35,
  itens: [
    { atividadeId: 1, titulo: 'Iniciacao Cientifica', cargaHoraria: 15, natureza: 'ACC' },
    { atividadeId: 2, titulo: 'Projeto de Extensao', cargaHoraria: 20, natureza: 'ACEX' },
  ],
};

function montar(
  duble: Partial<AvaliacaoSolicitacaoService>,
  id = '7',
): ComponentFixture<DetalheAvaliacaoComponent> {
  TestBed.configureTestingModule({
    imports: [DetalheAvaliacaoComponent],
    providers: [
      provideRouter([]),
      { provide: AvaliacaoSolicitacaoService, useValue: duble },
      {
        provide: ActivatedRoute,
        useValue: {
          snapshot: {
            paramMap: {
              get: (chave: string) => (chave === 'id' ? id : null),
            },
          },
        },
      },
    ],
  });
  return TestBed.createComponent(DetalheAvaliacaoComponent);
}

describe('DetalheAvaliacaoComponent', () => {
  afterEach(() => TestBed.resetTestingModule());

  it('exibe estado de carregamento antes da resposta', () => {
    const fixture = montar({
      detalhar: () => new Observable<SolicitacaoAvaliadorDetalhe>(() => {}),
    });
    fixture.detectChanges();

    expect(fixture.componentInstance.carregando()).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('Carregando');
  });

  it('renderiza o detalhe completo da solicitacao', () => {
    const fixture = montar({ detalhar: () => of(detalheMock) });
    fixture.detectChanges();

    const texto = fixture.nativeElement.textContent as string;
    expect(texto).toContain('Ana Souza');
    expect(texto).toContain('ana.souza@ufape.edu.br');
    expect(texto).toContain('Rejeitada');
    expect(texto).toContain('Certificado ilegível.');
    expect(texto).toContain('35h');
    expect(texto).toContain('Iniciacao Cientifica');
    expect(texto).toContain('Projeto de Extensao');
    expect(texto).toContain('22/08/2026');
  });

  it('nao renderiza rotulo de justificativa quando o campo e nulo', () => {
    const fixture = montar({
      detalhar: () => of({ ...detalheMock, justificativa: undefined, dataAvaliacao: undefined }),
    });
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="justificativa"]')).toBeNull();
    expect(fixture.nativeElement.textContent).not.toContain('Data de avaliação');
  });

  it('mostra banner de erro amigavel para id inexistente', () => {
    const fixture = montar(
      { detalhar: () => throwError(() => new Error('Solicitação não encontrada.')) },
      '999',
    );
    fixture.detectChanges();

    const alerta = fixture.nativeElement.querySelector('[role="alert"]');
    expect(alerta).toBeTruthy();
    expect(alerta.textContent).toContain('Solicitação não encontrada.');
  });
});
