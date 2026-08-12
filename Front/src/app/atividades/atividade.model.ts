export enum Natureza {
  ACC = 'ACC',
  ACEX = 'ACEX'
}

export enum Categoria {
  PESQUISA = 'PESQUISA',
  EXTENSAO = 'EXTENSAO',
  ENSINO = 'ENSINO',
  EVENTOS = 'EVENTOS'
}

export interface AtividadeRequest {
  titulo: string;
  descricao?: string;
  natureza: Natureza;
  categoria: Categoria;
  quantidadeHoras: number;
  comprovante: File;
}

export interface AtividadeResponse {
  id: number;
  titulo: string;
  descricao?: string;
  natureza: string;
  categoria: string;
  quantidadeHoras: number;
  status: string;
}
