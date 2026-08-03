=========================================
Fluxo de Desenvolvimento
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento define o fluxo oficial de desenvolvimento adotado pelo projeto.

O objetivo é estabelecer um processo único para criação, implementação,
revisão e integração das funcionalidades, garantindo rastreabilidade,
organização e qualidade durante todo o ciclo de desenvolvimento.

Todo trabalho realizado pela equipe deve seguir obrigatoriamente o fluxo aqui
descrito.

Objetivos
=========

O fluxo de desenvolvimento busca:

* organizar o trabalho da equipe;
* padronizar a implementação de funcionalidades;
* garantir rastreabilidade entre requisitos e código;
* facilitar revisões;
* reduzir conflitos de integração;
* preservar a estabilidade da branch principal.

Visão Geral
===========

Todo desenvolvimento segue o fluxo abaixo.

.. code-block:: text

    Product Owners

          │

          ▼

       Criam Epics

          │

          ▼

      Criam Issues

          │

          ▼

   Refinamento da Sprint

          │

          ▼

     Criação das Sub-Issues

          │

          ▼

 Distribuição aos Desenvolvedores

          │

          ▼

     Criação das Branches

          │

          ▼

      Desenvolvimento

          │

          ▼

         Commits

          │

          ▼

      Pull Request

          │

          ▼

      Revisão Técnica

          │

          ▼

        Correções

          │

          ▼

        Aprovação

          │

          ▼

          Merge

          │

          ▼

   Encerramento da Sprint

Papéis
=======

Product Owners
--------------

O projeto possui três Product Owners fixos.

São responsáveis por:

* definir prioridades;
* criar Epics;
* criar Issues;
* esclarecer requisitos;
* validar funcionalidades entregues.

Os Product Owners não implementam funcionalidades nem realizam revisões de
código.

Desenvolvedores
---------------

Os cinco desenvolvedores são responsáveis por:

* refinar Issues;
* criar Sub-Issues;
* implementar funcionalidades;
* revisar código;
* atualizar documentação;
* participar das cerimônias da Sprint.

Papéis Rotativos
----------------

A cada Sprint são definidos papéis específicos entre os desenvolvedores.

Os papéis podem incluir:

* Arquiteto da Sprint;
* Revisor Principal;
* Responsável por Testes;
* Responsável pela Documentação.

Esses papéis mudam ao longo do projeto para distribuir conhecimento entre toda
a equipe.

Fluxo da Sprint
===============

Planejamento
------------

Durante o planejamento:

* os Product Owners apresentam as Issues;
* a equipe esclarece dúvidas;
* ocorre o refinamento técnico;
* são definidas as Sub-Issues;
* as responsabilidades são distribuídas.

Nenhum desenvolvimento inicia antes dessa etapa.

Criação das Branches
====================

Cada Sub-Issue origina exatamente uma branch.

Não é permitido utilizar uma mesma branch para implementar múltiplas
Sub-Issues.

Exemplo:

.. code-block:: text

    Sub-Issue #52

            │

            ▼

    feature/52-upload-certificado

Desenvolvimento
===============

Cada desenvolvedor trabalha exclusivamente nas Sub-Issues sob sua
responsabilidade.

Durante o desenvolvimento devem ser respeitados:

* os padrões arquiteturais;
* as convenções do projeto;
* os padrões de commit;
* as diretrizes de documentação.

Commits
========

Os commits devem:

* representar alterações pequenas;
* possuir mensagens padronizadas;
* manter histórico legível;
* referenciar a Issue correspondente sempre que possível.

Pull Requests
=============

Ao concluir a implementação, o desenvolvedor abre um Pull Request.

Cada Pull Request deve estar relacionado a:

* uma Epic;
* uma Issue;
* uma Sub-Issue;
* uma Branch.

Não são permitidos Pull Requests contendo múltiplas funcionalidades
independentes.

Revisão
========

Todo Pull Request passa por revisão técnica.

A revisão verifica:

* arquitetura;
* qualidade do código;
* aderência aos padrões;
* documentação;
* testes.

Caso necessário, alterações são solicitadas ao autor.

Merge
=====

Após aprovação:

* o Pull Request é integrado utilizando Squash and Merge;
* a branch de desenvolvimento é removida;
* a Issue é encerrada automaticamente quando aplicável.

Encerramento da Sprint
======================

Ao final da Sprint:

* todas as entregas são revisadas;
* a documentação é atualizada;
* os papéis rotativos são redefinidos;
* inicia-se o planejamento da Sprint seguinte.

Boas Práticas
=============

Durante todo o fluxo recomenda-se:

* desenvolver funcionalidades pequenas;
* abrir Pull Requests frequentemente;
* manter comunicação constante entre os membros;
* evitar branches de longa duração;
* atualizar a documentação sempre que necessário.

Resumo
======

O fluxo de desenvolvimento estabelece todas as etapas percorridas por uma
funcionalidade, desde sua criação pelos Product Owners até sua integração na
branch principal.

A adoção desse processo garante organização, rastreabilidade e qualidade no
desenvolvimento colaborativo do projeto.
