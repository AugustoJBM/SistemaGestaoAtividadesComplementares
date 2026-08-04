=========================================
Diagrama de Casos de Uso
=========================================

.. contents::
   :local:


Introdução
==========

O diagrama de casos de uso representa as principais funcionalidades do Sistema
de Gestão de Atividades Complementares e os atores que interagem com a
aplicação.

Este diagrama apresenta uma visão externa do sistema, identificando quais
usuários utilizam cada funcionalidade.


Objetivo
========

O objetivo deste diagrama é:


* identificar os atores do sistema;
* representar funcionalidades principais;
* validar requisitos funcionais;
* auxiliar definição de épicos e funcionalidades.


Atores do Sistema
=================


Estudante
---------

Representa o usuário responsável pelo gerenciamento das próprias atividades
complementares.


Principais ações:


* autenticar no sistema;
* cadastrar atividades;
* enviar certificados;
* acompanhar progresso;
* gerar relatórios.


Gestor Institucional
--------------------

Representa usuários responsáveis pela análise e acompanhamento institucional.


Principais ações:


* validar certificados;
* consultar informações;
* gerar relatórios administrativos.


Administrador
-------------

Representa o usuário responsável pela configuração e manutenção do sistema.


Principais ações:


* gerenciar usuários;
* configurar regras;
* administrar permissões.


Casos de Uso Principais
=======================


Autenticação
------------

Responsável pelo controle de acesso ao sistema.


Cadastro de Atividades
----------------------

Permite que estudantes registrem atividades ACC e ACEX.


Gerenciamento de Certificados
-----------------------------

Permite envio, consulta e gerenciamento dos documentos comprobatórios.


Validação de Certificados
-------------------------

Permite análise e aprovação dos documentos enviados.


Acompanhamento Acadêmico
------------------------

Permite visualizar o progresso das atividades complementares.


Geração de Relatórios
---------------------

Permite criar documentos institucionais.


Gerenciamento de Usuários
-------------------------

Permite administração dos usuários do sistema.


Relacionamentos
===============


Estudante
---------

Interage principalmente com:


.. code-block:: text

    Autenticação

    Cadastro de atividades

    Envio de certificados

    Acompanhamento

    Geração de relatórios



Gestor Institucional
--------------------

Interage principalmente com:


.. code-block:: text

    Autenticação

    Validação de certificados

    Consulta de relatórios



Administrador
-------------

Interage principalmente com:


.. code-block:: text

    Autenticação

    Gerenciamento de usuários

    Configuração do sistema



Código PlantUML
===============

O código fonte do diagrama está localizado em:


.. code-block:: text

    07-diagramas/codigo/casos_de_uso.puml



Exemplo:


.. code-block:: text

    @startuml

    actor Estudante
    actor Gestor
    actor Administrador

    rectangle Sistema {

        usecase "Autenticar" as UC1
        usecase "Cadastrar atividade" as UC2
        usecase "Enviar certificado" as UC3
        usecase "Acompanhar progresso" as UC4
        usecase "Gerar relatório" as UC5
        usecase "Validar certificado" as UC6

    }

    Estudante --> UC1
    Estudante --> UC2
    Estudante --> UC3
    Estudante --> UC4
    Estudante --> UC5

    Gestor --> UC1
    Gestor --> UC6
    Gestor --> UC5

    Administrador --> UC1

    @enduml



Relacionamento com Requisitos
=============================

Os casos de uso estão relacionados aos requisitos funcionais definidos na
documentação de produto.


Exemplo:


.. code-block:: text

    RF01

    Usuário deve autenticar

        |

        v

    Caso de uso:

    Autenticação



    RF02

    Usuário deve enviar certificado

        |

        v

    Caso de uso:

    Gerenciamento de certificados



Relacionamento com Desenvolvimento
==================================

Os casos de uso originam funcionalidades do backlog.


Exemplo:


.. code-block:: text

    Caso de uso:

    Enviar certificado


        |

        v


    Epic:

    Gerenciamento de certificados


        |

        v


    Features:

    Upload

    Validação

    Associação


Resumo
======

O diagrama de casos de uso apresenta a visão funcional do sistema,
relacionando usuários e funcionalidades principais.

Ele serve como base para validação dos requisitos e organização do backlog de
desenvolvimento.
