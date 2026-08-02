=========================================
Fluxo de Branches
=========================================

.. contents::
   :local:
   :depth: 2


Introdução
==========

Este documento define a estratégia de utilização de branches adotada pelo
projeto.

O objetivo é manter um fluxo simples, previsível e organizado, permitindo que
vários desenvolvedores trabalhem simultaneamente sem comprometer a estabilidade
da branch principal.


Objetivos
=========

A estratégia de branches busca:

* reduzir conflitos;
* facilitar integração;
* manter rastreabilidade;
* organizar o desenvolvimento;
* permitir revisões independentes.


Princípios
==========

Toda alteração realizada no projeto deve estar associada a uma Sub-Issue.

Nenhum desenvolvimento deve ocorrer diretamente na branch principal.

Cada branch representa uma única unidade de trabalho.


Estrutura Geral
===============

O fluxo utilizado pelo projeto é representado pela sequência abaixo.

.. code-block:: text

    Epic

        |

        v

    Issue

        |

        v

    Sub-Issue

        |

        v

    Branch

        |

        v

    Commits

        |

        v

    Pull Request

        |

        v

    Code Review

        |

        v

    Merge


Branch Principal
================

main
----

A branch ``main`` representa a versão estável do projeto.

Características:

* nunca recebe commits diretos;
* somente Pull Requests podem alterar seu conteúdo;
* permanece sempre estável;
* representa a versão oficial do sistema.


Criação de Branches
===================

Uma branch somente pode ser criada quando existir uma Sub-Issue aprovada para
desenvolvimento.

Cada branch deve implementar apenas uma responsabilidade.

Exemplo:

.. code-block:: text

    Sub-Issue

        Criar endpoint de login

            |

            v

        feature/login-endpoint


Fluxo de Trabalho
=================

O ciclo de desenvolvimento segue as etapas abaixo.

1. Product Owner cria uma Issue.

2. Desenvolvedores refinam a Issue.

3. São criadas Sub-Issues.

4. Cada Sub-Issue recebe um responsável.

5. O responsável cria sua branch.

6. O desenvolvimento é realizado.

7. Commits são enviados para o GitHub.

8. Um Pull Request é aberto.

9. O código é revisado.

10. Após aprovação ocorre o merge.


Tipos de Branches
=================

Feature
-------

Utilizada para implementação de novas funcionalidades.

Formato:

.. code-block:: text

    feature/nome-da-funcionalidade


Exemplos:

.. code-block:: text

    feature/autenticacao

    feature/upload-certificado

    feature/dashboard


Fix
---

Utilizada para correção de defeitos.

Formato:

.. code-block:: text

    fix/nome-do-problema


Exemplos:

.. code-block:: text

    fix/login

    fix/upload


Refactor
--------

Utilizada para melhorias internas sem alterar comportamento.

Formato:

.. code-block:: text

    refactor/nome


Docs
----

Utilizada para documentação.

Formato:

.. code-block:: text

    docs/modelo-dominio


Test
----

Utilizada para criação ou melhoria de testes.

Formato:

.. code-block:: text

    test/certificado-service


Chore
-----

Utilizada para manutenção.

Exemplos:

* atualização de dependências;
* ajustes de configuração;
* organização do projeto.


Formato:

.. code-block:: text

    chore/update-gradle


Regras
=======

Cada branch:

* possui um único responsável;
* implementa apenas uma Sub-Issue;
* deve permanecer pequena;
* deve ser integrada rapidamente;
* deve ser removida após o merge.


Branches Proibidas
==================

Não são permitidas branches genéricas.

Exemplos incorretos:

.. code-block:: text

    frontend

    backend

    projeto

    sistema

    nova-feature

    teste123


Esses nomes dificultam a rastreabilidade.


Relacionamento com GitHub
=========================

Toda branch deve estar vinculada a:

* uma Issue;
* uma Sub-Issue;
* uma Milestone;
* um Pull Request.


Exemplo Completo
================

.. code-block:: text

    Epic

        Sistema de autenticação

            |

            v

    Issue

        Implementar autenticação

            |

            v

    Sub-Issue

        Criar endpoint de login

            |

            v

    Branch

        feature/login-endpoint

            |

            v

    Pull Request

            |

            v

    Merge


Resumo
======

A estratégia adotada é orientada por Issues e Sub-Issues.

Cada branch representa uma única unidade de trabalho, garantindo organização,
rastreabilidade e integração simplificada.
