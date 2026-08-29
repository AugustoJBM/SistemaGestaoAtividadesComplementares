export interface Credenciais {
  email: string;
  senha: string;
}

// Contrato de wire do backend. O campo `usuario` e resolvido por e-mail no servidor.
export interface LoginRequest {
  usuario: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
}
