===========================================
Sistema de Gestão de Atividades Complementares
===========================================

Bem-vindo à documentação oficial do **Sistema de Gestão de Atividades Complementares**.

Este projeto está sendo desenvolvido como parte da disciplina de
**Engenharia de Software** da **Universidade Federal do Agreste de Pernambuco (UFAPE)**.

O sistema tem como objetivo apoiar o gerenciamento das Atividades
Complementares Curriculares (ACC) e das Atividades Curriculares de Extensão
(ACEX), permitindo o envio de certificados, o acompanhamento da carga horária
integralizada e o gerenciamento do processo de validação das atividades pelos
responsáveis.

Esta documentação reúne todas as informações técnicas, arquiteturais e
organizacionais do projeto, servindo como principal fonte de consulta para toda
a equipe durante o ciclo de desenvolvimento.

::

   Toda decisão arquitetural, alteração significativa ou definição de processo
   deverá ser refletida nesta documentação.

=========================
Estrutura da Documentação
=========================

A documentação está organizada em oito grandes seções.

::

    documentos/

    ├── 1-inicial.rst
    │
    ├── 01-produto/
    │   ├── Visão Geral do Produto
    │   ├── Modelo de Domínio
    │   └── Requisitos
    │
    ├── 02-arquitetura/
    │   ├── Arquitetura Geral
    │   ├── Módulos do Sistema
    │   ├── Estrutura do Projeto
    │   ├── Decisões Arquiteturais
    │   ├── Backend
    │   └── Frontend
    │
    ├── 03-qualidade/
    │   └── Estratégia de Testes
    │
    ├── 04-processos/
    │   ├── Git
    │   ├── Pull Requests
    │   ├── Fluxo de Desenvolvimento
    │   ├── Metodologia Ágil
    │   └── CI/CD
    │
    ├── 05-modulos/
    │
    ├── 06-fluxos-de-negocio/
    │
    ├── 07-diagramas/
    │
    └── 08-governanca/
        ├── Índice
        ├── Fluxo de Desenvolvimento
        ├── Gerenciamento de Trabalho
        ├── Padrões de Implementação
        ├── Configuração do Repositório
        ├── Controle de Qualidade
        └── Convenções

Cada seção documenta um aspecto específico do projeto, desde os requisitos de
negócio até a governança do desenvolvimento.

=========================
Objetivos da Documentação
=========================

A documentação possui os seguintes objetivos:

* centralizar todas as informações técnicas do projeto;
* registrar as decisões arquiteturais;
* documentar os requisitos funcionais e não funcionais;
* padronizar o processo de desenvolvimento;
* definir padrões de implementação e organização do código;
* apoiar o gerenciamento do projeto;
* facilitar a integração de novos integrantes;
* servir como referência durante toda a evolução do software.

=========================
Público-Alvo
=========================

Esta documentação destina-se principalmente a:

* Product Owners;
* equipe de desenvolvimento;
* avaliadores do projeto;
* futuros colaboradores.

=========================
Organização da Equipe
=========================

O projeto é desenvolvido de forma colaborativa entre os Product Owners e a
Equipe de Desenvolvimento, possuindo responsabilidades distintas e
complementares.

**Product Owners**

Os Product Owners representam os solicitantes das funcionalidades do sistema.

Neste projeto, esse papel é desempenhado pelos professores e monitores da
disciplina.

Suas responsabilidades são:

* criar Issues representando novas demandas;
* esclarecer dúvidas sobre as funcionalidades solicitadas;
* validar as funcionalidades entregues pela equipe.

Os Product Owners possuem liberdade para criar as Issues da forma que julgarem
mais adequada, não sendo obrigados a seguir modelos, templates ou padrões
técnicos definidos para a equipe de desenvolvimento.

Os Product Owners não participam das atividades técnicas do projeto, como
planejamento da Sprint, refinamento, definição da arquitetura, implementação,
revisão de código ou integração das alterações.

**Equipe de Desenvolvimento**

A equipe de desenvolvimento é responsável por todas as atividades técnicas do
projeto.

Entre suas responsabilidades estão:

* analisar as Issues criadas pelos Product Owners;
* realizar o refinamento técnico;
* criar as Sub-Issues;
* definir a arquitetura da solução;
* organizar e distribuir as atividades da Sprint;
* implementar funcionalidades;
* desenvolver testes;
* revisar Pull Requests;
* manter a documentação atualizada;
* integrar as alterações ao repositório principal.

A equipe possui responsabilidade compartilhada sobre a qualidade do software e
pela correta implementação das funcionalidades solicitadas.

Durante cada Sprint poderão existir papéis rotativos relacionados à revisão de
código, documentação, qualidade ou coordenação técnica, sem prejuízo das
atividades de desenvolvimento desempenhadas por todos os integrantes.

=========================
Fluxo Geral do Projeto
=========================

O projeto segue um processo de desenvolvimento incremental baseado em
Metodologia Ágil.

O fluxo geral pode ser resumido da seguinte forma.

::

    Product Backlog
            │
            ▼
      Planejamento da Sprint
            │
            ▼
       Refinamento Técnico
            │
            ▼
      Criação das Sub-Issues
            │
            ▼
      Desenvolvimento
            │
            ▼
        Pull Request
            │
            ▼
      Revisão de Código
            │
            ▼
          Correções
            │
            ▼
          Integração
            │
            ▼
      Revisão da Sprint
            │
            ▼
     Retrospectiva

Os detalhes desse fluxo encontram-se documentados na seção
``08-governanca``.

=========================
Princípios da Documentação
=========================

Toda documentação deste projeto deve obedecer aos seguintes princípios:

* manter consistência com a implementação;
* evoluir juntamente com o software;
* evitar duplicação de informações;
* registrar decisões arquiteturais relevantes;
* manter rastreabilidade entre requisitos, implementação e documentação.

A documentação deve sempre representar o estado atual do projeto.

=========================
Documentação Oficial
=========================

Toda a documentação oficial encontra-se versionada neste repositório.

Documentos externos poderão ser utilizados como material de apoio, mas não
substituem esta documentação como fonte oficial de informações do projeto.

Alterações relevantes na arquitetura, nos requisitos, na organização da equipe
ou nos processos de desenvolvimento deverão ser registradas juntamente com as
respectivas alterações no código-fonte.

=========================
Controle de Versões
=========================

A documentação evolui em conjunto com o software.

Sempre que houver alterações significativas em qualquer módulo do sistema, os
documentos correspondentes deverão ser revisados e atualizados.

Essa prática garante que a documentação permaneça consistente, confiável e útil
durante todo o ciclo de vida do projeto.

=========================
Licença
=========================

Esta documentação é distribuída sob a mesma licença adotada pelo repositório
principal do projeto.
