=========================================
Padrão de Commits
=========================================

.. contents::
   :local:
   :depth: 2


Introdução
==========

Este documento define o padrão de mensagens de commit utilizado pelo projeto.

O objetivo é manter um histórico consistente, facilitar a rastreabilidade das
alterações e melhorar a integração com os recursos do GitHub.


Objetivos
=========

O padrão adotado busca:

* facilitar leitura do histórico;
* identificar rapidamente o tipo de alteração;
* relacionar commits com Issues;
* melhorar revisões de código;
* apoiar futuras automações.


Princípios
==========

Cada commit deve:

* representar uma alteração lógica;
* possuir descrição objetiva;
* estar relacionado a uma Sub-Issue;
* utilizar o padrão Conventional Commits.


Estrutura da Mensagem
=====================

Toda mensagem segue o formato abaixo.

.. code-block:: text

    tipo(escopo): descrição

    Corpo opcional

    Rodapé opcional


Exemplo:

.. code-block:: text

    feat(auth): implementar autenticação JWT


Tipos de Commit
===============

feat
----

Adiciona uma nova funcionalidade.

Exemplo:

.. code-block:: text

    feat(atividade): cadastrar atividade


fix
---

Corrige um defeito.

Exemplo:

.. code-block:: text

    fix(login): corrigir expiração do token


docs
----

Atualiza documentação.

refactor
---------

Melhora código sem alterar comportamento.

test
----

Adiciona ou modifica testes.

style
-----

Altera apenas formatação.

perf
----

Melhora desempenho.

build
-----

Altera processo de build.

chore
-----

Realiza tarefas de manutenção.

revert
------

Reverte alterações anteriores.


Escopo
=======

O escopo identifica o módulo alterado.

Exemplos:

.. code-block:: text

    auth

    usuario

    atividade

    certificado

    relatorio

    frontend

    backend

    arquitetura


Descrição
==========

A descrição deve:

* utilizar verbo no infinitivo;
* ser objetiva;
* possuir aproximadamente uma linha.

Exemplo:

.. code-block:: text

    implementar upload de certificado


Exemplos Válidos
================

.. code-block:: text

    feat(auth): implementar login JWT

    feat(certificado): adicionar upload

    fix(api): corrigir paginação

    docs(modelo): atualizar domínio

    refactor(usuario): simplificar serviço


Exemplos Inválidos
==================

.. code-block:: text

    atualização

    mudanças

    commit

    teste

    corrigindo

    ajustes


Essas mensagens não descrevem adequadamente a alteração.


Integração com GitHub
=====================

Sempre que possível, os commits devem referenciar a Issue relacionada.

Exemplo:

.. code-block:: text

    feat(auth): implementar login JWT

    Refs #52


Quando o commit concluir definitivamente a implementação, pode ser utilizada a
palavra-chave de fechamento.

Exemplo:

.. code-block:: text

    feat(auth): finalizar login JWT

    Closes #52


Boas Práticas
=============

Evitar commits muito grandes.

Preferir vários commits pequenos.

Cada commit deve representar apenas uma alteração lógica.

Evitar misturar documentação, testes e implementação no mesmo commit.


Resumo
======

O projeto utiliza Conventional Commits complementado pela referência às Issues
do GitHub, garantindo um histórico organizado, rastreável e integrado ao fluxo
de desenvolvimento.
