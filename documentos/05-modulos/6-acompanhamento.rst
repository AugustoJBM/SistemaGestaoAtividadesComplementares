=========================================
Módulo de Acompanhamento
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de acompanhamento é responsável por apresentar a evolução do
estudante em relação ao cumprimento das atividades complementares exigidas pela
instituição.

Este módulo consolida informações das atividades cadastradas e certificados
validados para apresentar indicadores de progresso.


====================
Objetivo
====================

O objetivo deste módulo é permitir que estudantes acompanhem sua situação
acadêmica relacionada às atividades complementares.

O módulo deve possibilitar:


* visualização da carga horária realizada;
* comparação com carga horária necessária;
* acompanhamento por categoria;
* identificação de pendências;
* visualização do progresso geral.


====================
Usuários Envolvidos
====================


Estudante
=========

Principal usuário do módulo.

Utiliza o acompanhamento para verificar seu progresso e identificar atividades
necessárias para conclusão.


Gestor Institucional
====================

Utiliza informações consolidadas para acompanhamento acadêmico.


Administrador
=============

Responsável pela configuração das regras institucionais.


====================
Responsabilidades
==================


Cálculo de Progresso
--------------------

Calcular o percentual de conclusão das atividades complementares.


Exemplo:


::

    Carga necessária: 200h


    Carga realizada: 120h


    Progresso: 60%



Acompanhamento por Categoria
----------------------------

Permitir visualizar o progresso separado por natureza de atividade.


Exemplo:


::

    Ensino


        40% concluído



    Pesquisa


        80% concluído



    Extensão


        70% concluído



Identificação de Pendências
---------------------------

Apresentar informações que necessitam de atenção.


Exemplos:


* certificados pendentes;
* carga horária insuficiente;
* atividades não validadas.


====================
Conceitos do Domínio
====================


Progresso
=========

Representa o percentual concluído pelo estudante em relação aos requisitos
definidos.


Fórmula:


::

    progresso =

    (carga realizada / carga necessária) * 100



Categoria de Atividade
======================

Representa uma divisão das atividades complementares conforme sua natureza.


Exemplo:


::

    Ensino


    Pesquisa


    Extensão



Carga Horária
=============

Quantidade de horas contabilizadas pelo estudante.


====================
Funcionalidades
================


====================
Dashboard de Progresso
=======================

Apresenta uma visão geral da situação do estudante.


Informações exibidas:


* percentual total concluído;
* horas realizadas;
* horas restantes;
* quantidade de atividades.


====================
Progresso por Categoria
=======================

Permite analisar cada natureza de atividade separadamente.


Exemplo:


::

    Extensão


    ███████░░░


    70%



====================
Consulta de Carga Horária
=========================

Permite visualizar a distribuição das horas realizadas.


Possibilidades:


* total acumulado;
* distribuição por categoria;
* histórico.


====================
Identificação de Pendências
===========================

Apresenta informações que impedem a conclusão das atividades.


Exemplos:


::

    Certificado aguardando aprovação


    Categoria abaixo do mínimo exigido



====================
Atualização Automática
=====================

O progresso deve ser atualizado automaticamente após alterações nos módulos
relacionados.


Exemplo:


::

    Certificado aprovado


          |


          v


    Recalcular progresso


          |


          v


    Atualizar dashboard



====================
Regras de Negócio
==================


RN-ACO-01
---------

O progresso deve considerar apenas atividades válidas.


RN-ACO-02
---------

Certificados rejeitados não devem contabilizar carga horária.


RN-ACO-03
---------

A carga horária deve respeitar limites definidos por categoria.


RN-ACO-04
---------

O percentual concluído não pode ultrapassar 100%.


RN-ACO-05
---------

Alterações em atividades devem atualizar os indicadores relacionados.


====================
Entidades Relacionadas
======================


Atividade
==========

Fonte das informações de carga horária.


Certificado
===========

Define se uma atividade possui comprovação válida.


Categoria
=========

Define agrupamento das atividades.


Usuário
=======

Representa o estudante analisado.


====================
Relacionamento com Outros Módulos
==================================


Atividades Complementares
-------------------------

Responsável pelos dados utilizados no cálculo.


::

    Atividades


          |


          v


    Acompanhamento



Certificados
------------

Define quais atividades podem ser contabilizadas.


::

    Certificados


          |


          v


    Progresso



Relatórios
----------

Utiliza indicadores gerados pelo acompanhamento.


====================
Integrações Backend
===================


Controller
----------

Disponibiliza informações de acompanhamento.


Service
-------

Responsável pelos cálculos e regras.


Repository
----------

Consulta dados necessários para geração dos indicadores.



====================
Endpoints Esperados
===================


Consultar progresso geral
-------------------------


::

    GET /api/v1/acompanhamento



Consultar progresso por categoria
--------------------------------


::

    GET /api/v1/acompanhamento/categorias



Consultar pendências
--------------------


::

    GET /api/v1/acompanhamento/pendencias



====================
Componentes Frontend
====================


DashboardAcompanhamentoComponent
--------------------------------

Apresenta visão geral do progresso.


::

    ProgressoCardComponent



Exibe indicadores individuais.


::

    CategoriaProgressComponent



Exibe progresso por categoria.


::

    PendenciasComponent



Exibe itens que precisam de atenção.



====================
Fluxo Principal
===============


Visualização do progresso:


::

    Usuário acessa dashboard


          |


          v


    Angular solicita dados


          |


          v


    API de acompanhamento


          |


          v


    Cálculo de progresso


          |


          v


    Retorno dos indicadores



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Acompanhamento das atividades complementares



Features
========


::

    Criar dashboard de progresso


    Implementar cálculo de carga horária


    Criar visualização por categoria


    Implementar controle de pendências



Issues
======


Exemplos:


::

    Criar serviço de cálculo de progresso


    Criar endpoint de acompanhamento


    Criar gráficos de progresso


    Criar testes de cálculo



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Estudantes conseguem visualizar progresso.

[ ] Cálculo de carga horária funciona corretamente.

[ ] Categorias possuem acompanhamento individual.

[ ] Pendências são apresentadas.

[ ] Dados refletem atividades válidas.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de acompanhamento transforma os dados cadastrados pelo estudante em
informações de progresso e tomada de decisão.

Ele permite que o usuário compreenda sua situação acadêmica e identifique quais
atividades ainda precisam ser realizadas para conclusão dos requisitos
institucionais.
