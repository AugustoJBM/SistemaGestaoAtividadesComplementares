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

Além de beneficiar os estudantes, o sistema também deverá fornecer mecanismos para facilitar o processo de avaliação das atividades pelos responsáveis institucionais.

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

* envie certificados;
* organize suas atividades;
* acompanhe sua carga horária;
* visualize o percentual concluído em cada categoria;
* acompanhe o status das solicitações;
* emita relatórios para formalização institucional.

Além disso, os responsáveis pela validação poderão analisar as solicitações através de uma interface dedicada.

====================
Objetivos do Produto
====================

Os principais objetivos do sistema são:

* centralizar o gerenciamento das atividades complementares;
* reduzir processos manuais;
* facilitar o envio de certificados;
* automatizar o cálculo da carga horária;
* fornecer transparência durante o processo de avaliação;
* reduzir erros de documentação;
* facilitar a emissão de relatórios institucionais;
* melhorar a experiência do estudante.

====================
Público-Alvo
====================

O sistema será utilizado principalmente pelos seguintes perfis.

Estudantes

* cadastro de atividades;
* envio de certificados;
* acompanhamento do progresso;
* consulta das horas cumpridas;
* emissão de relatórios.

Avaliadores

* análise de solicitações;
* aprovação de atividades;
* rejeição de atividades;
* solicitação de correções.

Coordenadores

* acompanhamento geral;
* gerenciamento das regras institucionais;
* gerenciamento das categorias de atividades.

Administradores

* gerenciamento do sistema;
* manutenção dos usuários;
* parametrização da aplicação.

====================
Escopo do Produto
====================

O sistema contemplará inicialmente as seguintes funcionalidades.

Autenticação

* cadastro;
* login;
* recuperação de senha;
* controle de acesso.

Gerenciamento de Atividades

* cadastro;
* edição;
* exclusão;
* consulta;
* categorização.

Gerenciamento de Certificados

* upload;
* download;
* visualização;
* validação.

Avaliação

* aprovação;
* rejeição;
* solicitação de correções.

Dashboard

* horas acumuladas;
* percentual concluído;
* progresso por categoria.

Relatórios

* resumo das atividades;
* carga horária;
* documentos institucionais.

Notificações

* alterações de status;
* solicitações de correção;
* novas avaliações.

====================
Escopo Fora da Primeira Versão
====================

As funcionalidades abaixo não fazem parte do escopo inicial do projeto.

* aplicativo mobile;
* integração com SIGAA;
* assinatura digital;
* reconhecimento automático de certificados;
* inteligência artificial para validação automática;
* integração com sistemas externos;
* múltiplas instituições de ensino;
* suporte multilíngue.

Essas funcionalidades poderão ser avaliadas para versões futuras.

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
* reduzir significativamente o controle manual;
* apresentar uma interface intuitiva;
* possuir arquitetura organizada e modular;
* permitir evolução futura do sistema;
* atender aos requisitos definidos pelos Product Owners;
* manter alta qualidade de código;
* possuir documentação técnica completa.

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
