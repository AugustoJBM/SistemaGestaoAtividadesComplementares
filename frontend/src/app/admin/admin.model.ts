export interface UsuarioAdmin {
  id: number;
  nome: string;
  email: string;
  role: 'ESTUDANTE' | 'AVALIADOR' | 'ADMINISTRADOR';
  ativo: boolean;
  detalheInstitucional: string;
}

export interface CadastroInstitucionalRequest {
  nome: string;
  email: string;
  senha: string;
  role: 'AVALIADOR' | 'ADMINISTRADOR';
  registro?: string;
  areaAtuacao?: string;
  nivelAcesso?: string;
  setor?: string;
}

export interface Curso {
  id?: number;
  nome: string;
  codigo: string;
  horasAccExigidas: number;
  horasAcexExigidas: number;
  ativo: boolean;
}
