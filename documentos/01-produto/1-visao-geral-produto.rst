===========================================
Visão do Produto
===========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento apresenta a visão geral do **Sistema de Gestão de Atividades Complementares**, descrevendo os objetivos do projeto, o problema que se pretende solucionar, o público-alvo, as funcionalidades esperadas, as restrições conhecidas e o escopo inicial do sistema.

Este documento representa a principal referência para entendimento do produto e serve como base para os requisitos, arquitetura e planejamento das Sprints.

====================
Propósito
====================

O Sistema de Gestão de Atividades Complementares tem como objetivo fornecer uma plataforma web capaz de auxiliar estudantes da Universidade Federal do Agreste de Pernambuco (UFAPE) no gerenciamento das Atividades Complementares Curriculares (ACC) e das Atividades Curriculares de Extensão (ACEX).

O sistema busca simplificar o acompanhamento das horas complementares exigidas pelos cursos de graduação, centralizando em um único ambiente todas as informações relacionadas às atividades realizadas pelos estudantes.

Além de beneficiar os estudantes, o sistema também fornece mecanismos automatizados e assistidos por Inteligência Artificial para facilitar o processo de auditoria e validação das atividades pelos responsáveis institucionais.

====================
Problema
====================

Atualmente, o gerenciamento das atividades complementares normalmente envolve processos manuais, envio descentralizado de documentos, controle individual das horas cumpridas e dificuldade para acompanhar o progresso em relação aos requisitos exigidos pelo curso.

Esse cenário gera problemas como:

* perda de documentos;
* dificuldade de organização dos certificados;
* erros no cálculo da carga horária;
* dificuldade de acompanhar o percentual concluído;
* demora no processo de avaliação;
* falta de transparência durante a validação das atividades.

====================
Solução Proposta
====================

O projeto propõe uma plataforma web capaz de centralizar todo o processo de gerenciamento das atividades complementares.

O sistema permitirá que o estudante:

* envie certificados com leitura e preenchimento assistidos por IA;
* organize suas atividades complementares;
* acompanhe sua carga horária em tempo real;
* visualize o percentual concluído em cada modalidade (ACC e ACEX);
* consulte pareceres preliminares de conformidade regulatória;
* acompanhe o status das solicitações;
* emita relatórios para formalização institucional.

Além disso, os responsáveis pela validação poderão analisar as solicitações através de uma interface dedicada com suporte a pareceres automáticos baseados nas normas oficiais da UFAPE.

====================
Objetivos do Produto
====================

Os principais objetivos do sistema são:

* centralizar o gerenciamento das atividades complementares;
* reduzir processos manuais e retrabalho de digitação;
* facilitar o envio e a validação de certificados;
* automatizar o cálculo e a conferência de carga horária com base em limites regulamentares;
* fornecer transparência e rastreabilidade durante o processo de auditoria;
* reduzir erros de classificação documental;
* facilitar a emissão de relatórios institucionais;
* melhorar a experiência do estudante e do avaliador.

====================
Público-Alvo
====================

O sistema será utilizado principalmente pelos seguintes perfis.

Estudantes

* cadastro de atividades com autopreenchimento;
* envio de certificados;
* acompanhamento do progresso;
* consulta das horas cumpridas e pendentes;
* emissão de relatórios.

Avaliadores

* análise de solicitações e documentos;
* consulta a pareceres técnicos emitidos por IA;
* aprovação de atividades;
* rejeição de atividades com justificativa;
* solicitação de correções.

Coordenadores e Administradores

* acompanhamento geral e indicadores de integralização;
* gestão da base de conhecimento regulatória (PPC e resoluções);
* parametrização das exigências horárias e categorias;
* acompanhamento de métricas de acurácia e concordância do sistema.

====================
Escopo do Produto
====================

O sistema contempla as seguintes funcionalidades principais.

Autenticação e Autorização

* cadastro de estudantes;
* login com credenciais institucionais;
* controle de acesso baseado em papéis (RBAC).

Gerenciamento de Atividades

* cadastro com autopreenchimento inteligente;
* edição e exclusão de atividades;
* consulta e filtragem por natureza e categoria.

