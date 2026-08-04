=========================================
Módulo de Usuários
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de usuários é responsável pelo gerenciamento das informações dos
usuários que utilizam o Sistema de Gestão de Atividades Complementares.

Este módulo centraliza informações de identificação, dados institucionais e
perfis de acesso utilizados pelos demais módulos da aplicação.


====================
Objetivo
====================

O objetivo deste módulo é permitir:

* cadastro de usuários;
* consulta de informações;
* atualização de dados;
* associação de perfis;
* gerenciamento das informações institucionais.


====================
Usuários Envolvidos
====================


Estudante
=========

Usuário principal do sistema.

Utiliza a aplicação para acompanhar suas atividades complementares, enviar
certificados e consultar sua situação.


Administrador
=============

Responsável pelo gerenciamento dos usuários e configurações administrativas.


Gestor Institucional
====================

Responsável pelo acompanhamento de informações relacionadas aos estudantes e
suas atividades.


====================
Responsabilidades
==================

O módulo possui as seguintes responsabilidades:


Gerenciamento de Dados Pessoais
--------------------------------

Armazenar e disponibilizar informações básicas do usuário.


Exemplos:

* nome;
* matrícula;
* e-mail institucional;
* curso.


Gerenciamento de Perfil
-----------------------

Relacionar usuários aos seus níveis de acesso.


Exemplo:


::

    Usuário


       |


       v


    Perfil


       |


       +-- ESTUDANTE

       +-- ADMINISTRADOR



Consulta de Usuários
--------------------

Permitir busca e visualização de usuários cadastrados.


Atualização de Informações
--------------------------

Permitir alteração de informações permitidas pelo sistema.


====================
Funcionalidades
================


====================
Cadastro de Usuário
===================

Permite registrar novos usuários no sistema.


Informações possíveis:


* nome completo;
* matrícula;
* e-mail;
* curso;
* perfil.


====================
Consulta de Usuário
===================

Permite visualizar informações de usuários cadastrados.


Exemplos:


::

    Buscar estudante por matrícula


    Listar usuários do sistema



====================
Atualização de Dados
====================

Permite alterar informações cadastrais.


Exemplos:


* atualização de e-mail;
* atualização de dados pessoais.


====================
Gerenciamento de Perfis
======================

Permite associar permissões conforme o papel do usuário.


Exemplo:


::

    Estudante


        acessar atividades


        enviar certificados



::

    Administrador


        gerenciar usuários



====================
Ativação e Desativação
======================

Permite controlar disponibilidade de usuários.


Exemplos:


* usuário ativo;
* usuário bloqueado;
* usuário desligado.


====================
Regras de Negócio
==================


RN-USR-01
---------

Todo usuário deve possuir uma identificação única no sistema.


RN-USR-02
---------

A matrícula do estudante não pode possuir duplicidade.


RN-USR-03
---------

Usuários devem possuir pelo menos um perfil associado.


RN-USR-04
---------

Usuários desativados não podem acessar funcionalidades protegidas.


RN-USR-05
---------

Alterações de dados sensíveis devem respeitar permissões definidas.


====================
Entidades Relacionadas
======================


Usuário
=======

Representa uma pessoa cadastrada no sistema.


Possíveis atributos:


::

    id


    nome


    email


    matricula


    curso


    senha


    status



Perfil
======

Representa o tipo de acesso do usuário.


Exemplos:


::

    ESTUDANTE


    ADMINISTRADOR


    GESTOR



====================
Relacionamento com Outros Módulos
==================================


Autenticação
------------

Utiliza usuários para validar acesso ao sistema.


::

    Autenticação


          |


          v


       Usuário



Atividades Complementares
-------------------------

Relaciona atividades ao estudante responsável.


::

    Usuário


          |


          v


    Atividades



Certificados
------------

Relaciona documentos enviados ao estudante.


::

    Usuário


          |


          v


    Certificados



====================
Integrações Backend
===================


O módulo utiliza:


Controller
----------

Responsável pelos endpoints de gerenciamento.


Service
-------

Responsável pelas regras de negócio.


Repository
----------

Responsável pela persistência dos dados.


DTO
---

Responsável pela comunicação externa.


====================
Endpoints Esperados
===================


Consultar usuário autenticado
-----------------------------


::

    GET /api/v1/usuarios/me



Buscar usuários
---------------


::

    GET /api/v1/usuarios



Consultar usuário por identificador
-----------------------------------


::

    GET /api/v1/usuarios/{id}



Atualizar usuário
-----------------


::

    PUT /api/v1/usuarios/{id}



====================
Componentes Frontend
====================


Usuários


::

    UsuarioService



Responsável pela comunicação com API.


::

    UsuarioPerfilComponent



Responsável pela apresentação das informações do usuário.


::

    UsuarioFormComponent



Responsável pela edição de dados cadastrais.



====================
Fluxo Principal
===============


Atualização de dados:


::

    Usuário acessa perfil


          |


          v


    Formulário Angular


          |


          v


    UsuarioService


          |


          v


    API REST


          |


          v


    UsuarioService Backend


          |


          v


    Persistência



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Gerenciamento de usuários



Features
========


::

    Criar cadastro de usuário


    Implementar consulta de perfil


    Implementar atualização cadastral


    Criar gerenciamento de perfis



Issues
======


Exemplos:


::

    Criar entidade Usuário


    Criar endpoint de consulta


    Criar tela de perfil


    Implementar testes de usuário



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Usuários podem ser cadastrados.

[ ] Dados possuem validação.

[ ] Perfis são associados corretamente.

[ ] Usuários podem consultar suas informações.

[ ] Permissões são respeitadas.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de usuários representa a base cadastral do sistema.

Ele fornece as informações necessárias para autenticação, controle de acesso e
associação das atividades complementares aos estudantes.
