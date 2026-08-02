=========================================
Módulo de Relatórios
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de relatórios é responsável pela geração de informações consolidadas
sobre as atividades complementares realizadas pelos estudantes.

Este módulo permite transformar os dados registrados no sistema em documentos
e relatórios utilizados para acompanhamento, análise e formalização do processo
institucional.


====================
Objetivo
====================

O objetivo deste módulo é disponibilizar mecanismos para consulta,
consolidação e emissão de documentos relacionados às atividades complementares.

O módulo deve possibilitar:


* geração de relatórios acadêmicos;
* emissão de documentos institucionais;
* consolidação de atividades realizadas;
* apresentação de informações de carga horária;
* exportação de informações.


====================
Usuários Envolvidos
====================


Estudante
=========

Utiliza o módulo para gerar documentos relacionados às suas próprias
atividades complementares.


Gestor Institucional
====================

Utiliza relatórios para acompanhamento e análise das atividades dos estudantes.


Administrador
=============

Responsável pelo gerenciamento das funcionalidades administrativas.


====================
Responsabilidades
==================


Consolidação de Dados
---------------------

Reunir informações provenientes dos demais módulos do sistema.


Dados utilizados:


* informações do estudante;
* atividades cadastradas;
* certificados aprovados;
* carga horária contabilizada;
* progresso acadêmico.


Geração de Documentos
---------------------

Permitir criação de documentos utilizados no processo institucional.


Exemplos:


::

    Relatório de atividades complementares


    Comprovante de carga horária


    Documento de acompanhamento



Filtros e Consultas
-------------------

Permitir geração de relatórios conforme critérios definidos.


Exemplos:


* período;
* estudante;
* categoria;
* situação.


====================
Conceitos do Domínio
====================


Relatório
=========

Representa uma visão consolidada das informações armazenadas no sistema.


Documento Institucional
=======================

Representa um arquivo gerado pelo sistema utilizado para formalização de
processos acadêmicos.


Filtro
======

Representa critérios utilizados para selecionar informações específicas.


Exemplo:


::

    Curso


    Período


    Categoria



====================
Funcionalidades
================


====================
Geração de Relatório Individual
================================

Permite gerar relatório referente a um estudante específico.


Informações apresentadas:


* identificação do estudante;
* atividades realizadas;
* carga horária;
* categorias;
* situação atual.


====================
Relatório de Atividades
========================

Apresenta todas as atividades complementares registradas.


Informações:


* nome da atividade;
* natureza;
* carga horária;
* status.


====================
Relatório de Progresso
=======================

Apresenta informações do acompanhamento acadêmico.


Exemplo:


::

    Carga necessária: 200h


    Carga concluída: 150h


    Percentual: 75%



====================
Emissão de Documento
=====================

Permite gerar documentos formais utilizados pela instituição.


Possíveis formatos:


::

    PDF


    Documento digital


    Arquivo para impressão



====================
Exportação de Dados
===================

Permite disponibilizar informações para utilização externa.


Exemplos:


* exportação institucional;
* armazenamento documental.


====================
Regras de Negócio
==================


RN-REL-01
---------

Relatórios devem apresentar apenas informações autorizadas ao usuário.


RN-REL-02
---------

Dados de atividades devem considerar somente registros válidos.


RN-REL-03
---------

Documentos gerados devem possuir informações atualizadas.


RN-REL-04
---------

A geração de relatórios deve respeitar permissões de acesso.


RN-REL-05
---------

Documentos institucionais devem possuir identificação do estudante e período
correspondente.


====================
Entidades Relacionadas
======================


Usuário
=======

Identifica o estudante ou usuário relacionado ao relatório.


Atividade
==========

Fornece informações das atividades realizadas.


Certificado
===========

Fornece comprovação documental.


Acompanhamento
==============

Fornece indicadores de progresso.


Relatório
=========

Representa o resultado consolidado.


====================
Relacionamento com Outros Módulos
==================================


Atividades Complementares
-------------------------

Fornece informações das atividades.


::

    Atividades


          |


          v


    Relatórios



Certificados
------------

Fornece validações documentais.


::

    Certificados


          |


          v


    Relatórios



Acompanhamento
--------------

Fornece informações calculadas.


::

    Acompanhamento


          |


          v


    Relatórios



====================
Integrações Backend
===================


Controller
----------

Responsável pelos endpoints de geração.


Service
-------

Responsável pela consolidação das informações.


Gerador de Documentos
---------------------

Responsável pela criação dos arquivos finais.


Possíveis tecnologias:


* biblioteca de geração PDF;
* templates de documentos.


Repository
----------

Responsável pela consulta dos dados necessários.


====================
Endpoints Esperados
===================


Gerar relatório individual
--------------------------


::

    GET /api/v1/relatorios/usuario/{id}



Gerar relatório de atividades
-----------------------------


::

    GET /api/v1/relatorios/atividades



Gerar documento PDF
-------------------


::

    GET /api/v1/relatorios/{id}/download



Consultar relatórios disponíveis
--------------------------------


::

    GET /api/v1/relatorios



====================
Componentes Frontend
====================


RelatoriosPage
--------------

Tela principal de relatórios.


::

    RelatorioService



Responsável pela comunicação com API.


::

    RelatorioCardComponent



Apresenta documentos disponíveis.


::

    FiltroRelatorioComponent



Responsável pela seleção de critérios.



====================
Fluxo Principal
===============


Geração de relatório:


::

    Usuário solicita relatório


          |


          v


    Angular envia parâmetros


          |


          v


    API REST


          |


          v


    Serviço de relatório


          |


          v


    Consolidação dos dados


          |


          v


    Documento gerado



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Geração de relatórios institucionais



Features
========


::

    Criar relatório de atividades


    Implementar geração de PDF


    Criar filtros de consulta


    Implementar download de documentos



Issues
======


Exemplos:


::

    Criar serviço de relatórios


    Implementar geração PDF


    Criar componente de relatórios


    Implementar testes de geração



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Usuários conseguem gerar relatórios.

[ ] Informações estão consolidadas corretamente.

[ ] Documentos possuem formato adequado.

[ ] Permissões são respeitadas.

[ ] Downloads funcionam corretamente.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de relatórios permite transformar os dados armazenados pelo sistema
em documentos e informações úteis para estudantes e gestores institucionais.

Ele representa a etapa de formalização e apresentação dos dados das atividades
complementares, integrando diversos módulos da aplicação.
