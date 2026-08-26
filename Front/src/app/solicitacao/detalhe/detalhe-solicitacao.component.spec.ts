import { TestBed, ComponentFixture } from '@angular/core/testing';
import { DetalheSolicitacaoComponent } from './detalhe-solicitacao.component';
import { SolicitacaoDetalhe } from '../solicitacao.model';

const base: SolicitacaoDetalhe = {
  id: 7,
  status: 'APROVADA',
  dataSubmissao: '2026-08-20T10:30:00',
  dataAvaliacao: '2026-08-22T09:00:00',
  totalAtividades: 2,
  itens: [
    { atividadeId: 1, titulo: 'Iniciacao Cientifica', cargaHoraria: 15, natureza: 'ACC' },
    { atividadeId: 2, titulo: 'Projeto de Extensao', cargaHoraria: 20, natureza: 'ACEX' }
  ]
};

function montar(detalhe: SolicitacaoDetalhe): ComponentFixture<DetalheSolicitacaoComponent> {
  TestBed.configureTestingModule({ imports: [DetalheSolicitacaoComponent] });
  const fixture = TestBed.createComponent(DetalheSolicitacaoComponent);
  fixture.componentRef.setInput('detalhe', detalhe);
  fixture.detectChanges();
  return fixture;
}

describe('DetalheSolicitacaoComponent', () => {
  afterEach(() => TestBed.resetTestingModule());

  it('lista os itens submetidos com carga horaria e natureza', () => {
    const fixture = montar(base);

    const texto = fixture.nativeElement.textContent as string;
    expect(texto).toContain('Iniciacao Cientifica');
    expect(texto).toContain('Projeto de Extensao');
    expect(texto).toContain('15h');
    expect(texto).toContain('ACEX');
  });

  it('nao exibe bloco de justificativa quando aprovada', () => {
    const fixture = montar(base);

    expect(fixture.componentInstance.mostraJustificativa()).toBe(false);
    expect(fixture.nativeElement.querySelector('[data-testid="justificativa"]')).toBeNull();
  });

  it('exibe a justificativa quando rejeitada', () => {
    const fixture = montar({ ...base, status: 'REJEITADA', justificativa: 'Certificado ilegível.' });

    expect(fixture.componentInstance.mostraJustificativa()).toBe(true);
    const bloco = fixture.nativeElement.querySelector('[data-testid="justificativa"]');
    expect(bloco).toBeTruthy();
    expect(bloco.textContent).toContain('Certificado ilegível.');
  });

  it('exibe a justificativa quando ha pendencias', () => {
    const fixture = montar({ ...base, status: 'COM_PENDENCIAS', justificativa: 'Anexe o certificado do item 2.' });

    const bloco = fixture.nativeElement.querySelector('[data-testid="justificativa"]');
    expect(bloco).toBeTruthy();
    expect(bloco.textContent).toContain('Anexe o certificado do item 2.');
  });

  it('nao exibe bloco vazio quando rejeitada sem texto de justificativa', () => {
    const fixture = montar({ ...base, status: 'REJEITADA', justificativa: '   ' });

    expect(fixture.componentInstance.mostraJustificativa()).toBe(false);
    expect(fixture.nativeElement.querySelector('[data-testid="justificativa"]')).toBeNull();
  });
});
