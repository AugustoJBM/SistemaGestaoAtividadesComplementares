=========================================
Módulo de Atividades Complementares
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de atividades complementares é responsável pelo gerenciamento das
atividades acadêmicas realizadas pelos estudantes da UFAPE.

Este módulo permite registrar, classificar e acompanhar atividades dos tipos
ACC (Atividades Curriculares Complementares) e ACEX (Atividades de Extensão).


====================
Objetivo
====================

O objetivo deste módulo é permitir que estudantes e gestores acompanhem o
cumprimento das atividades complementares exigidas pela instituição.


O módulo deve possibilitar:


* cadastro de atividades;
* classificação por natureza;
* controle de carga horária;
* acompanhamento do progresso;
* validação das informações cadastradas.


====================
Usuários Envolvidos
====================


Estudante
=========

Responsável por cadastrar e acompanhar suas próprias atividades.


Gestor Institucional
====================

Responsável por acompanhar atividades e validar informações quando necessário.


Administrador
=============

Responsável pela configuração das regras do sistema.


====================
Responsabilidades
==================


Gerenciamento de Atividades
---------------------------

Controlar todas as atividades complementares cadastradas pelos estudantes.


Classificação das Atividades
----------------------------

Permitir categorizar atividades conforme sua natureza.


Exemplos:


::

    Ensino


    Pesquisa


    Extensão


    Monitoria


    Eventos



Controle de Carga Horária
-------------------------

Controlar a quantidade de horas realizadas pelo estudante.


Acompanhamento Institucional
----------------------------

Permitir verificar o progresso do estudante em relação aos requisitos
estabelecidos.


====================
Conceitos do Domínio
====================


Atividade Complementar
======================

Representa uma atividade realizada pelo estudante que contribui para sua
formação acadêmica.


Exemplos:


::

    Participação em evento


    Projeto de extensão


    Monitoria


    Iniciação científica



Natureza da Atividade
=====================

Representa a categoria institucional da atividade.


Exemplo:


::

    Pesquisa


    Ensino


    Extensão



Carga Horária
=============

Representa a quantidade de horas atribuídas a uma atividade.


====================
Funcionalidades
================


====================
Cadastro de Atividade
=====================

Permite ao estudante registrar uma nova atividade complementar.


Informações possíveis:


* título;
* descrição;
* natureza;
* data de realização;
* carga horária;
* documento comprobatório.


====================
Classificação por Natureza
===========================

Permite categorizar atividades conforme regras institucionais.


Exemplo:


::

    Atividade


        |

        +-- Ensino


        +-- Pesquisa


        +-- Extensão



====================
Consulta de Atividades
======================

Permite visualizar atividades cadastradas.


Possibilidades:


* listar atividades;
* filtrar por categoria;
* consultar status;
* visualizar carga horária.


====================
Atualização de Atividades
=========================

Permite modificar informações enquanto a atividade estiver em estado
permitido.


Exemplos:


* corrigir descrição;
* alterar informações;
* substituir documento.


====================
Exclusão de Atividade
=====================

Permite remover atividades cadastradas quando permitido pelas regras do
sistema.


====================
Acompanhamento de Progresso
===========================

Permite visualizar o cumprimento das atividades necessárias.


Exemplo:


::

    Categoria: Extensão


    Realizado: 80h


    Necessário: 120h


    Progresso: 66%



====================
Regras de Negócio
==================


RN-ATV-01
---------

Toda atividade deve estar associada a um estudante.


RN-ATV-02
---------

Toda atividade deve possuir uma natureza definida.


RN-ATV-03
---------

A carga horária informada deve possuir valor válido.


RN-ATV-04
---------

Uma atividade pode exigir documento comprobatório.


RN-ATV-05
---------

O limite de carga horária por categoria deve respeitar as regras institucionais.


RN-ATV-06
---------

Atividades pendentes de validação não devem ser contabilizadas como concluídas.


====================
Entidades Relacionadas
======================


Atividade
==========

Representa uma atividade complementar cadastrada.


Possíveis atributos:


::

    id


    titulo


    descricao


    natureza


    cargaHoraria


    dataRealizacao


    status



Usuário
=======

Representa o estudante responsável pela atividade.


Categoria
=========

Representa a natureza institucional da atividade.



====================
Relacionamento com Outros Módulos
==================================


Usuários
--------

Cada atividade pertence a um estudante.


::

    Usuário


       1


       |


       *


    Atividade



Certificados
------------

Atividades podem possuir documentos comprobatórios associados.


::

    Atividade


          |


          v


    Certificado



Relatórios
----------

As atividades são utilizadas para geração de relatórios.


::

    Atividades


          |


          v


    Relatórios



====================
Integrações Backend
===================


Controller
----------

Gerencia operações relacionadas às atividades.


Service
-------

Implementa regras de negócio.


Repository
----------

Realiza persistência.


DTO
---

Define comunicação entre frontend e backend.


====================
Endpoints Esperados
===================


Listar atividades
-----------------


::

    GET /api/v1/atividades



Cadastrar atividade
-------------------


::

    POST /api/v1/atividades



Consultar atividade
-------------------


::

    GET /api/v1/atividades/{id}



Atualizar atividade
-------------------


::

    PUT /api/v1/atividades/{id}



Excluir atividade
-----------------


::

    DELETE /api/v1/atividades/{id}



====================
Componentes Frontend
====================


AtividadesPage
--------------


Responsável pela visualização geral.


::

    AtividadeService



Responsável pela comunicação com API.


::

    AtividadeFormComponent



Responsável pelo cadastro e edição.


::

    AtividadeCardComponent



Responsável pela apresentação individual.



====================
Fluxo Principal
===============


Cadastro de atividade:


::

    Estudante


        |


        v


    Formulário Angular


        |


        v


    AtividadeService


        |


        v


    API REST


        |


        v


    Regras de negócio


        |


        v


    Persistência



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Gerenciamento de atividades complementares



Features
========


::

    Criar cadastro de atividade


    Implementar classificação por natureza


    Controlar carga horária


    Implementar acompanhamento de progresso



Issues
======


Exemplos:


::

    Criar entidade Atividade


    Implementar CRUD de atividades


    Criar componente de cadastro


    Implementar cálculo de progresso


    Criar testes unitários



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Estudantes conseguem cadastrar atividades.

[ ] Atividades possuem classificação.

[ ] Carga horária é calculada corretamente.

[ ] Dados possuem validação.

[ ] Atividades podem ser consultadas.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de atividades complementares representa o núcleo funcional do sistema,
permitindo o gerenciamento das atividades ACC e ACEX realizadas pelos
estudantes.

Ele integra usuários, certificados, relatórios e acompanhamento de progresso,
sendo uma das principais áreas de desenvolvimento da aplicação.
