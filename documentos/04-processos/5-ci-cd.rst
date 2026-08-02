=========================================
Integração e Entrega Contínua (CI/CD)
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as práticas de Integração Contínua (Continuous
Integration - CI) e Entrega Contínua (Continuous Delivery - CD) utilizadas no
Sistema de Gestão de Atividades Complementares.

O objetivo é automatizar validações, reduzir erros de integração e garantir
maior confiabilidade durante o desenvolvimento.


====================
Objetivos
====================

A utilização de CI/CD busca:

* validar automaticamente alterações;
* detectar problemas rapidamente;
* garantir estabilidade da branch principal;
* padronizar o processo de entrega;
* reduzir falhas manuais.


====================
Conceitos
====================


====================
Integração Contínua (CI)
========================

CI representa a prática de integrar frequentemente alterações ao repositório,
executando validações automáticas.


Fluxo:


::

    Desenvolvedor


          |

          v


    Push para branch


          |

          v


    Pipeline executa


          |

          v


    Testes e análises


          |

          v


    Resultado



====================
Entrega Contínua (CD)
======================

CD representa a preparação automática de uma versão pronta para entrega.


Objetivo:

Garantir que o sistema esteja sempre em estado publicável.


====================
Pipeline
====================

O pipeline representa uma sequência automática de etapas executadas a cada
alteração relevante.


Fluxo:


::

    Código


      |

      v


    Build


      |

      v


    Testes


      |

      v


    Análise de qualidade


      |

      v


    Empacotamento


      |

      v


    Entrega



====================
Etapas do Pipeline
===================


====================
1. Checkout
================

Obtém o código do repositório.


Responsável:

Sistema de CI.


====================
2. Instalação de Dependências
==============================

Realiza preparação do ambiente.


Backend:


::

    Maven / Gradle



Frontend:


::

    npm



====================
3. Build
===========

Verifica se o projeto pode ser compilado.


Backend:


::

    mvn package



Frontend:


::

    npm build



====================
4. Testes Automatizados
=======================

Executa testes definidos pelo projeto.


Inclui:


* testes unitários;
* testes de integração;
* testes de API.


====================
5. Análise de Qualidade
=======================

Avalia aspectos do código.


Exemplos:


* cobertura;
* padrões;
* vulnerabilidades;
* complexidade.


Ferramentas possíveis:


* SonarQube;
* SonarCloud.


====================
6. Validação
=============

Caso alguma etapa falhe:

* Pull Request não poderá ser aprovado;
* problema deverá ser corrigido.


====================
Pipeline por Branch
===================

Cada Pull Request deverá executar validações antes do merge.


Fluxo:


::

    feature branch


          |

          v


    Pull Request


          |

          v


    CI executa


          |

          v


    Aprovação



====================
Pipeline da Main
=================

A branch principal deverá possuir validações mais rigorosas.


Objetivos:

* garantir estabilidade;
* preparar releases;
* evitar código quebrado.


====================
Ferramentas
============

A escolha das ferramentas poderá ser definida pela equipe.


Possíveis tecnologias:


Controle de repositório:

::

    GitHub



Backend:

::

    Java Spring Boot



Build:

::

    Maven



Frontend:

::

    Angular CLI



CI:

::

    GitHub Actions



Qualidade:

::

    SonarCloud



====================
Integração com Pull Requests
============================

O pipeline deverá estar conectado aos Pull Requests.


Um PR somente poderá ser integrado quando:


[ ] Build executado com sucesso.

[ ] Testes aprovados.

[ ] Análise de qualidade concluída.

[ ] Revisão realizada.



====================
Falhas no Pipeline
=================

Quando ocorrer uma falha:


Responsável pela alteração deverá:


* analisar logs;
* corrigir código;
* atualizar branch;
* solicitar nova validação.


====================
Responsabilidade dos Desenvolvedores
=====================================

Todos os desenvolvedores devem:


* executar testes localmente;
* verificar impacto das alterações;
* não ignorar falhas do pipeline;
* manter código compatível com a integração.


====================
Papel Rotativo de Qualidade
============================

Durante uma sprint, um desenvolvedor poderá acompanhar:


* funcionamento do pipeline;
* qualidade das entregas;
* cobertura de testes;
* problemas recorrentes.


Esse papel auxilia a equipe, mas não substitui a responsabilidade individual.


====================
Ambientes
==========

O projeto poderá possuir diferentes ambientes.


Desenvolvimento
---------------

Utilizado pelos desenvolvedores localmente.


Teste
-----

Ambiente para validações integradas.


Produção
--------

Ambiente utilizado pelos usuários finais.


====================
Versionamento de Releases
==========================

Versões estáveis deverão ser identificadas.


Exemplo:


::

    v1.0.0



Cada release deve possuir:

* código correspondente;
* documentação;
* histórico de alterações.


====================
Boas Práticas
=============


A equipe deverá:


* integrar frequentemente;
* evitar branches muito longas;
* corrigir falhas rapidamente;
* manter pipeline saudável.


====================
Checklist de CI/CD
===================


[ ] Pipeline configurado.

[ ] Build automatizado.

[ ] Testes executados automaticamente.

[ ] Pull Requests possuem validação.

[ ] Main permanece estável.

[ ] Releases possuem versão.



====================
Resumo
====================

A utilização de CI/CD automatiza validações e aumenta a confiabilidade do
processo de desenvolvimento.

Com pipelines integrados ao Git e Pull Requests, os cinco desenvolvedores
conseguem trabalhar paralelamente mantendo qualidade, rastreabilidade e
segurança nas entregas.
