=========================================
Módulos do Sistema
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento apresenta a organização dos módulos funcionais que compõem o
Sistema de Gestão de Atividades Complementares.

Os módulos representam agrupamentos de funcionalidades relacionadas a um
determinado objetivo de negócio.

Diferentemente da arquitetura técnica, que define como o sistema é construído,
os módulos representam as capacidades entregues aos usuários.


====================
Objetivo
====================

A divisão em módulos tem como objetivo:

* organizar funcionalidades;
* facilitar manutenção;
* permitir desenvolvimento paralelo;
* relacionar funcionalidades com Epics e Features;
* facilitar planejamento das sprints.


====================
Conceito de Módulo
====================

Um módulo representa uma área funcional independente do sistema.


Cada módulo possui:

* objetivo;
* responsabilidades;
* regras de negócio;
* entidades relacionadas;
* funcionalidades;
* integrações.


Exemplo:


::

    Módulo de Certificados


        Responsável por:


        - envio de certificados

        - armazenamento

        - validação

        - consulta



====================
Organização dos Módulos
=======================

O sistema será dividido nos seguintes módulos:


====================
Módulo de Autenticação
=======================

Responsável pelo controle de acesso ao sistema.


Principais funcionalidades:

* login;
* autenticação;
* gerenciamento de sessão;
* controle de permissões.


Arquivo:


::

    2-autenticacao.rst



====================
Módulo de Usuários
===================

Responsável pelo gerenciamento das informações dos usuários.


Principais funcionalidades:

* cadastro;
* consulta;
* atualização;
* gerenciamento de perfis.


Arquivo:


::

    3-usuarios.rst



====================
Módulo de Atividades Complementares
====================================

Responsável pelo gerenciamento das atividades realizadas pelos estudantes.


Principais funcionalidades:

* cadastro de atividades;
* classificação por natureza;
* controle de carga horária;
* acompanhamento de progresso.


Arquivo:


::

    4-atividades-complementares.rst



====================
Módulo de Certificados
======================

Responsável pelo gerenciamento dos documentos comprobatórios.


Principais funcionalidades:

* upload;
* armazenamento;
* consulta;
* validação.


Arquivo:


::

    5-certificados.rst



====================
Módulo de Relatórios
=====================

Responsável pela geração de documentos e informações consolidadas.


Principais funcionalidades:

* geração de relatórios;
* emissão de documentos;
* exportação de informações.


Arquivo:


::

    6-relatorios.rst



====================
Módulo de Notificações
======================

Responsável pela comunicação de eventos importantes aos usuários.


Principais funcionalidades:

* avisos;
* atualização de status;
* alertas do sistema.


Arquivo:


::

    7-notificacoes.rst



====================
Módulo de Acompanhamento
========================

Responsável pela visualização do progresso das atividades complementares.


Principais funcionalidades:

* percentual concluído;
* acompanhamento por categoria;
* indicadores.


Arquivo:


::

    8-acompanhamento.rst



====================
Relacionamento com Arquitetura
================================

Os módulos funcionais utilizam os componentes definidos na arquitetura do
sistema.


Relacionamento:


::

    Módulo


       |


       +-- Backend


       |


       +-- Frontend


       |


       +-- Banco de Dados



====================
Relacionamento com Desenvolvimento
=================================

Cada módulo pode originar:

* Epics;
* Features;
* Issues;
* tarefas de sprint.


Exemplo:


::

    Módulo Certificados


          |


          v


    Epic: Gerenciamento de certificados


          |


          v


    Features:


        - Upload

        - Consulta

        - Validação



====================
Critérios para Criação de Módulos
==================================

Um módulo deve possuir:

* objetivo claro;
* responsabilidade definida;
* baixo acoplamento;
* possibilidade de evolução independente.


====================
Resumo
====================

A organização modular permite dividir o sistema em partes compreensíveis e
gerenciáveis.

Cada módulo representa uma capacidade funcional do sistema e serve como ponte
entre os requisitos do produto, a arquitetura técnica e o planejamento das
atividades de desenvolvimento.
