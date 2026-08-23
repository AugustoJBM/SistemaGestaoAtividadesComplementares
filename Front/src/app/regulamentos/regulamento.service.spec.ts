import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { RegulamentoService, IngestaoNormativaResponse, RegulamentoChunk } from './regulamento.service';
import { API_BASE_URL } from '../api.config';

const REGULAMENTOS_URL = `${API_BASE_URL}/regulamentos`;

describe('RegulamentoService', () => {
    let service: RegulamentoService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
            providers: [RegulamentoService, provideHttpClient(), provideHttpClientTesting()]
        });
        service = TestBed.inject(RegulamentoService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => httpMock.verify());

    it('deve ser criado com sucesso', () => {
        expect(service).toBeTruthy();
    });

    it('deve enviar POST para ingerir documento com FormData', () => {
        const arquivo = new File(['PPC content'], 'ppc.pdf', { type: 'application/pdf' });
        const mockResponse: IngestaoNormativaResponse = {
            nomeDocumento: 'ppc.pdf',
            totalChunksExtraidos: 5,
            status: 'SUCESSO',
            mensagem: 'Ingestão concluída.'
        };

        service.ingerirDocumento(arquivo, true).subscribe((res) => {
            expect(res).toEqual(mockResponse);
        });

        const req = httpMock.expectOne(`${REGULAMENTOS_URL}/ingerir?substituirExistentes=true`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body instanceof FormData).toBe(true);
        req.flush(mockResponse);
    });

    it('deve listar chunks normativos com GET', () => {
        const chunks: RegulamentoChunk[] = [
            { id: 1, artigo: 'Art. 12', conteudoTexto: 'Monitoria vale 40h' }
        ];

        service.listarRegulamentos().subscribe((res) => {
            expect(res).toEqual(chunks);
        });

        const req = httpMock.expectOne(REGULAMENTOS_URL);
        expect(req.request.method).toBe('GET');
        req.flush(chunks);
    });
});