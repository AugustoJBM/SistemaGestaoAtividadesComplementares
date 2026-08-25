import { Component, computed, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SolicitacaoDetalhe, StatusSolicitacao } from '../solicitacao.model';
import { classeStatus, rotuloStatus } from '../status-solicitacao';
import { dataFormatada } from '../solicitacao.helpers';

const STATUS_COM_JUSTIFICATIVA: StatusSolicitacao[] = ['REJEITADA', 'COM_PENDENCIAS'];

@Component({
  selector: 'app-detalhe-solicitacao',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detalhe-solicitacao.component.html'
})
export class DetalheSolicitacaoComponent {
  readonly detalhe = input.required<SolicitacaoDetalhe>();

  readonly mostraJustificativa = computed<boolean>(() => {
    const solicitacao = this.detalhe();
    const texto = solicitacao.justificativa?.trim() ?? '';
    return STATUS_COM_JUSTIFICATIVA.includes(solicitacao.status) && texto.length > 0;
  });

  readonly rotuloStatus = rotuloStatus;
  readonly classeStatus = classeStatus;
  readonly dataFormatada = dataFormatada;
}