Gerenciamento de Certificados

* upload de arquivos (PDF, PNG, JPEG);
* armazenamento seguro e visualização integrada;
* substituição de comprovantes durante a edição.

Inteligência Artificial e Auditoria Regulatória

* extração inteligente de metadados a partir de certificados (OCR/Visão);
* ingestão e vetorização semântica de regulamentos e PPCs (RAG);
* emissão de pareceres automatizados de conformidade com citação de artigos;
* monitoramento de métricas de concordância e tempo de inferência.

Dashboard e Acompanhamento

* horas acumuladas, pendentes e restantes;
* percentual de conclusão de ACC (90h) e ACEX (320h);
* visão integrada de progresso.

Relatórios

* relatório consolidado por natureza e categoria;
* discriminação de atividades realizadas para formalização acadêmica.

====================
Escopo Fora da Primeira Versão
====================

As funcionalidades abaixo não fazem parte do escopo inicial do projeto:

* aplicativo mobile nativo;
* integração direta via API com o SIGAA;
* assinatura digital ICP-Brasil;
* integração com sistemas externos de outras instituições;
* suporte a múltiplos idiomas.


====================
Benefícios Esperados
====================

Para os estudantes

* maior organização;
* redução de retrabalho;
* acompanhamento em tempo real;
* facilidade na submissão das atividades.

Para a instituição

* redução do trabalho manual;
* maior padronização;
* maior confiabilidade das informações;
* facilidade na auditoria do processo.

====================
Premissas
====================

Durante o desenvolvimento serão consideradas as seguintes premissas.

* O sistema será utilizado inicialmente apenas pela UFAPE.
* O acesso ocorrerá através de navegadores modernos.
* O armazenamento dos dados será centralizado.
* Os usuários possuirão autenticação individual.
* O projeto seguirá desenvolvimento incremental utilizando Scrum.

====================
Restrições
====================

O desenvolvimento deverá respeitar as seguintes restrições.

Tecnológicas

* Angular;
* TypeScript;
* Tailwind CSS;
* Java;
* Spring Boot;
* PostgreSQL.

Metodológicas

* Scrum;
* Kanban;
* GitHub Projects;
* Pull Requests obrigatórios;
* Revisão de código.

Organizacionais

* backlog gerenciado pelos Product Owners;
* implementação realizada por cinco desenvolvedores;
* responsabilidades rotativas durante as Sprints.

====================
Visão Geral das Funcionalidades
====================

::

                Sistema de Gestão
          de Atividades Complementares
                        │
        ┌───────────────┼───────────────┐
        │               │               │
    Autenticação     Atividades     Certificados
        │               │               │
        ├───────────────┼───────────────┤
        │               │               │
     Avaliações     Dashboard      Relatórios
                        │
                        │
                  Notificações

====================
Critérios de Sucesso
====================

O projeto será considerado bem-sucedido quando:

* permitir o gerenciamento completo das atividades complementares;
* automatizar a extração de dados e a auditoria de conformidade via normas institucionais;
* apresentar interface responsiva, intuitiva e acessível;
* manter arquitetura modular e baixo acoplamento entre os domínios;
* atender aos requisitos funcionais e não funcionais estabelecidos.


====================
Visão de Longo Prazo
====================

Embora a primeira versão seja destinada exclusivamente à UFAPE, a arquitetura do sistema será planejada para permitir futuras expansões.

Entre as possibilidades futuras estão:

* suporte a múltiplas instituições;
* integração com sistemas acadêmicos;
* notificações em tempo real;
* geração automática de documentos;
* serviços independentes para armazenamento de documentos;
* aplicações móveis;
* análise inteligente de certificados.

====================
Conclusão
====================

O Sistema de Gestão de Atividades Complementares pretende oferecer uma solução moderna, organizada e escalável para o gerenciamento das atividades complementares dos estudantes da UFAPE.

Este documento estabelece a visão geral do produto e servirá como referência para todas as decisões relacionadas aos requisitos, arquitetura e desenvolvimento do sistema durante todo o ciclo de vida do projeto.
