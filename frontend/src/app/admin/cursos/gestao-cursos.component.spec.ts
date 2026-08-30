import { TestBed, ComponentFixture } from '@angular/core/testing';
import { of } from 'rxjs';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { GestaoCursosComponent } from './gestao-cursos.component';
import { AdminService } from '../admin.service';
import { Curso } from '../admin.model';

const cursosMock: Curso[] = [
  {
    id: 1,
    nome: 'Ciência da Computação',
    codigo: 'BCC',
    horasAccExigidas: 90,
    horasAcexExigidas: 320,
    ativo: true,
  },
];

describe('GestaoCursosComponent', () => {
  let component: GestaoCursosComponent;
  let fixture: ComponentFixture<GestaoCursosComponent>;
  let adminServiceSpy: {
    listarCursos: ReturnType<typeof vi.fn>;
    criarCurso: ReturnType<typeof vi.fn>;
    atualizarCurso: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    adminServiceSpy = {
      listarCursos: vi.fn().mockReturnValue(of(cursosMock)),
      criarCurso: vi.fn(),
      atualizarCurso: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [GestaoCursosComponent],
      providers: [{ provide: AdminService, useValue: adminServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(GestaoCursosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => TestBed.resetTestingModule());

  it('deve carregar a lista de cursos parametrizados', () => {
    expect(component).toBeTruthy();
    expect(adminServiceSpy.listarCursos).toHaveBeenCalled();
    expect(component.cursos().length).toBe(1);
    expect(fixture.nativeElement.textContent).toContain('Ciência da Computação');
  });
});
