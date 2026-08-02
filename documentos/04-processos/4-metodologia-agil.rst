=========================================
Metodologia Ágil
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento descreve a metodologia de desenvolvimento utilizada pelo
Sistema de Gestão de Atividades Complementares.

A equipe utilizará uma abordagem ágil adaptada, combinando práticas de Scrum e
Kanban para organizar o desenvolvimento, acompanhar progresso e melhorar a
comunicação entre os integrantes.


====================
Objetivos
====================

A metodologia tem como objetivos:

* organizar o trabalho da equipe;
* permitir entregas incrementais;
* facilitar acompanhamento das atividades;
* identificar problemas rapidamente;
* melhorar comunicação entre desenvolvedores e Product Owner.


====================
Modelo Adotado
====================

O projeto utilizará um modelo híbrido:


Scrum
=====

Serão utilizados conceitos como:

* planejamento de sprint;
* definição de objetivos;
* revisão das entregas;
* retrospectiva.


Kanban
======

Serão utilizados conceitos como:

* quadro visual;
* acompanhamento do fluxo;
* controle de tarefas;
* identificação de bloqueios.



====================
Organização do Trabalho
=======================

O desenvolvimento será organizado em ciclos chamados sprints.


Cada sprint possui:

* objetivo definido;
* conjunto de funcionalidades selecionadas;
* acompanhamento do progresso;
* entrega ao final do período.



====================
Sprint
====================

Uma sprint representa um ciclo de desenvolvimento com duração definida pela
equipe.


Durante uma sprint são realizadas:


Planejamento
------------

Definição das tarefas que serão desenvolvidas.


Execução
--------

Implementação das funcionalidades.


Acompanhamento
--------------

Monitoramento do progresso.


Entrega
-------

Apresentação das funcionalidades concluídas.


Retrospectiva
-------------

Avaliação do processo e identificação de melhorias.



====================
Planejamento da Sprint
======================

Antes do início de cada sprint, a equipe deverá definir:


Objetivo da Sprint
------------------

Exemplo:


::

    Implementar fluxo completo de cadastro e envio de certificados.



Tarefas selecionadas
--------------------

Issues escolhidas para desenvolvimento.


Responsáveis
------------

Cada tarefa deverá possuir um desenvolvedor responsável.


Critérios de conclusão
----------------------

Definição do que significa uma entrega finalizada.



====================
Quadro de Trabalho
===================

O projeto utilizará um quadro visual para acompanhamento das tarefas.


Colunas recomendadas:


::

    Backlog


       |


       v


    Sprint Backlog


       |


       v


    Em Desenvolvimento


       |


       v


    Em Revisão


       |


       v


    Concluído



====================
Backlog
====================

O backlog representa todas as funcionalidades e melhorias futuras do sistema.


Ele contém:

* Epics;
* Features;
* Issues;
* melhorias;
* correções.



A priorização é responsabilidade do Product Owner.


====================
Sprint Backlog
==================

Representa o conjunto de tarefas selecionadas para uma sprint.


Somente tarefas planejadas devem estar nesse fluxo.


====================
Estados das Tarefas
===================


Backlog
-------

Ainda não planejada.


Sprint Backlog
--------------

Selecionada para a sprint atual.


Em Desenvolvimento
------------------

Possui implementação em andamento.


Em Revisão
----------

Possui Pull Request aberto.


Concluído
---------

Implementada, testada e integrada.



====================
Papéis do Projeto
=================

O projeto possui três grupos principais.


====================
Product Owner
================

Responsável por:


* definir prioridades;
* criar requisitos;
* validar entregas;
* gerenciar backlog.


O Product Owner é composto por:

* dois monitores;
* um professor.


Esse papel não sofre alterações durante o projeto.


====================
Desenvolvedores
===============

A equipe possui cinco desenvolvedores.


Responsabilidades:


* implementar funcionalidades;
* criar testes;
* revisar código;
* participar das decisões técnicas.


Os desenvolvedores podem assumir diferentes responsabilidades durante as
sprints.


====================
Papéis Rotativos
=================

Durante cada sprint podem existir responsabilidades adicionais.


Responsável por Arquitetura
---------------------------

Auxilia decisões técnicas e integração dos módulos.


Responsável por Qualidade
-------------------------

Acompanha testes, cobertura e pipeline.


Responsável por Documentação
----------------------------

Mantém documentação e registros técnicos atualizados.


Esses papéis são rotativos.


====================
Daily / Acompanhamento
======================

A equipe poderá realizar reuniões curtas de acompanhamento.


Objetivo:

* identificar impedimentos;
* atualizar progresso;
* alinhar prioridades.


A discussão deve ser objetiva.


====================
Revisão da Sprint
=================

Ao final da sprint, a equipe deverá avaliar:


* funcionalidades concluídas;
* problemas encontrados;
* qualidade das entregas.


O Product Owner valida se o objetivo foi alcançado.


====================
Retrospectiva
=============

A retrospectiva tem como objetivo melhorar o processo.


Perguntas sugeridas:


O que funcionou?
---------------

Práticas positivas.


O que pode melhorar?
--------------------

Problemas encontrados.


O que será alterado?
--------------------

Ações para próximas sprints.



====================
Métricas
=========

O projeto utilizará métricas para acompanhamento.


====================
Burndown Chart
==============

O gráfico de burndown representa a quantidade de trabalho restante durante uma
sprint.


Objetivo:

Visualizar se a equipe está avançando conforme planejado.


Exemplo:


::

    Trabalho restante


        |

        |\
        | \
        |  \
        |   \
        +-----------

             Dias



====================
Velocidade da Equipe
====================

A velocidade representa a quantidade de trabalho concluído em uma sprint.


Pode ser utilizada para melhorar o planejamento futuro.


====================
Definition of Done
==================

Uma tarefa será considerada concluída quando:


[ ] Implementação finalizada.

[ ] Testes executados.

[ ] Código revisado.

[ ] Pull Request aprovado.

[ ] Documentação atualizada quando necessário.

[ ] Funcionalidade integrada.



====================
Princípios da Equipe
====================


A equipe deverá seguir:


* comunicação constante;
* pequenas entregas frequentes;
* revisão colaborativa;
* melhoria contínua;
* transparência do progresso.



====================
Resumo
====================

A metodologia ágil adotada combina práticas de Scrum e Kanban para fornecer
organização sem criar burocracia excessiva.

O modelo permite que os cinco desenvolvedores trabalhem em paralelo,
acompanhando claramente tarefas, responsabilidades e evolução do sistema.
