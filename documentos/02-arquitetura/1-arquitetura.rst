=========================================
Arquitetura do Sistema
=========================================

.. contents::
   :local:
   :depth: 2

Este documento apresenta a arquitetura geral do Sistema de Gestão de Atividades Complementares.

====================
Introdução
====================

Seu objetivo é definir a organização estrutural da aplicação, os princípios arquiteturais adotados, a comunicação entre os componentes do sistema e as diretrizes que deverão ser seguidas durante todo o desenvolvimento.

A arquitetura foi projetada priorizando simplicidade, modularidade, facilidade de manutenção e evolução futura.

====================
Objetivos Arquiteturais
====================

A arquitetura do sistema foi definida considerando os seguintes objetivos.

* Alta coesão entre componentes.
* Baixo acoplamento entre módulos.
* Facilidade de manutenção.
* Facilidade para inclusão de novas funcionalidades.
* Facilidade de testes.
* Escalabilidade futura.
* Reutilização de código.
* Organização clara do domínio.

====================
Princípios Arquiteturais
====================

Durante o desenvolvimento serão observados os seguintes princípios.

Separação de responsabilidades

Cada módulo deverá possuir responsabilidades claramente definidas.

Modularidade

Cada domínio do sistema será implementado como um módulo independente.

Baixo Acoplamento

Os módulos devem possuir o menor número possível de dependências diretas.

Alta Coesão

Cada módulo deverá concentrar apenas funcionalidades relacionadas ao seu domínio.

Evolução Incremental

A arquitetura deverá permitir novas funcionalidades sem grandes modificações na estrutura existente.

====================
Estilo Arquitetural
====================

O sistema adotará o padrão **Monólito Modular (Modular Monolith)**.

Embora toda a aplicação seja executada como um único sistema, ela será organizada internamente em módulos independentes.

Cada módulo possuirá:

* responsabilidades próprias;
* serviços próprios;
* controladores próprios;
* entidades próprias;
* regras de negócio próprias.

Essa organização reduz o acoplamento interno e permite uma possível migração para microserviços no futuro.

====================
Motivação da Escolha
====================

A opção por um Monólito Modular foi baseada nos seguintes fatores.

* equipe reduzida;
* simplicidade de implantação;
* facilidade de depuração;
* menor complexidade operacional;
* inexistência de necessidade de escalabilidade distribuída;
* possibilidade futura de extração de módulos para microserviços.

Essa abordagem oferece grande parte dos benefícios da modularização sem introduzir a complexidade inerente a arquiteturas distribuídas.

====================
Arquitetura Geral
====================

A arquitetura completa do sistema encontra-se detalhada nos documentos desta seção.

.. toctree::
   :maxdepth: 2

   c4_context
   c4_container
   c4_component
   backend
   frontend
   modules
   package_structure
   security
   database

====================
Visão Geral
====================

A organização da aplicação segue a estrutura abaixo.

::

                    Navegador
                         │
                         ▼
                    Angular Frontend
                         │
                    REST API
                         │
                         ▼
                  Spring Boot Backend
                         │
             ┌───────────┼────────────┐
             │           │            │
             ▼           ▼            ▼
        Módulo A    Módulo B     Módulo C
             │
             ▼
        PostgreSQL

====================
Organização em Camadas
====================

A aplicação será organizada segundo uma arquitetura em camadas.

::

    Presentation Layer

            │

    Application Layer

            │

      Domain Layer

            │

    Persistence Layer

Cada camada possui responsabilidades específicas.

====================
Documentos Relacionados
====================

Os próximos documentos detalham individualmente cada aspecto da arquitetura.

* Contexto do sistema.
* Containers.
* Componentes.
* Backend.
* Frontend.
* Organização dos módulos.
* Estrutura de pacotes.
* Segurança.
* Persistência.
