import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { SituacaoSolicitacaoComponent } from './situacao-solicitacao.component';
import { SolicitacaoService } from '../../solicitacao/solicitacao.service';
import { environment } from '../../../environments/environment';

describe('SituacaoSolicitacaoComponent', () => {
  let component: SituacaoSolicitacaoComponent;
  let fixture: ComponentFixture<SituacaoSolicitacaoComponent>;
  let httpMock: HttpTestingController;
  let apiUrl: string;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SituacaoSolicitacaoComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        SolicitacaoService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SituacaoSolicitacaoComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    apiUrl = `${environment.apiUrl}/solicitacoes`;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne(apiUrl);
    req.flush([]);
    expect(component).toBeTruthy();
  });

  it('deve exibir estado de sem solicitação quando a lista estiver vazia', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne(apiUrl);
    req.flush([]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Você ainda não enviou nenhuma solicitação');
  });

  it('deve exibir solicitação em andamento (SUBMETIDA)', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne(apiUrl);
    req.flush([
      { id: 1, status: 'SUBMETIDA', dataSubmissao: '2026-08-01', totalAtividades: 2 }
    ]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Solicitação em Andamento');
    expect(compiled.textContent).toContain('SUBMETIDA');
  });

  it('deve exibir alerta de atenção quando houver pendências (COM_PENDENCIAS)', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne(apiUrl);
    req.flush([
      { id: 1, status: 'COM_PENDENCIAS', dataSubmissao: '2026-08-01', totalAtividades: 2 }
    ]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Sua solicitação requer ajustes');
    expect(compiled.textContent).toContain('COM_PENDENCIAS');
  });

  it('deve degradar silenciosamente em caso de erro na API', () => {
    fixture.detectChanges();
    const req = httpMock.expectOne(apiUrl);
    req.error(new ProgressEvent('error'));
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.bg-white')).toBeNull();
  });
});
