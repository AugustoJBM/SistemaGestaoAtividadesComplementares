=========================================
Módulos do Sistema
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento apresenta a divisão funcional do Sistema de Gestão de
Atividades Complementares em módulos de negócio.

A modularização tem como objetivo reduzir o acoplamento entre diferentes partes
do sistema, facilitar a manutenção do código e permitir que múltiplos
desenvolvedores trabalhem simultaneamente em funcionalidades distintas.

Cada módulo representa um conjunto coeso de responsabilidades relacionadas a um
mesmo domínio de negócio.

====================
Objetivos da Modularização
====================

A divisão do sistema em módulos busca atingir os seguintes objetivos.

* Separar responsabilidades de negócio.
* Facilitar a manutenção do sistema.
* Reduzir dependências entre funcionalidades.
* Permitir evolução incremental.
* Favorecer o desenvolvimento paralelo.
* Organizar o código-fonte de forma consistente.
* Facilitar futuros processos de refatoração.

====================
Princípios
====================

A organização modular deverá seguir os seguintes princípios.

Alta Coesão
-----------

Cada módulo deverá conter apenas responsabilidades relacionadas ao seu domínio.

Baixo Acoplamento
-----------------

Os módulos devem depender o mínimo possível uns dos outros.

Encapsulamento
--------------

Cada módulo será responsável por proteger suas regras de negócio, expondo apenas
as operações necessárias aos demais módulos.

Responsabilidade Única
----------------------

Cada módulo deverá possuir um propósito claramente definido.

====================
Visão Geral dos Módulos
====================

O sistema será dividido nos seguintes módulos.

::

    Autenticação e Autorização

    Usuários

    Atividades Complementares

    Certificados

    Avaliações

    Relatórios

    Notificações

    Administração

Cada módulo possui responsabilidades específicas descritas nas seções
seguintes.

====================
Módulo de Autenticação e Autorização
====================

Objetivo
--------

Gerenciar a identidade dos usuários e controlar o acesso aos recursos do
sistema.

Responsabilidades
-----------------

* autenticação;
* autorização;
* recuperação de senha;
* gerenciamento de sessão;
* controle de permissões;
* gerenciamento de papéis.

Principais funcionalidades
--------------------------

* login;
* logout;
* alteração de senha;
* redefinição de senha;
* controle de acesso baseado em perfis.

Dependências
------------

Este módulo poderá ser utilizado pelos demais módulos, mas não dependerá das
regras de negócio específicas deles.

====================
Módulo de Usuários
====================

Objetivo
--------

Gerenciar as informações dos usuários do sistema.

Responsabilidades
-----------------

* cadastro;
* atualização;
* consulta;
* gerenciamento de perfis.

Principais entidades
--------------------

* Usuário;
* Estudante;
* Avaliador;
* Administrador.

====================
Módulo de Atividades Complementares
====================

Objetivo
--------

Gerenciar todas as atividades complementares cadastradas pelos estudantes.

Responsabilidades
-----------------

* cadastro;
* edição;
* consulta;
* exclusão;
* categorização;
* cálculo de carga horária.

Principais entidades
--------------------

* Atividade Complementar;
* Categoria de Atividade.

====================
Módulo de Certificados
====================

Objetivo
--------

Gerenciar os documentos comprobatórios enviados pelos estudantes.

Responsabilidades
-----------------

* upload;
* armazenamento;
* download;
* substituição;
* visualização.

Principais entidades
--------------------

* Certificado.

====================
Módulo de Avaliações
====================

Objetivo
--------

Gerenciar o fluxo de análise das atividades submetidas.

Responsabilidades
-----------------

* submissão;
* aprovação;
* rejeição;
* solicitação de correção;
* histórico de avaliações.

Principais entidades
--------------------

* Solicitação;
* Avaliação.

====================
Módulo de Relatórios
====================

Objetivo
--------

Gerar documentos e informações consolidadas sobre o progresso do estudante.

Responsabilidades
-----------------

* emissão de relatórios;
* acompanhamento da carga horária;
* consolidação de informações;
* exportação de documentos.

Principais entidades
--------------------

* Relatório.

====================
Módulo de Notificações
====================

Objetivo
--------

Informar usuários sobre eventos relevantes ocorridos durante o uso do sistema.

Responsabilidades
-----------------

* envio de notificações;
* histórico de notificações;
* gerenciamento de mensagens.

Principais entidades
--------------------

* Notificação.

====================
Módulo de Administração
====================

Objetivo
--------

Disponibilizar funcionalidades administrativas para gerenciamento do sistema.

Responsabilidades
-----------------

* gerenciamento de categorias;
* parametrização do sistema;
* gerenciamento de usuários;
* manutenção de configurações.

====================
Dependências Entre os Módulos
====================

As dependências entre módulos deverão ser mínimas.

De forma conceitual, as principais relações são apresentadas abaixo.

::

        Autenticação
             │
             ▼
          Usuários
             │
             ▼
         Atividades
              │
        ┌────┴────┐
        ▼          ▼
  Certificados  Avaliações
        │           │
        └────┬────┘
              ▼
         Relatórios
              │
              ▼
         Notificações

O módulo de Administração atua de forma transversal, oferecendo suporte aos
demais módulos quando necessário.

====================
Comunicação Entre Módulos
====================

A comunicação deverá ocorrer preferencialmente por meio de interfaces e serviços
bem definidos.

Os módulos não deverão acessar diretamente detalhes internos de outros módulos.

Sempre que possível, cada módulo deverá expor apenas operações relacionadas às
suas responsabilidades.

====================
Organização do Desenvolvimento
====================

A divisão modular não implica que um desenvolvedor seja permanentemente
responsável por um módulo específico.

As funcionalidades serão implementadas de forma incremental e distribuídas entre
os integrantes da equipe durante as Sprints.

Uma mesma Sprint poderá conter tarefas pertencentes a diferentes módulos, desde
que respeitadas as dependências definidas no planejamento.

====================
Resumo
====================

A arquitetura modular adotada pelo Sistema de Gestão de Atividades
Complementares busca organizar o domínio de negócio em componentes coesos,
reduzindo o acoplamento entre funcionalidades e facilitando o desenvolvimento
colaborativo.

Essa organização servirá como base para a estrutura dos pacotes do backend,
das funcionalidades do frontend, das APIs e do planejamento das Epics e Issues
do projeto.
