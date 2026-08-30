import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminService } from '../admin.service';
import { CadastroInstitucionalRequest, UsuarioAdmin } from '../admin.model';

@Component({
  selector: 'app-gestao-usuarios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestao-usuarios.component.html',
})
export class GestaoUsuariosComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly fb = inject(FormBuilder);

  readonly usuarios = signal<UsuarioAdmin[]>([]);
  readonly carregando = signal(true);
  readonly mensagemErro = signal<string | null>(null);
  readonly mensagemSucesso = signal<string | null>(null);
  readonly modalAberto = signal(false);
  readonly salvando = signal(false);

  readonly formUsuario: FormGroup = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(8)]],
    role: ['AVALIADOR', [Validators.required]],
    registro: [''],
    areaAtuacao: [''],
    setor: [''],
    nivelAcesso: ['TOTAL'],
  });

  ngOnInit(): void {
    this.carregarUsuarios();
  }

  carregarUsuarios(): void {
    this.carregando.set(true);
    this.mensagemErro.set(null);
    this.adminService.listarUsuarios().subscribe({
      next: (dados: UsuarioAdmin[]) => {
        this.usuarios.set(dados);
        this.carregando.set(false);
      },
      error: (err: Error) => {
        this.mensagemErro.set(err.message || 'Falha ao carregar usuários');
        this.carregando.set(false);
      },
    });
  }

  alternarStatus(usuario: UsuarioAdmin): void {
    this.adminService.alternarStatusUsuario(usuario.id).subscribe({
      next: (atualizado: UsuarioAdmin) => {
        this.usuarios.update((lista) =>
          lista.map((u) => (u.id === atualizado.id ? atualizado : u)),
        );
        this.mensagemSucesso.set(`Status de ${usuario.nome} alterado com sucesso.`);
      },
      error: (err: Error) => this.mensagemErro.set(err.message || 'Erro ao alternar status.'),
    });
  }

  abrirModal(): void {
    this.formUsuario.reset({ role: 'AVALIADOR', nivelAcesso: 'TOTAL' });
    this.modalAberto.set(true);
  }

  fecharModal(): void {
    this.modalAberto.set(false);
  }

  salvarUsuario(): void {
    if (this.formUsuario.invalid) {
      this.formUsuario.markAllAsTouched();
      return;
    }
    this.salvando.set(true);
    this.adminService
      .cadastrarUsuarioInstitucional(this.formUsuario.value as CadastroInstitucionalRequest)
      .subscribe({
        next: (novo: UsuarioAdmin) => {
          this.usuarios.update((lista) => [novo, ...lista]);
          this.salvando.set(false);
          this.fecharModal();
          this.mensagemSucesso.set('Usuário institucional cadastrado com sucesso.');
        },
        error: (err: Error) => {
          this.salvando.set(false);
          this.mensagemErro.set(err.message || 'Erro ao cadastrar usuário.');
        },
      });
  }
}
