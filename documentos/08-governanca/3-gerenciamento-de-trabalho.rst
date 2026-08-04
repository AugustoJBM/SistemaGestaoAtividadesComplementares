=========================================
Gerenciamento de Trabalho
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento define como o trabalho é organizado, planejado, distribuído e
acompanhado durante o desenvolvimento do projeto.

O gerenciamento de trabalho utiliza os recursos do GitHub para organizar o
backlog, acompanhar o progresso das Sprints e garantir a rastreabilidade entre
requisitos, implementação e entregas.

Objetivos
=========

O gerenciamento de trabalho busca:

* organizar o backlog do produto;
* priorizar funcionalidades;
* distribuir tarefas entre os desenvolvedores;
* acompanhar o progresso das Sprints;
* facilitar o planejamento;
* garantir rastreabilidade.

Hierarquia do Trabalho
======================

Todo o trabalho do projeto segue a hierarquia apresentada abaixo.

.. code-block:: text

    Produto

        │

        ▼

      Epic

        │

        ▼

      Issue

        │

        ▼

    Sub-Issue

        │

        ▼

      Branch

        │

        ▼

      Commit

        │

        ▼

   Pull Request

        │

        ▼

      Merge

Cada nível possui um objetivo específico e está relacionado ao nível anterior.

Epics
=====

Uma Epic representa um grande objetivo do sistema.

Características:

* possui alto nível de abstração;
* normalmente atravessa várias Sprints;
* é composta por diversas Issues;
* representa um módulo ou funcionalidade relevante.

Exemplos:

* Autenticação
* Atividades Complementares
* Certificados
* Relatórios
* Administração

Issues
======

As Issues representam funcionalidades completas que entregam valor ao usuário.

Cada Issue:

* pertence a uma Epic;
* possui descrição funcional;
* possui critérios de aceitação;
* possui prioridade;
* pertence a uma Milestone;
* pode ser dividida em diversas Sub-Issues.

Exemplo:

Epic

    Atividades Complementares

Issue

    Implementar cadastro de atividades

Sub-Issues
==========

As Sub-Issues representam unidades técnicas de implementação.

Cada Sub-Issue deve:

* possuir apenas um responsável;
* ser pequena;
* possuir objetivo único;
* originar exatamente uma branch;
* originar exatamente um Pull Request.

Exemplos:

* Criar entidade Activity
* Criar ActivityRepository
* Criar ActivityService
* Criar ActivityController
* Criar tela de cadastro
* Criar testes

Sprints
========

O desenvolvimento é organizado em Sprints.

Cada Sprint possui:

* objetivo;
* período definido;
* conjunto de Issues;
* Milestone correspondente.

Durante uma Sprint não devem ser adicionadas novas funcionalidades, exceto em
casos excepcionais aprovados pelos Product Owners.

Planejamento
============

Durante o planejamento da Sprint ocorre:

* apresentação das Issues;
* refinamento técnico;
* estimativa de esforço;
* criação das Sub-Issues;
* definição dos responsáveis;
* definição dos papéis rotativos.

Distribuição das Atividades
===========================

A distribuição das Sub-Issues deve buscar equilíbrio entre os desenvolvedores.

Devem ser considerados:

* conhecimento técnico;
* carga de trabalho;
* disponibilidade;
* oportunidade de aprendizado.

A distribuição não deve concentrar um módulo em apenas um desenvolvedor.

Priorização
===========

As prioridades são definidas pelos Product Owners.

Sugere-se a seguinte classificação:

* Alta
* Média
* Baixa

A equipe deve implementar primeiro as funcionalidades de maior prioridade.

Milestones
==========

Cada Sprint corresponde a uma Milestone do GitHub.

As Milestones permitem:

* acompanhar progresso;
* visualizar entregas;
* medir evolução;
* organizar releases.

GitHub Projects
===============

O acompanhamento do trabalho ocorre através do GitHub Projects.

Fluxo sugerido:

.. code-block:: text

    Backlog

        │

        ▼

    Refinamento

        │

        ▼

    Pronto

        │

        ▼

    Em Desenvolvimento

        │

        ▼

    Em Revisão

        │

        ▼

    Ajustes

        │

        ▼

    Concluído

Atualização do Quadro
=====================

Cada desenvolvedor é responsável por manter suas tarefas atualizadas.

Sempre que uma atividade mudar de estado, o GitHub Project deve refletir essa
mudança.

Responsabilidades
=================

Product Owners

* Criar Epics.
* Criar Issues.
* Priorizar funcionalidades.
* Aprovar requisitos.

Equipe de Desenvolvimento

* Refinar Issues.
* Criar Sub-Issues.
* Atualizar andamento das tarefas.
* Concluir implementações.

Rastreabilidade
===============

Todo trabalho desenvolvido deve permitir rastrear:

* Epic;
* Issue;
* Sub-Issue;
* Branch;
* Commits;
* Pull Request;
* Sprint;
* Milestone.

Resumo
======

O gerenciamento de trabalho estabelece como as funcionalidades são organizadas
desde sua concepção até sua entrega, utilizando Epics, Issues, Sub-Issues,
Sprints e os recursos de gerenciamento oferecidos pelo GitHub.
