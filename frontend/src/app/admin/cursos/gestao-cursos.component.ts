import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../admin.service';
import { Curso } from '../admin.model';

@Component({
  selector: 'app-gestao-cursos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestao-cursos.component.html',
})
export class GestaoCursosComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly fb = inject(FormBuilder);

  readonly cursos = signal<Curso[]>([]);
  readonly carregando = signal(true);
  readonly mensagemSucesso = signal<string | null>(null);
  readonly mensagemErro = signal<string | null>(null);
  readonly modalAberto = signal(false);
  readonly editandoId = signal<number | null>(null);

  readonly formCurso: FormGroup = this.fb.group({
    nome: ['', [Validators.required]],
    codigo: ['', [Validators.required]],
    horasAccExigidas: [90, [Validators.required, Validators.min(1)]],
    horasAcexExigidas: [320, [Validators.required, Validators.min(1)]],
    ativo: [true],
  });

  ngOnInit(): void {
    this.carregarCursos();
  }

  carregarCursos(): void {
    this.carregando.set(true);
    this.adminService.listarCursos().subscribe({
      next: (dados: Curso[]) => {
        this.cursos.set(dados);
        this.carregando.set(false);
      },
      error: (err: Error) => {
        this.mensagemErro.set(err.message || 'Falha ao buscar cursos');
        this.carregando.set(false);
      },
    });
  }

  abrirModalNovo(): void {
    this.editandoId.set(null);
    this.formCurso.reset({ horasAccExigidas: 90, horasAcexExigidas: 320, ativo: true });
    this.modalAberto.set(true);
  }

  abrirModalEditar(curso: Curso): void {
    this.editandoId.set(curso.id!);
    this.formCurso.setValue({
      nome: curso.nome,
      codigo: curso.codigo,
      horasAccExigidas: curso.horasAccExigidas,
      horasAcexExigidas: curso.horasAcexExigidas,
      ativo: curso.ativo,
    });
    this.modalAberto.set(true);
  }

  salvar(): void {
    if (this.formCurso.invalid) return;
    const dados = this.formCurso.value as Curso;
    const id = this.editandoId();

    if (id) {
      this.adminService.atualizarCurso(id, dados).subscribe({
        next: (atualizado: Curso) => {
          this.cursos.update((lista) => lista.map((c) => (c.id === id ? atualizado : c)));
          this.modalAberto.set(false);
          this.mensagemSucesso.set('Curso atualizado com sucesso.');
        },
        error: (err: Error) => this.mensagemErro.set(err.message || 'Erro ao atualizar curso.'),
      });
    } else {
      this.adminService.criarCurso(dados).subscribe({
        next: (criado: Curso) => {
          this.cursos.update((lista) => [...lista, criado]);
          this.modalAberto.set(false);
          this.mensagemSucesso.set('Curso cadastrado com sucesso.');
        },
        error: (err: Error) => this.mensagemErro.set(err.message || 'Erro ao cadastrar curso.'),
      });
    }
  }
}
