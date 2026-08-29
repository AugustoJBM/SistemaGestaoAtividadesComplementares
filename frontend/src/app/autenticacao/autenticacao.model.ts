export type Role = 'ESTUDANTE' | 'AVALIADOR' | 'ADMINISTRADOR';

export interface Credenciais {
  email: string;
  senha: string;
}

export interface LoginRequest {
  usuario: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
}
