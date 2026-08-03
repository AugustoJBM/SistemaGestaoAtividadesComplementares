=========================================
Convenções do Projeto
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento reúne as convenções gerais adotadas pelo projeto.

Seu objetivo é estabelecer um conjunto único de regras para organização do
repositório, nomenclatura, documentação e implementação, promovendo
padronização entre todos os membros da equipe.

As convenções aqui descritas complementam os demais documentos da seção de
governança.

Idioma
======

Documentação
------------

Toda a documentação do projeto deverá ser escrita em português brasileiro.

Incluem-se:

* documentos ``.rst``;
* README, quando destinado à equipe do projeto;
* comentários em Pull Requests;
* descrições de Issues;
* descrições de Milestones;
* descrições de Releases.

Código-Fonte
------------

O código-fonte deverá utilizar o idioma inglês para:

* nomes de classes;
* interfaces;
* métodos;
* atributos;
* variáveis;
* pacotes;
* componentes;
* serviços;
* rotas da API.

Essa convenção facilita a interoperabilidade com bibliotecas e frameworks e
segue o padrão adotado pela comunidade de desenvolvimento.

Comentários
-----------

Comentários devem ser utilizados apenas quando realmente agregarem valor à
compreensão do código.

Sempre que possível, deve-se preferir nomes claros e autoexplicativos em vez de
comentários extensos.

Nomenclatura
============

Classes
--------

Utilizar PascalCase.

Exemplos:

.. code-block:: text

    StudentService

    CertificateController

Interfaces
-----------

Utilizar PascalCase.

Exemplos:

.. code-block:: text

    StudentRepository

    NotificationService

Pacotes
--------

Utilizar apenas letras minúsculas.

Exemplo:

.. code-block:: text

    br.ufape.acc.student

Métodos
--------

Utilizar camelCase.

Exemplo:

.. code-block:: text

    createCertificate()

Variáveis
----------

Utilizar camelCase.

Constantes
-----------

Utilizar UPPER_SNAKE_CASE.

Exemplo:

.. code-block:: text

    MAX_UPLOAD_SIZE

Arquivos Angular
================

Arquivos devem utilizar kebab-case.

Exemplos:

.. code-block:: text

    student-list.component.ts

    login.service.ts

Componentes Angular
===================

Cada componente deve possuir uma única responsabilidade.

Sempre que possível:

* reutilizar componentes;
* evitar duplicação;
* manter componentes pequenos.

Serviços Angular
================

Serviços devem conter apenas lógica de negócio relacionada ao frontend.

Não devem conter:

* lógica de apresentação;
* manipulação direta da interface;
* regras de negócio pertencentes ao backend.

Controladores Spring
====================

Controllers devem:

* receber requisições;
* validar entradas quando necessário;
* delegar processamento aos serviços;
* retornar respostas HTTP.

Não devem conter regras de negócio.

Serviços Spring
===============

Toda regra de negócio deve permanecer na camada de serviços.

Repositorios
============

Repositorios devem conter exclusivamente operações de persistência.

Não devem implementar regras de negócio.

DTOs
====

DTOs devem ser utilizados para comunicação entre cliente e servidor.

Entidades JPA não devem ser expostas diretamente pela API.

Documentação
============

Sempre que uma alteração modificar:

* arquitetura;
* módulos;
* fluxos;
* regras de negócio;
* API;

a documentação correspondente deverá ser atualizada.

Estrutura do Projeto
====================

Novos arquivos devem respeitar a organização definida para o projeto.

Não devem ser criados diretórios paralelos sem justificativa técnica.

Branches
=========

Toda branch deve seguir o padrão:

.. code-block:: text

    tipo/numero-descricao

Exemplo:

.. code-block:: text

    feature/58-login

Commits
========

Todas as mensagens de commit devem seguir o padrão Conventional Commits.

Exemplo:

.. code-block:: text

    feat(auth): implementar autenticação JWT

Pull Requests
=============

Todo Pull Request deve:

* utilizar o template oficial;
* referenciar a Issue correspondente;
* possuir descrição completa;
* permanecer focado em uma única Sub-Issue.

Boas Práticas
=============

Durante o desenvolvimento recomenda-se:

* escrever código simples;
* evitar duplicação;
* reutilizar componentes;
* manter métodos pequenos;
* dividir responsabilidades;
* manter documentação atualizada;
* abrir Pull Requests pequenos;
* revisar o código antes do envio.

Convenções de Organização
=========================

Toda funcionalidade deverá possuir rastreabilidade entre:

* Epic;
* Issue;
* Sub-Issue;
* Branch;
* Commits;
* Pull Request;
* Merge.

Essa rastreabilidade deve ser preservada durante todo o ciclo de vida do
projeto.

Resumo
======

As convenções apresentadas neste documento estabelecem um padrão único para
desenvolvimento, documentação e organização do projeto, promovendo
consistência, legibilidade e facilidade de manutenção ao longo de todo o ciclo
de desenvolvimento.
