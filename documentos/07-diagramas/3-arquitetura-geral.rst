=========================================
Diagrama de Arquitetura Geral
=========================================

.. contents::
   :local:


Introdução
==========

O diagrama de arquitetura geral representa a visão de alto nível do Sistema de
Gestão de Atividades Complementares.

Ele apresenta os principais elementos da solução e como eles se comunicam,
permitindo compreender a organização geral da aplicação.


Objetivo
========

O objetivo deste diagrama é:


* apresentar a estrutura principal do sistema;
* demonstrar a separação entre frontend e backend;
* representar os módulos principais da aplicação;
* documentar decisões arquiteturais.


Visão Geral da Arquitetura
==========================

O sistema segue uma arquitetura cliente-servidor composta por:


Frontend
--------

Desenvolvido utilizando Angular com TypeScript e Tailwind CSS.

Responsável por:


* interface com usuários;
* apresentação de informações;
* interação com APIs;
* controle de navegação.


Backend
-------

Desenvolvido utilizando Java com Spring Boot.

Responsável por:


* regras de negócio;
* autenticação;
* autorização;
* processamento das funcionalidades;
* comunicação com persistência.


Persistência
------------

Responsável pelo armazenamento das informações da aplicação.


Comunicação
===========

A comunicação entre frontend e backend ocorre através de uma API REST.


Fluxo:


.. code-block:: text

    Usuário

        |

        v

    Angular

        |

        v

    API REST

        |

        v

    Spring Boot

        |

        v

    Persistência



Camada Frontend
===============


Angular Application
-------------------

Responsável pela aplicação cliente.


Principais responsabilidades:


* componentes;
* páginas;
* serviços HTTP;
* gerenciamento de estado;
* autenticação no cliente.


Tecnologias:


.. code-block:: text

    Angular

    TypeScript

    Tailwind CSS



Camada Backend
==============


Spring Boot Application
-----------------------

Responsável pela lógica da aplicação.


Organização:


.. code-block:: text

    Controllers

          |

          v

    Services

          |

          v

    Repositories

          |

          v

    Persistência



Módulos Backend
===============


Autenticação e Segurança
------------------------

Responsável pelo controle de acesso.


Usuários
--------

Responsável pelo gerenciamento dos usuários.


Atividades Complementares
-------------------------

Responsável pelo cadastro e gerenciamento das atividades.


Certificados
------------

Responsável pelos documentos comprobatórios.


Acompanhamento
--------------

Responsável pelos cálculos de progresso.


Relatórios
----------

Responsável pela geração de documentos.


Notificações
------------

Responsável pela comunicação de eventos.


Persistência
============

A camada de persistência é responsável pela comunicação com o banco de dados.


Responsabilidades:


* armazenamento de entidades;
* consultas;
* atualizações;
* gerenciamento de dados.


Código PlantUML
===============

O código fonte do diagrama está localizado em:


.. code-block:: text

    07-diagramas/codigo/arquitetura_geral.puml



Código:


.. code-block:: text

    @startuml

    actor Usuario

    package "Frontend" {

        component "Angular\nTypeScript\nTailwind" as frontend

    }


    package "Backend - Spring Boot" {

        component "API REST" as api

        component "Segurança" as security

        component "Usuários" as users

        component "Atividades" as activities

        component "Certificados" as certificates

        component "Acompanhamento" as tracking

        component "Relatórios" as reports

        component "Notificações" as notifications

    }


    database "Banco de Dados" as database


    Usuario --> frontend

    frontend --> api

    api --> security

    api --> users

    api --> activities

    api --> certificates

    api --> tracking

    api --> reports

    api --> notifications


    users --> database

    activities --> database

    certificates --> database

    tracking --> database

    reports --> database

    notifications --> database


    @enduml



Relacionamento com Arquitetura
==============================

Este diagrama representa as decisões documentadas na seção:


.. code-block:: text

    02-arquitetura/


Incluindo:


* separação frontend/backend;
* organização modular;
* responsabilidades dos componentes.


Relacionamento com Desenvolvimento
==================================

O diagrama auxilia na divisão de trabalho dos desenvolvedores.


Exemplo:


.. code-block:: text

    Desenvolvedor 1

        |

        v

    Segurança


    Desenvolvedor 2

        |

        v

    Atividades


    Desenvolvedor 3

        |

        v

    Certificados


    Desenvolvedor 4

        |

        v

    Relatórios


    Desenvolvedor 5

        |

        v

    Frontend e integração



Resumo
======

O diagrama de arquitetura geral apresenta a estrutura principal do sistema e
suas relações.

Ele serve como referência para desenvolvimento, manutenção e entendimento da
solução como um todo.
