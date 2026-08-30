import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { AdminService } from './admin.service';
import { API_BASE_URL } from '../api.config';
import { CadastroInstitucionalRequest, Curso, UsuarioAdmin } from './admin.model';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  const adminUrl = `${API_BASE_URL}/admin`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AdminService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('deve listar usuarios com filtros de role e status', () => {
    const mockUsuarios: UsuarioAdmin[] = [
      {
        id: 1,
        nome: 'Prof. Avaliador',
        email: 'prof@ufape.edu.br',
        role: 'AVALIADOR',
        ativo: true,
        detalheInstitucional: 'BCC',
      },
    ];

    service.listarUsuarios('AVALIADOR', true).subscribe((res) => {
      expect(res).toEqual(mockUsuarios);
    });

    const req = httpMock.expectOne(`${adminUrl}/usuarios?role=AVALIADOR&ativo=true`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsuarios);
  });

  it('deve alternar status do usuario via PATCH', () => {
    const atualizado: UsuarioAdmin = {
      id: 2,
      nome: 'Admin',
      email: 'admin@ufape.edu.br',
      role: 'ADMINISTRADOR',
      ativo: false,
      detalheInstitucional: 'TI',
    };

    service.alternarStatusUsuario(2).subscribe((res) => {
      expect(res.ativo).toBe(false);
    });

    const req = httpMock.expectOne(`${adminUrl}/usuarios/2/status`);
    expect(req.request.method).toBe('PATCH');
    req.flush(atualizado);
  });

  it('deve cadastrar usuario institucional via POST', () => {
    const payload: CadastroInstitucionalRequest = {
      nome: 'Novo Prof',
      email: 'novo@ufape.edu.br',
      senha: 'senhaSegura123',
      role: 'AVALIADOR',
    };

    service.cadastrarUsuarioInstitucional(payload).subscribe();
    const req = httpMock.expectOne(`${adminUrl}/usuarios`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 3, ...payload, ativo: true, detalheInstitucional: '-' });
  });

  it('deve listar e criar cursos', () => {
    const mockCurso: Curso = {
      id: 1,
      nome: 'Engenharia de Software',
      codigo: 'ES',
      horasAccExigidas: 90,
      horasAcexExigidas: 320,
      ativo: true,
    };

    service.listarCursos().subscribe((res) => expect(res).toEqual([mockCurso]));
    const reqGet = httpMock.expectOne(`${adminUrl}/cursos`);
    expect(reqGet.request.method).toBe('GET');
    reqGet.flush([mockCurso]);

    service.criarCurso(mockCurso).subscribe((res) => expect(res).toEqual(mockCurso));
    const reqPost = httpMock.expectOne(`${adminUrl}/cursos`);
    expect(reqPost.request.method).toBe('POST');
    reqPost.flush(mockCurso);
  });
});
