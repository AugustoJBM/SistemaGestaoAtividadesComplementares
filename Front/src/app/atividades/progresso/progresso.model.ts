export interface ProgressoModalidade {
  horasAcumuladas: number;
  horasPendentes: number;
  horasExigidas: number;
  horasRestantes: number;
  percentualConcluido: number; // 0..100
}

export interface ProgressoCargaHoraria {
  acc: ProgressoModalidade;
  acex: ProgressoModalidade;
}

// Contrato de fio (payload cru do backend, issue #67). Campos podem vir
// ausentes ou nulos quando o estudante ainda nao possui atividades.
export interface ProgressoModalidadeDTO {
  horasAcumuladas?: number | null;
  horasPendentes?: number | null;
  horasExigidas?: number | null;
  percentualConcluido?: number | null;
}

export interface ProgressoCargaHorariaDTO {
  acc?: ProgressoModalidadeDTO | null;
  acex?: ProgressoModalidadeDTO | null;
}
