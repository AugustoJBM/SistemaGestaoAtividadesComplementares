=========================================
Módulo de Autenticação
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de autenticação é responsável pelo controle de acesso ao Sistema de
Gestão de Atividades Complementares.

Este módulo garante que apenas usuários identificados possam acessar recursos
protegidos do sistema, além de controlar quais funcionalidades cada usuário
pode utilizar.


====================
Objetivo
====================

O objetivo deste módulo é fornecer mecanismos para:

* autenticação de usuários;
* gerenciamento de sessão;
* controle de acesso;
* identificação do usuário autenticado;
* integração entre frontend Angular e backend Spring Boot.


====================
Usuários Envolvidos
====================

Os principais usuários relacionados ao módulo são:


Estudante
=========

Usuário responsável por acessar o sistema para gerenciar suas atividades
complementares.


Administrador
=============

Usuário responsável por operações administrativas e gerenciamento do sistema.


Gestores Institucionais
=======================

Usuários responsáveis por acompanhar informações institucionais relacionadas
às atividades complementares.


====================
Responsabilidades
==================

O módulo possui as seguintes responsabilidades:


Autenticação
------------

Validar a identidade do usuário através de suas credenciais.


Exemplo:

::

    Matrícula/e-mail + senha


Gerenciamento de Sessão
-----------------------

Controlar a permanência do usuário autenticado no sistema.


Controle de Acesso
------------------

Definir quais funcionalidades podem ser acessadas por cada perfil.


Integração de Segurança
-----------------------

Comunicar-se com os mecanismos de segurança definidos no backend.


====================
Funcionalidades
================

O módulo possui as seguintes funcionalidades:


====================
Login
====================

Permite que o usuário informe suas credenciais para acessar o sistema.


Fluxo:


::

    Usuário


       |


       v


    Tela de login Angular


       |


       v


    API de autenticação


       |


       v


    Validação Spring Security


       |


       v


    Token JWT



====================
Logout
====================

Permite que o usuário encerre sua sessão.


Ações realizadas:


* remover sessão local;
* invalidar informações temporárias;
* retornar usuário para tela inicial.


====================
Controle de Permissões
=====================

Permite restringir funcionalidades conforme o perfil do usuário.


Exemplo:


::

    Estudante


        pode:


        - enviar certificado

        - consultar atividades



::

    Administrador


        pode:


        - gerenciar usuários

        - administrar categorias



====================
Recuperação de Acesso
======================

Permite recuperação de acesso quando o usuário não consegue autenticar-se.


Possíveis funcionalidades:

* redefinição de senha;
* validação de identidade;
* envio de instruções de recuperação.


====================
Regras de Negócio
==================


RN-AUTH-01
----------

Todo usuário deve possuir credenciais válidas para acessar funcionalidades
protegidas.


RN-AUTH-02
----------

Usuários devem possuir permissões compatíveis com a funcionalidade acessada.


RN-AUTH-03
----------

Tokens de autenticação possuem tempo limitado de validade.


RN-AUTH-04
----------

Tentativas inválidas de autenticação devem ser tratadas pelo sistema.


RN-AUTH-05
----------

Informações sensíveis de autenticação não devem ser armazenadas de forma
insegura.


====================
Entidades Relacionadas
======================

As principais entidades relacionadas ao módulo são:


Usuário
=======

Representa uma pessoa cadastrada no sistema.


Possui informações como:


* identificação;
* dados pessoais;
* perfil de acesso.


Perfil
======

Representa permissões associadas ao usuário.


Exemplo:


::

    ESTUDANTE


    ADMINISTRADOR



====================
Integrações Backend
===================

O módulo utiliza:


Spring Security
---------------

Responsável pela segurança da aplicação.


JWT
---

Responsável pela autenticação baseada em tokens.


API REST
--------

Responsável pela comunicação com o frontend.


====================
Endpoints Esperados
===================


Login
-----

::

    POST /api/v1/auth/login



Entrada:


::

    {
        "usuario": "exemplo",
        "senha": "123456"
    }



Saída:


::

    {
        "token": "jwt-token"
    }



Logout
------

::

    POST /api/v1/auth/logout



Usuário atual
-------------

::

    GET /api/v1/auth/me



====================
Componentes Frontend
====================

Componentes relacionados:


::

    LoginPage


Responsável pela tela de autenticação.



::

    AuthService


Responsável pela comunicação com a API.



::

    AuthGuard


Responsável pela proteção de rotas.



::

    AuthInterceptor


Responsável pelo envio automático do token.



====================
Fluxo de Autorização
====================


::

    Usuário acessa recurso


          |


          v


    Angular verifica rota


          |


          v


    JWT enviado


          |


          v


    Spring Security valida


          |


          v


    Permissão analisada


          |


          v


    Recurso liberado



====================
Relação com Desenvolvimento
===========================

Este módulo pode gerar a seguinte organização:


Epic
====

::

    Gerenciamento de autenticação e segurança



Features
========


::

    Implementar login


    Implementar JWT


    Criar proteção de rotas


    Implementar controle de permissões



Issues
======


Exemplos:


::

    Criar endpoint de login


    Criar tela Angular de login


    Implementar AuthInterceptor


    Criar testes de autenticação



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Usuários conseguem autenticar-se.

[ ] Rotas protegidas bloqueiam acesso indevido.

[ ] JWT é enviado corretamente.

[ ] Permissões são respeitadas.

[ ] Erros de autenticação possuem mensagens adequadas.

[ ] Testes automatizados foram implementados.



====================
Resumo
====================

O módulo de autenticação fornece a base de segurança do sistema.

Ele integra frontend Angular e backend Spring Boot, garantindo identificação dos
usuários e controle de acesso às funcionalidades da aplicação.
