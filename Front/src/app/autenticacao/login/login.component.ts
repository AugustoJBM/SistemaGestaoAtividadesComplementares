import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AutenticacaoService } from '../autenticacao.service';
import { Credenciais } from '../autenticacao.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AutenticacaoService);
  private readonly router = inject(Router);

  readonly isLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly showPassword = signal<boolean>(false);

  readonly loginForm: FormGroup = this.fb.group({
    login: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    rememberMe: [false]
  });

  get loginControl() {
    return this.loginForm.get('login');
  }

  get passwordControl() {
    return this.loginForm.get('password');
  }

  togglePasswordVisibility(): void {
    this.showPassword.update(value => !value);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const credenciais: Credenciais = {
      email: this.loginForm.value.login,
      senha: this.loginForm.value.password
    };

    this.authService.login(credenciais).subscribe({
      next: () => {
        this.isLoading.set(false);
        const role = this.authService.getRole();

        // Redireciona conforme o perfil
        if (role === 'ADMINISTRADOR' || role === 'AVALIADOR') {
          this.router.navigate(['/regulamentos/gestao']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (erro: Error) => {
        this.isLoading.set(false);
        this.errorMessage.set(erro.message);
      }
    });
  }
}