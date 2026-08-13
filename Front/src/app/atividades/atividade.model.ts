export type NaturezaAtividade = 'ACC' | 'ACEX';

export interface CadastroAtividadeRequest {
    titulo: string;
    instituicao?: string;
    data: string;
    cargaHoraria: number;
    natureza: NaturezaAtividade;
    categoria: string;
    comprovante: File;
}