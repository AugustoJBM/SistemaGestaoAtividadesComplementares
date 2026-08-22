import { Categoria, Natureza } from '../atividade.model';

export interface AtividadeEdicaoRequest {
    titulo: string;
    instituicaoResponsavel?: string;
    dataRealizacao: string;
    cargaHoraria: number;
    natureza: Natureza;
    categoria: Categoria;
    arquivo?: File | null;
}