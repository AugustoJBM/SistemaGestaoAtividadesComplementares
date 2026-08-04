=========================================
Fluxos de Negócio
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Esta seção apresenta os principais fluxos de negócio do Sistema de Gestão de
Atividades Complementares.

Os fluxos descrevem como usuários interagem com o sistema para realizar
atividades relacionadas ao gerenciamento de ACC e ACEX.


Diferentemente da documentação de arquitetura, que apresenta a estrutura
técnica da aplicação, os fluxos de negócio representam o comportamento esperado
do sistema considerando a perspectiva dos usuários.


====================
Objetivo
====================

A documentação dos fluxos possui como objetivo:


* representar processos realizados pelos usuários;
* facilitar entendimento das regras de negócio;
* auxiliar desenvolvimento das funcionalidades;
* servir como base para criação de requisitos e testes;
* apoiar criação de diagramas.


====================
Conceito de Fluxo de Negócio
==============================

Um fluxo de negócio representa uma sequência de ações realizadas por usuários
ou pelo próprio sistema para atingir determinado objetivo.


Exemplo:


::

    Estudante envia certificado


        |

        v


    Sistema valida arquivo


        |

        v


    Certificado associado à atividade


        |

        v


    Progresso atualizado



====================
Importância no Desenvolvimento
==============================

Os fluxos de negócio auxiliam:


Desenvolvedores
---------------

Permitem compreender o comportamento esperado antes da implementação.


Testadores
----------

Permitem criar cenários de teste baseados em ações reais.


Product Owner
-------------

Permitem validar se as funcionalidades entregam valor esperado.


====================
Padrão de Documentação
=======================

Cada fluxo possui:


Objetivo
--------

Descrição do propósito do processo.


Atores
------

Usuários ou componentes envolvidos.


Pré-condições
-------------

Condições necessárias antes da execução.


Fluxo Principal
---------------

Sequência esperada de execução.


Fluxos Alternativos
-------------------

Comportamentos diferentes do caminho principal.


Regras de Negócio
-----------------

Restrições aplicadas ao processo.


Critérios de Aceitação
----------------------

Condições para considerar a funcionalidade concluída.


====================
Fluxos Documentados
===================


Cadastro de Atividade
=====================

Responsável pelo registro de atividades complementares realizadas pelo
estudante.


Arquivo:


::

    2-fluxo-cadastro-atividade.rst



Envio de Certificado
====================

Responsável pelo envio dos documentos comprobatórios.


Arquivo:


::

    3-fluxo-envio-certificado.rst



Validação de Certificado
========================

Responsável pelo processo de análise e aprovação documental.


Arquivo:


::

    4-fluxo-validacao-certificado.rst



Acompanhamento do Estudante
===========================

Responsável pela visualização do progresso acadêmico.


Arquivo:


::

    5-fluxo-acompanhamento-estudante.rst



Geração de Relatório
====================

Responsável pela emissão de documentos institucionais.


Arquivo:


::

    6-fluxo-geracao-relatorio.rst



====================
Relacionamento com Módulos
==========================


Os fluxos utilizam os módulos definidos anteriormente.


Exemplo:


::

    Fluxo de envio de certificado


            |


            v


    Módulo de Certificados


            |


            v


    Módulo de Atividades


            |


            v


    Módulo de Acompanhamento



====================
Relacionamento com Desenvolvimento
=================================

Cada fluxo pode originar:


* Epic;
* Feature;
* Issue;
* Caso de teste.


Exemplo:


::

    Fluxo:


    Envio de certificado


          |


          v


    Epic:


    Gerenciamento de certificados


          |


          v


    Features:


    - Upload


    - Validação


    - Consulta



====================
Resumo
====================

Os fluxos de negócio representam o comportamento funcional do sistema.

Eles conectam os requisitos do produto com os módulos implementados e servem
como referência para desenvolvimento, testes e evolução da aplicação.
