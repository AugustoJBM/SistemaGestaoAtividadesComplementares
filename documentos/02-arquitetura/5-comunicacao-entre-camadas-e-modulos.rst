=========================================
Comunicação entre Camadas e Módulos
=========================================

Introdução
==========

Este documento define como ocorre a comunicação entre as camadas da aplicação
e entre os diferentes módulos do sistema.

Seu objetivo é reduzir o acoplamento entre componentes, aumentar a
manutenibilidade do software e estabelecer um padrão único de implementação
para toda a equipe de desenvolvimento.

Todas as funcionalidades implementadas no projeto deverão seguir as regras
descritas neste documento.

Arquitetura Geral
=================

O sistema adota uma arquitetura em camadas organizada da seguinte forma.

::

    Frontend
        │
        ▼
    REST Controller
        │
        ▼
    Facade
        │
        ▼
    Service
        │
        ▼
    Repository
        │
        ▼
    Banco de Dados

Cada camada possui responsabilidades bem definidas e somente pode comunicar-se
com a camada imediatamente inferior.

Responsabilidades das Camadas
=============================

Controller
----------

A camada Controller representa a interface HTTP do sistema.

Suas responsabilidades incluem:

* receber requisições HTTP;
* validar parâmetros básicos;
* encaminhar a requisição para a Facade;
* retornar a resposta ao cliente.

Os Controllers não devem conter regras de negócio.

Facade
------

A Facade representa a camada de aplicação do sistema.

Cada operação disponibilizada pelo sistema deverá possuir uma Facade
responsável por coordenar sua execução.

Entre suas responsabilidades estão:

* orquestrar casos de uso;
* coordenar chamadas para múltiplos serviços;
* iniciar e finalizar transações quando necessário;
* converter objetos entre DTOs e modelos internos;
* preparar a resposta para a camada de apresentação.

A Facade não deve conter regras de persistência.

Service
-------

A camada Service implementa as regras de negócio pertencentes ao módulo.

Cada Service deve ser responsável apenas pelas regras relacionadas ao seu
próprio domínio.

Os Services não devem realizar chamadas diretas aos Controllers.

Repository
----------

Os Repositories são responsáveis exclusivamente pelo acesso aos dados.

Nenhuma regra de negócio deverá ser implementada nesta camada.

Fluxo de Requisição
===================

Uma requisição percorre obrigatoriamente o seguinte fluxo.

::

    HTTP Request
          │
          ▼
     Controller
          │
          ▼
       Facade
          │
          ▼
       Service
          │
          ▼
     Repository
          │
          ▼
      Banco de Dados

Após o processamento, o fluxo ocorre em sentido inverso até o cliente.

Comunicação entre Módulos
=========================

Cada módulo é responsável por encapsular suas regras de negócio e controlar o
acesso aos seus componentes internos.

Um módulo nunca deve acessar diretamente componentes internos de outro módulo.

Para isso, cada módulo disponibiliza um contrato público de integração.

Contratos de Integração
=======================

A comunicação entre módulos deve ocorrer exclusivamente por meio de contratos.

Um contrato define quais operações um módulo disponibiliza para os demais,
sem expor detalhes de implementação.

Exemplo:

::

    StudentGateway

        + findById()

        + exists()

        + isActive()

Os contratos representam a interface pública do módulo.

Implementações dos Contratos
============================

Cada contrato possui uma implementação responsável por acessar os serviços do
módulo correspondente.

Exemplo.

::

    StudentGateway
            │
            ▼
    StudentGatewayImpl
            │
            ▼
      StudentService

Os módulos consumidores dependem apenas do contrato, nunca da implementação.

Dependências Permitidas
=======================

São permitidas as seguintes dependências.

::

    Controller
        ▼
    Facade

    Facade
        ▼
    Service

    Service
        ▼
    Repository

    Service
        ▼
    Gateway de outro módulo

Dependências Proibidas
======================

Não são permitidas as seguintes dependências.

::

    Controller
        ▼
    Repository

::

    Controller
        ▼
    Controller

::

    Repository
        ▼
    Repository

::

    Service
        ▼
    Controller

::

    Módulo
        ▼
    Entidade de outro módulo

::

    Módulo
        ▼
    Repository de outro módulo

Essas restrições reduzem o acoplamento entre componentes e facilitam a evolução
da arquitetura.

Exemplo de Comunicação
======================

Durante a aprovação de um certificado, o fluxo ocorre da seguinte maneira.

::

    ValidationController
              │
              ▼
       ValidationFacade
              │
              ▼
      ValidationService
              │
              ├────────────► CertificateGateway
              │
              ├────────────► StudentGateway
              │
              └────────────► NotificationGateway

Cada Gateway comunica-se com seu respectivo módulo sem expor sua implementação.

Benefícios da Arquitetura
=========================

A arquitetura proposta oferece diversos benefícios.

* baixo acoplamento entre módulos;
* maior encapsulamento;
* separação clara de responsabilidades;
* facilidade para manutenção;
* facilidade para testes unitários;
* maior reutilização de componentes;
* menor impacto durante refatorações;
* possibilidade de evolução para uma arquitetura distribuída no futuro.

Boas Práticas
=============

Durante o desenvolvimento deverão ser observadas as seguintes recomendações.

* Controllers devem permanecer pequenos.
* Facades devem orquestrar casos de uso.
* Services devem conter apenas regras de negócio.
* Repositories devem realizar apenas acesso aos dados.
* Gateways representam a única forma de comunicação entre módulos.
* Nenhum módulo deve acessar componentes internos de outro módulo.
* DTOs devem ser utilizados para comunicação entre camadas e módulos quando
  necessário.
* Entidades não devem ser compartilhadas entre módulos.

Considerações Finais
====================

As regras descritas neste documento fazem parte da arquitetura oficial do
projeto e deverão ser seguidas por toda a equipe de desenvolvimento.

Qualquer alteração neste modelo deverá ser discutida previamente e registrada
na documentação arquitetural antes de sua adoção.
