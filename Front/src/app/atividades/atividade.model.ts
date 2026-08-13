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
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHoraria: number;
  natureza: Natureza;
  categoria: Categoria;
  arquivo: File;
}

export interface AtividadeResponse {
  id: number;
  titulo: string;
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHorariaEmHoras: number;
  natureza: string;
  categoria: string;
  dataCadastro?: string;
  estudanteEmail?: string;
}