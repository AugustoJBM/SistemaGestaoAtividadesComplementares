=========================================
Diagramas do Sistema
=========================================

.. contents::
   :local:


Introdução
==========

Esta seção apresenta os diagramas utilizados para representar diferentes
aspectos do Sistema de Gestão de Atividades Complementares.

Os diagramas têm como objetivo facilitar a compreensão da estrutura,
comportamento e relacionamento dos componentes da aplicação.


Objetivo
========

A documentação visual possui os seguintes objetivos:


* facilitar comunicação entre equipe técnica e stakeholders;
* apoiar decisões de arquitetura;
* auxiliar desenvolvimento;
* documentar decisões do projeto;
* servir como referência para manutenção.


Tipos de Diagramas
==================


Casos de Uso
------------

Representam as funcionalidades disponíveis para os diferentes usuários do
sistema.


Arquivo:

.. code-block:: text

    2-casos-de-uso.rst



Arquitetura Geral
-----------------

Representa a visão de alto nível da aplicação.


Arquivo:

.. code-block:: text

    3-arquitetura-geral.rst



Componentes
-----------

Representa a divisão interna dos principais componentes do sistema.


Arquivo:

.. code-block:: text

    4-componentes.rst



Sequência
---------

Representa a comunicação entre usuários, frontend, backend e serviços.


Arquivo:

.. code-block:: text

    5-sequencia.rst



Modelo Entidade-Relacionamento
------------------------------

Representa as entidades principais e seus relacionamentos.


Arquivo:

.. code-block:: text

    6-modelo-entidade-relacionamento.rst



Fluxos Principais
-----------------

Representa processos importantes do sistema.


Arquivo:

.. code-block:: text

    7-fluxos-principais.rst



Padrão de Documentação
======================

Cada diagrama deve possuir:


Objetivo
--------

Descrição do motivo de existência do diagrama.


Elementos Representados
-----------------------

Descrição dos componentes presentes.


Relacionamento
--------------

Explicação das conexões existentes.


Código do Diagrama
------------------

Quando aplicável, o código fonte utilizado para geração.


Exemplo:

.. code-block:: text

    Código PlantUML

    ou

    Código Mermaid



Relacionamento com Arquitetura
==============================

Os diagramas representam visualmente decisões documentadas anteriormente.


Exemplo:


.. code-block:: text

    Arquitetura

        |

        v

    Componentes

        |

        v

    Sequência

        |

        v

    Implementação



Ferramentas
========================


PlantUML
--------

Utilizado para diagramas UML versionáveis no Git.


Draw.io
-------

Utilizado para diagramas visuais mais complexos.


Versionamento
=============

Os diagramas devem permanecer dentro do repositório do projeto para garantir:


* histórico de alterações;
* sincronização entre desenvolvedores;
* rastreabilidade das decisões.


Resumo
======

A seção de diagramas complementa a documentação textual do sistema,
fornecendo representações visuais da arquitetura, comportamento e estrutura da
aplicação.
