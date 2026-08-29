import { Notificacao } from './notificacao.model';

export let NOTIFICACOES_MOCK: Notificacao[] = [
  {
    id: 1,
    tipo: 'SOLICITACAO_APROVADA',
    titulo: 'Solicitação de Validação Homologada',
    mensagem:
      'Sua solicitação #7 de validação de atividades complementares (ACC) foi aprovada integralmente pela comissão.',
    solicitacaoId: 7,
    lida: false,
    dataCriacao: '2026-08-28T14:30:00',
  },
  {
    id: 2,
    tipo: 'SOLICITACAO_COM_PENDENCIAS',
    titulo: 'Pendências na Solicitação #8',
    mensagem:
      'O avaliador solicitou ajuste no comprovante do item "Projeto de Extensão": anexo ilegível ou incompleto.',
    solicitacaoId: 8,
    lida: false,
    dataCriacao: '2026-08-27T10:15:00',
  },
  {
    id: 3,
    tipo: 'SOLICITACAO_EM_ANALISE',
    titulo: 'Solicitação em Análise',
    mensagem: 'Sua solicitação #9 entrou na fila de análise da coordenação acadêmica.',
    solicitacaoId: 9,
    lida: true,
    dataCriacao: '2026-08-25T08:00:00',
  },
  {
    id: 4,
    tipo: 'SOLICITACAO_REJEITADA',
    titulo: 'Solicitação Recusada',
    mensagem:
      'A solicitação #6 foi rejeitada pois ultrapassou o prazo regulamentar do semestre letivo.',
    solicitacaoId: 6,
    lida: true,
    dataCriacao: '2026-08-20T16:45:00',
  },
  {
    id: 5,
    tipo: 'SOLICITACAO_SUBMETIDA',
    titulo: 'Comprovante de Envio',
    mensagem: 'Relatório geral de atividades acadêmicas submetido com sucesso no sistema.',
    solicitacaoId: null,
    lida: true,
    dataCriacao: '2026-08-15T09:20:00',
  },
];

export function resetarMocks(): void {
  NOTIFICACOES_MOCK = NOTIFICACOES_MOCK.map((n) => ({ ...n }));
}
