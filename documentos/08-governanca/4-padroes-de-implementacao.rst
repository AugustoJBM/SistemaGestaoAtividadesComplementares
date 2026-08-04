=========================================
Padrões de Implementação
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento define os padrões técnicos utilizados durante a implementação
do software.

Seu objetivo é garantir consistência entre os desenvolvedores, facilitar a
manutenção do código e reduzir divergências de estilo durante o desenvolvimento.

Os padrões aqui definidos complementam a arquitetura do sistema e devem ser
seguidos por toda a equipe.

Objetivos
=========

Os padrões de implementação buscam:

* padronizar a escrita do código;
* facilitar revisões;
* melhorar a legibilidade;
* reduzir retrabalho;
* preservar a arquitetura do projeto.

Princípios
==========

Toda implementação deve seguir os seguintes princípios.

Responsabilidade Única
----------------------

Cada classe, componente ou serviço deve possuir apenas uma responsabilidade.

Baixo Acoplamento
-----------------

Os módulos devem possuir o menor número possível de dependências entre si.

Alta Coesão
-----------

Elementos relacionados devem permanecer agrupados.

Reutilização
------------

Sempre que possível, componentes já existentes devem ser reutilizados.

Simplicidade
------------

A solução mais simples que atenda aos requisitos deve ser priorizada.

Organização das Branches
========================

Cada branch representa exatamente uma Sub-Issue.

Formato:

.. code-block:: text

    tipo/<numero>-descricao

Exemplos:

.. code-block:: text

    feature/52-upload-certificado

    fix/103-login

    docs/84-modelo-de-dominio

Tipos permitidos:

* feature
* fix
* refactor
* docs
* test
* chore

Padrão de Commits
=================

O projeto adota o padrão Conventional Commits.

Formato:

.. code-block:: text

    tipo(escopo): descrição

Exemplos:

.. code-block:: text

    feat(auth): implementar login JWT

    fix(certificado): corrigir upload

    docs(arquitetura): atualizar módulos

Sempre que possível, o commit deve referenciar a Issue correspondente.

Exemplo:

.. code-block:: text

    feat(auth): implementar login JWT

    Refs #52

Estrutura das Alterações
========================

Cada Pull Request deve conter apenas alterações relacionadas à mesma Sub-Issue.

Não é permitido misturar:

* novas funcionalidades;
* correções;
* documentação;
* refatorações independentes.

Nomenclatura
============

Classes Java
------------

Utilizar PascalCase.

Exemplo:

.. code-block:: text

    UsuarioService

    CertificadoController

Interfaces
-----------

Utilizar PascalCase.

Exemplo:

.. code-block:: text

    UsuarioRepository

Componentes Angular
-------------------

Utilizar PascalCase para classes e kebab-case para arquivos.

Exemplo:

.. code-block:: text

    dashboard.component.ts

    upload-certificado.component.ts

Variáveis
----------

Utilizar camelCase.

Constantes
-----------

Utilizar UPPER_SNAKE_CASE apenas quando representarem constantes globais.

Pacotes
--------

Utilizar apenas letras minúsculas.

Exemplo:

.. code-block:: text

    br.ufape.acc.usuario

Organização do Código
=====================

As implementações devem respeitar a arquitetura definida na documentação.

Não é permitido:

* acessar diretamente o banco pela camada de apresentação;
* implementar regras de negócio em Controllers;
* duplicar regras de negócio em múltiplos módulos.

Documentação do Código
======================

Comentários devem ser utilizados apenas quando realmente agregarem valor.

Preferir nomes claros em vez de comentários explicativos.

Código morto deve ser removido e não comentado.

Boas Práticas
=============

Sempre:

* reutilizar componentes existentes;
* reutilizar serviços;
* reutilizar DTOs quando apropriado;
* manter métodos pequenos;
* dividir responsabilidades complexas.

Evitar:

* métodos excessivamente longos;
* classes com múltiplas responsabilidades;
* duplicação de código;
* dependências desnecessárias.

Resumo
======

Os padrões de implementação estabelecem diretrizes para produção de código,
garantindo consistência entre os desenvolvedores e preservando a arquitetura
definida para o projeto.
