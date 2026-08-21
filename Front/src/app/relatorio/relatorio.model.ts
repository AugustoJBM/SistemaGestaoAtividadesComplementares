export interface ItemAtividadeRelatorio {
  id: number;
  titulo: string;
  instituicaoResponsavel: string;
  dataRealizacao: string;
  cargaHorariaEmHoras: number;
}

export interface GrupoCategoriaRelatorio {
  categoria: string;
  totalHoras: number;
  atividades: ItemAtividadeRelatorio[];
}

export interface GrupoNaturezaRelatorio {
  natureza: string;
  totalHoras: number;
  categorias: GrupoCategoriaRelatorio[];
}

export interface RelatorioAtividades {
  estudanteEmail: string;
  naturezas: GrupoNaturezaRelatorio[];
  totalHorasAcc: number;
  totalHorasAcex: number;
  totalHorasGeral: number;
}
