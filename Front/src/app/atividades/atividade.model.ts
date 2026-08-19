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

// Contrato de fio da listagem (GET /api/v1/atividades). Campos podem vir
// ausentes ou nulos, por isso o service normaliza antes de expor o dominio.
export interface AtividadeListagemDTO {
  id?: number | null;
  titulo?: string | null;
  instituicaoResponsavel?: string | null;
  dataRealizacao?: string | null;
  cargaHorariaEmHoras?: number | null;
  natureza?: string | null;
  categoria?: string | null;
  dataCadastro?: string | null;
}

// Modelo de dominio entregue as telas de listagem.
export interface Atividade {
  id: number;
  titulo: string;
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHorariaEmHoras: number;
  natureza: string;
  categoria: string;
  dataCadastro: string | null;
}

export interface FiltroAtividades {
  natureza?: Natureza;
  categoria?: Categoria;
}