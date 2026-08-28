export type TipoNotificacao =
  | 'SOLICITACAO_SUBMETIDA'
  | 'SOLICITACAO_EM_ANALISE'
  | 'SOLICITACAO_COM_PENDENCIAS'
  | 'SOLICITACAO_APROVADA'
  | 'SOLICITACAO_REJEITADA';

export interface Notificacao {
  id: number;
  tipo: TipoNotificacao;
  titulo: string;
  mensagem: string;
  solicitacaoId: number | null;
  lida: boolean;
  dataCriacao: string;
}

export interface ContagemNaoLidas {
  naoLidas: number;
}
