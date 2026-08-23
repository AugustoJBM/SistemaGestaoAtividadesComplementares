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
  status?: string;
}

export interface AtividadeListagemDTO {
  id?: number | null;
  titulo?: string | null;
  instituicaoResponsavel?: string | null;
  dataRealizacao?: string | null;
  cargaHorariaEmHoras?: number | null;
  natureza?: string | null;
  categoria?: string | null;
  dataCadastro?: string | null;
  status?: string | null;
}

export interface Atividade {
  id: number;
  titulo: string;
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHorariaEmHoras: number;
  natureza: string;
  categoria: string;
  dataCadastro: string | null;
  status?: string;
}

export interface FiltroAtividades {
  natureza?: Natureza;
  categoria?: Categoria;
}

export interface ParecerResponseDTO {
  id: number | null;
  atividadeId: number | null;
  naturezaSugerida: string;
  categoriaSugerida: string;
  cargaHorariaAproveitavel: number;
  artigoRegulamento: string;
  justificativaTecnica: string;
  scoreConfianca: number;
  decisaoIA: 'DEFERIDO' | 'INDEFERIDO' | 'AMBIGUO';
  tempoProcessamentoMs: number | null;
}

export interface ExtracaoCertificadoResponseDTO {
  titulo: string;
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHoraria: number;
  natureza: Natureza | string;
  categoria: Categoria | string;
}