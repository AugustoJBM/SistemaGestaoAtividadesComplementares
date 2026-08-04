=========================================
Fluxo de Desenvolvimento
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define o fluxo utilizado para transformar requisitos do sistema
em funcionalidades implementadas no Sistema de Gestão de Atividades
Complementares.

O objetivo é estabelecer um processo previsível para organização, execução e
entrega das tarefas realizadas pelos cinco desenvolvedores.


====================
Visão Geral do Fluxo
====================

O desenvolvimento seguirá o seguinte ciclo:


::

    Requisito


       |

       v


    Epic


       |

       v


    Feature


       |

       v


    Issue


       |

       v


    Branch


       |

       v


    Desenvolvimento


       |

       v


    Pull Request


       |

       v


    Revisão


       |

       v


    Merge



====================
Requisitos
====================

Requisitos representam necessidades do sistema identificadas pelo Product
Owner.


Exemplo:


::

    Permitir que estudantes acompanhem suas atividades complementares.



O requisito ainda representa uma visão ampla e precisa ser detalhado.


====================
Epic
====================

Uma Epic representa um grande conjunto de funcionalidades relacionadas a um
objetivo do sistema.


Exemplo:


Epic:


::

    Gerenciamento de Certificados



Pode envolver:


* envio de certificados;
* validação;
* armazenamento;
* consulta;
* notificações.



Uma Epic normalmente não deve ser implementada diretamente.


====================
Feature
====================

Uma Feature representa uma funcionalidade entregável do sistema.


Ela é uma divisão menor de uma Epic.


Exemplo:


Epic:


::

    Gerenciamento de Certificados



Features:


::

    Upload de certificado


    Validação de certificado


    Consulta de certificados



====================
Issue
====================

A Issue representa uma tarefa específica de desenvolvimento.


Ela deve possuir escopo claro e ser executável por um desenvolvedor.


Exemplo:


Feature:


::

    Upload de certificado



Issues:


::

    Criar entidade Certificado


    Criar endpoint de upload


    Implementar validação de arquivo


    Criar testes



====================
Subtarefas
====================

Quando uma Issue possuir grande complexidade, ela poderá ser dividida em
subtarefas.


Exemplo:


Issue:


::

    Implementar autenticação



Subtarefas:


::

    Criar entidade usuário


    Implementar login


    Criar JWT


    Configurar Spring Security


    Criar testes



====================
Planejamento da Sprint
======================

Antes do início de uma sprint, o time deverá selecionar as Issues que serão
desenvolvidas.


Critérios:


* prioridade definida pelo Product Owner;
* capacidade da equipe;
* dependências existentes;
* complexidade.


====================
Distribuição Entre Desenvolvedores
==================================

Os cinco desenvolvedores não serão divididos permanentemente por camada.


O modelo adotado será:


Cada desenvolvedor:

* possui uma Feature ou conjunto de Issues;
* implementa frontend/backend conforme necessidade;
* cria testes;
* documenta alterações.


====================
Motivo da Divisão por Feature
==============================

Separar permanentemente por:

::

    Dev Backend


    Dev Frontend


    Dev Testes



poderia gerar dependências excessivas.


O modelo por Feature permite:

* maior conhecimento do sistema;
* menor gargalo;
* responsabilidade clara;
* desenvolvimento paralelo.


====================
Exemplo de Distribuição
========================


Sprint:


Feature:

::

    Autenticação



Dev 1:

::

    Login e JWT Backend



Dev 2:

::

    Tela de login Angular



Dev 3:

::

    Testes de autenticação



Dev 4:

::

    Integração frontend/backend



Dev 5:

::

    Revisão arquitetural e documentação



Na próxima sprint essas responsabilidades podem mudar.


====================
Papéis Rotativos
=================

Além da implementação das Features, cada sprint possuirá papéis auxiliares.


====================
Responsável por Arquitetura
==============================

Responsabilidades:


* revisar decisões técnicas;
* avaliar padrões;
* auxiliar integração entre módulos.


====================
Responsável por Qualidade
============================

Responsabilidades:


* acompanhar testes;
* verificar cobertura;
* acompanhar pipeline CI.


====================
Responsável por Documentação
================================

Responsabilidades:


* atualizar documentação;
* registrar decisões;
* revisar diagramas.


====================
Importante
===========

Esses papéis não substituem a responsabilidade dos demais desenvolvedores.


Todos devem:

* escrever testes;
* revisar código;
* conhecer o sistema.


====================
Fluxo de Implementação
======================


1. Criar Branch
---------------

A partir da main:


::

    feature/nome-da-feature



====================

2. Desenvolvimento
------------------

Implementar:


* código;
* testes;
* documentação necessária.


====================

3. Commit
----------

Utilizar padrão definido:


::

    feat: adicionar upload de certificado



====================

4. Pull Request
---------------

Criar PR relacionando a Issue.


====================

5. Revisão
-----------

Outro desenvolvedor analisa:


* código;
* arquitetura;
* testes.


====================

6. Merge
---------

Após aprovação:


::

    feature


        |

        v


    main



====================
Definition of Done
==================

Uma tarefa somente será considerada concluída quando:


[ ] Código implementado.

[ ] Testes criados.

[ ] Revisão realizada.

[ ] Documentação atualizada quando necessário.

[ ] Pipeline aprovado.

[ ] Issue encerrada.



====================
Tratamento de Dependências
==========================

Quando uma Feature depender de outra, deverá ser identificado antes da sprint.


Exemplo:


::

    Upload certificado


        depende de


    Autenticação



Soluções possíveis:


* priorizar dependência;
* criar mock temporário;
* dividir entrega.


====================
Comunicação da Equipe
======================

Decisões importantes deverão ser registradas.


Exemplos:

* mudanças arquiteturais;
* novos padrões;
* decisões técnicas.


Evitar decisões importantes apenas em mensagens temporárias.


====================
Resumo
====================

O fluxo de desenvolvimento organiza a transformação de requisitos em entregas
concretas.

A utilização de Epics, Features e Issues permite que os cinco desenvolvedores
trabalhem simultaneamente, mantendo organização, rastreabilidade e qualidade
durante todo o ciclo do projeto.
