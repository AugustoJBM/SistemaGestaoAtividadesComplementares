import { SolicitacaoItem, StatusSolicitacao } from '../solicitacao.model';

export interface SolicitacaoAvaliadorResumo {
  id: number;
  estudanteNome: string;
  dataSubmissao: string;
  status: StatusSolicitacao;
  dataAvaliacao?: string;
  totalAtividades: number;
  cargaHorariaTotal: number;
}

export interface SolicitacaoAvaliadorDetalhe {
  id: number;
  estudanteNome: string;
  estudanteEmail: string;
  dataSubmissao: string;
  status: StatusSolicitacao;
  justificativa?: string;
  dataAvaliacao?: string;
  cargaHorariaTotal: number;
  itens: SolicitacaoItem[];
}
