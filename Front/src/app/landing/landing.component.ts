import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

interface ModuloDestaque {
    icone: string;
    titulo: string;
    descricao: string;
    tag: string;
}

interface EtapaFluxo {
    passo: string;
    titulo: string;
    subtitulo: string;
    icone: string;
    status: string;
}

interface PerfilAtor {
    icone: string;
    titulo: string;
    papel: string;
    acoes: string[];
}

@Component({
    selector: 'app-landing',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './landing.component.html'
})
export class LandingComponent {
    readonly modalidades = [
        {
            sigla: 'ACC',
            nome: 'Atividades Complementares Curriculares',
            horasExigidas: 90,
            descricao: 'Atividades de formação geral que complementam a grade curricular do curso.',
            categorias: ['Ensino e Monitoria', 'Iniciação Científica e Pesquisa', 'Eventos e Congressos Acadêmicos']
        },
        {
            sigla: 'ACEX',
            nome: 'Atividades Curriculares de Extensão',
            horasExigidas: 320,
            descricao: 'Ações contínuas de extensão universitária em diálogo direto com a comunidade e a sociedade.',
            categorias: ['Projetos de Extensão', 'Programas e Cursos Comunitários', 'Prestação de Serviços Tecnológicos']
        }
    ];

    readonly cicloDeVida: EtapaFluxo[] = [
        {
            passo: '01',
            titulo: 'Cadastro e Upload',
            subtitulo: 'O estudante registra a atividade e anexa o certificado digital em PDF, PNG ou JPEG.',
            icone: 'upload_file',
            status: 'ENVIADO'
        },
        {
            passo: '02',
            titulo: 'Análise Institucional',
            subtitulo: 'Avaliadores e monitores verificam a autenticidade, carga horária e compatibilidade com a categoria.',
            icone: 'fact_check',
            status: 'EM AVALIAÇÃO'
        },
        {
            passo: '03',
            titulo: 'Cálculo e Integralização',
            subtitulo: 'Após a aprovação, as horas válidas são consolidadas automaticamente no dashboard de progresso.',
            icone: 'verified',
            status: 'APROVADO'
        },
        {
            passo: '04',
            titulo: 'Emissão Institucional',
            subtitulo: 'Geração de relatório discriminado e formalizado para integralização da carga horária acadêmica.',
            icone: 'description',
            status: 'RELATÓRIO EMITIDO'
        }
    ];

    readonly modulosSistema: ModuloDestaque[] = [
        {
            icone: 'lock',
            titulo: 'Autenticação e Segurança JWT',
            descricao: 'Acesso seguro por e-mail institucional ou matrícula com controle de permissões baseado em papéis (RBAC).',
            tag: 'Segurança'
        },
        {
            icone: 'library_books',
            titulo: 'Gestão de Atividades',
            descricao: 'Cadastro estruturado com categorização por Ensino, Pesquisa, Extensão e Eventos.',
            tag: 'Atividades'
        },
        {
            icone: 'cloud_done',
            titulo: 'Repositório de Certificados',
            descricao: 'Armazenamento seguro de documentos comprobatórios com pré-visualização integrada de arquivos.',
            tag: 'Documentos'
        },
        {
            icone: 'monitoring',
            titulo: 'Acompanhamento em Tempo Real',
            descricao: 'Monitoramento detalhado de horas aprovadas, pendências em análise e percentual de conclusão das metas.',
            tag: 'Progresso'
        },
        {
            icone: 'article',
            titulo: 'Relatórios e Formalização',
            descricao: 'Consolidação das atividades com identificação oficial do estudante e data de emissão para a coordenação.',
            tag: 'Relatórios'
        },
        {
            icone: 'notifications_active',
            titulo: 'Notificações de Status',
            descricao: 'Comunicação sobre o andamento das solicitações, aprovações e eventuais pedidos de ajuste.',
            tag: 'Notificações'
        }
    ];

    readonly atores: PerfilAtor[] = [
        {
            icone: 'school',
            titulo: 'Estudantes',
            papel: 'Gerenciamento individual',
            acoes: [
                'Registro de atividades e upload de comprovantes',
                'Acompanhamento de horas aprovadas e pendências',
                'Emissão de relatório acadêmico para validação'
            ]
        },
        {
            icone: 'rate_review',
            titulo: 'Avaliadores e Docentes',
            papel: 'Análise e homologação',
            acoes: [
                'Avaliação documental e verificação de autenticidade',
                'Aprovação, rejeição ou solicitação de correções',
                'Registro do histórico de pareceres acadêmicos'
            ]
        },
        {
            icone: 'admin_panel_settings',
            titulo: 'Coordenação e Gestão',
            papel: 'Supervisão e parâmetros',
            acoes: [
                'Parametrização das exigências horárias do curso',
                'Auditoria e acompanhamento global de integralização',
                'Controle de categorias e parâmetros institucionais'
            ]
        }
    ];
}