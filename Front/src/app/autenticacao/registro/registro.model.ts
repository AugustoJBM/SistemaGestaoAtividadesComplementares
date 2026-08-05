export interface RegistroRequest {
  fullName: string;
  emailOrRegistration: string;
  role: 'estudante' | 'avaliador' | 'admin';
  password: string;
}

export interface RegistroResponse {
  message: string;
  success: boolean;
}