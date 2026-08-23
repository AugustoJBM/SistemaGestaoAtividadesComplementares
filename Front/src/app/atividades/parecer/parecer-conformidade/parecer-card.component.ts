import { Component, input } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ParecerResponseDTO } from '../../atividade.model';

@Component({
    selector: 'app-parecer-card',
    standalone: true,
    imports: [CommonModule, DecimalPipe],
    templateUrl: './parecer-card.component.html'
})
export class ParecerCardComponent {
    readonly parecer = input.required<ParecerResponseDTO>();
}