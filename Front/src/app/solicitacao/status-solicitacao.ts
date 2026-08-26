import { StatusSolicitacao } from './solicitacao.model';

const ROTULOS_STATUS: Record<StatusSolicitacao, string> = {
  SUBMETIDA: 'Submetida',
  EM_ANALISE: 'Em análise',
  COM_PENDENCIAS: 'Com pendências',
  APROVADA: 'Aprovada',
  REJEITADA: 'Rejeitada'
};

const CLASSES_STATUS: Record<StatusSolicitacao, string> = {
  SUBMETIDA: 'bg-[#e1e3e4] text-[#404945]',
  EM_ANALISE: 'bg-[#cfe6ff] text-[#00497d]',
  COM_PENDENCIAS: 'bg-[#ffdfa0] text-[#7a5900]',
  APROVADA: 'bg-[#c3ecd2] text-[#00522e]',
  REJEITADA: 'bg-[#ffdad6] text-[#93000a]'
};

export function rotuloStatus(status: StatusSolicitacao): string {
  return ROTULOS_STATUS[status];
}

export function classeStatus(status: StatusSolicitacao): string {
  return CLASSES_STATUS[status];
}
