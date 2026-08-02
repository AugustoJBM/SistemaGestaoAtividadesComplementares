=========================================
Visão Geral da Arquitetura do Backend
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento apresenta a arquitetura adotada para o backend do Sistema de
Gestão de Atividades Complementares.

O backend será responsável por implementar as regras de negócio do sistema,
controlar o acesso aos recursos, persistir os dados e disponibilizar uma API
REST consumida pelo frontend.

A arquitetura foi concebida para privilegiar organização, modularidade,
manutenibilidade e facilidade de evolução.

====================
Objetivos
====================

A arquitetura do backend busca atender aos seguintes objetivos:

* separar claramente as responsabilidades do sistema;
* reduzir o acoplamento entre funcionalidades;
* aumentar a coesão dos módulos;
* facilitar a manutenção do código;
* permitir o desenvolvimento paralelo da equipe;
* tornar a aplicação escalável e extensível;
* padronizar a implementação de novas funcionalidades.

====================
Princípios Arquiteturais
====================

A implementação deverá respeitar os seguintes princípios.

Coesão
-------

Cada módulo deverá concentrar apenas responsabilidades relacionadas ao seu
domínio de negócio.

Acoplamento Reduzido
--------------------

Os módulos deverão comunicar-se apenas por interfaces públicas bem definidas,
evitando dependências desnecessárias.

Responsabilidade Única
----------------------

Cada classe deverá possuir apenas uma responsabilidade claramente definida.

Separação de Responsabilidades
------------------------------

Aspectos relacionados à apresentação, regras de negócio e persistência deverão
permanecer separados.

Padronização
------------

Todas as funcionalidades deverão seguir uma mesma organização estrutural.

====================
Tecnologias
====================

O backend será desenvolvido utilizando:

* Java;
* Spring Boot;
* Spring Security;
* Spring Data JPA;
* PostgreSQL;
* Maven.

Outras bibliotecas poderão ser incorporadas quando justificadas por requisitos
do projeto.

====================
Arquitetura Adotada
====================

O projeto utilizará uma arquitetura de Monólito Modular.

Cada domínio de negócio será organizado como um módulo independente dentro da
mesma aplicação.

Essa abordagem busca combinar simplicidade operacional com boa separação das
responsabilidades.

====================
Divisão em Módulos
====================

O backend será organizado nos seguintes módulos:

* Autenticação;
* Usuários;
* Atividades Complementares;
* Certificados;
* Avaliações;
* Relatórios;
* Notificações;
* Administração.

Cada módulo possuirá suas próprias classes de controle, serviços,
repositórios, entidades e objetos de transferência de dados.

====================
Organização Geral
====================

A estrutura lógica da aplicação seguirá a seguinte organização.

::

    backend
        └── src
            └── main
                └── java
                    └── br
                        └── ufape
                            └── sgac
                                ├── configuracao
                                ├── comum
                                ├── autenticacao
                                ├── usuarios
                                ├── atividades
                                ├── certificados
                                ├── avaliacoes
                                ├── relatorios
                                ├── notificacoes
                                └── administracao

Os diretórios ``configuracao`` e ``comum`` conterão elementos compartilhados
entre os módulos. Todos os demais diretórios representarão domínios de negócio.

====================
Fluxo Geral
====================

Uma requisição seguirá, de forma simplificada, o seguinte fluxo:

::

    Cliente
       │
       ▼
    Controller
       │
       ▼
    Service
       │
       ▼
    Repository
       │
       ▼
    Banco de Dados

Durante esse processo poderão ocorrer validações, verificações de segurança,
tratamento de exceções e transformações entre entidades e DTOs.

====================
Extensibilidade
====================

A arquitetura foi projetada para permitir a inclusão de novos módulos sem
impactar significativamente os módulos existentes.

Novas funcionalidades deverão ser adicionadas respeitando a estrutura definida
nesta documentação.

====================
Resumo
====================

A arquitetura do backend estabelece um conjunto de princípios, módulos e
convenções que orientarão toda a implementação da aplicação.

Os documentos subsequentes detalharão cada aspecto dessa arquitetura,
incluindo organização dos pacotes, camadas, comunicação entre módulos,
persistência, segurança, testes e demais componentes necessários ao
desenvolvimento do sistema.
