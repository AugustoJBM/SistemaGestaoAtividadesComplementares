import { SolicitacaoItem, StatusSolicitacao } from '../solicitacao/solicitacao.model';

export type DecisaoAvaliacao = 'APROVADA' | 'COM_PENDENCIAS' | 'REJEITADA';

export interface SolicitacaoAvaliadorResumo {
  id: number;
  estudanteNome: string;
  dataSubmissao: string;
  status: StatusSolicitacao;
  dataAvaliacao?: string;
  totalAtividades: number;
  cargaHorariaTotal: number;
}

export type SolicitacaoFilaItem = SolicitacaoAvaliadorResumo;

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

export type SolicitacaoDetalheAvaliacao = SolicitacaoAvaliadorDetalhe;

export interface AvaliacaoRequest {
  decisao: DecisaoAvaliacao;
  justificativa?: string;
}
