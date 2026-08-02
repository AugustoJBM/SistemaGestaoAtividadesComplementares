=========================================
Pull Requests
=========================================

.. contents::
   :local:
   :depth: 2


Introdução
==========

Este documento define o processo de criação, revisão e integração de Pull
Requests utilizados durante o desenvolvimento do projeto.

Todo código integrado à branch principal deve obrigatoriamente passar por um
Pull Request.


Objetivos
=========

O processo de Pull Requests busca:

* manter a estabilidade da branch principal;
* permitir revisão técnica das alterações;
* registrar decisões de desenvolvimento;
* facilitar discussões técnicas;
* garantir rastreabilidade das entregas.


Princípios
==========

Todo Pull Request deve:

* estar relacionado a uma Sub-Issue;
* possuir uma única responsabilidade;
* conter alterações pequenas;
* ser revisado antes do merge;
* permanecer aberto apenas pelo tempo necessário.


Fluxo Geral
===========

O fluxo de integração segue a sequência abaixo.

.. code-block:: text

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

    Revisão

        |

        v

    Aprovação

        |

        v

    Merge



Quando Criar um Pull Request
============================

O Pull Request deve ser criado quando:

* a implementação da Sub-Issue estiver concluída;
* os testes locais tiverem sido executados;
* não existirem conflitos conhecidos;
* a documentação necessária estiver atualizada.


Informações Obrigatórias
========================

Todo Pull Request deve responder às seguintes perguntas.

Objetivo
---------

O que foi implementado?

Motivação
---------

Por que essa alteração foi realizada?

Implementação
-------------

Como a solução foi construída?

Validação
---------

Como testar a alteração?

Relacionamento
--------------

Qual Issue ou Sub-Issue está sendo atendida?


Escopo
======

Cada Pull Request deve resolver apenas uma Sub-Issue.

Não é permitido misturar funcionalidades independentes em um mesmo Pull
Request.


Exemplo correto:

.. code-block:: text

    Sub-Issue

        Upload de certificado

            |

            v

    Pull Request


Exemplo incorreto:

.. code-block:: text

    Upload

    Login

    Dashboard

    Documentação

Tudo no mesmo PR.


Critérios para Aprovação
========================

Antes do merge, o Pull Request deve atender aos seguintes critérios:

* implementação concluída;
* código revisado;
* documentação atualizada quando necessário;
* ausência de conflitos;
* comentários resolvidos;
* aprovação do revisor.


Merge
=====

O merge somente pode ocorrer após:

* aprovação obrigatória;
* resolução das conversas;
* atualização da branch em relação à main, quando necessário.


Estratégia de Merge
===================

O projeto adota **Squash and Merge** como estratégia padrão.

Justificativa:

* mantém o histórico limpo;
* reduz commits intermediários;
* facilita a navegação no histórico;
* preserva o vínculo entre Pull Request e Issue.

O histórico da branch de desenvolvimento permanece disponível no Pull Request,
mas a branch principal recebe apenas um commit consolidado.


Relacionamento com GitHub
=========================

Todo Pull Request deve:

* referenciar a Issue correspondente;
* possuir pelo menos um revisor;
* estar associado à Milestone da Sprint;
* utilizar labels adequadas;
* estar vinculado ao GitHub Project.


Boas Práticas
=============

Preferir Pull Requests pequenos.

Evitar alterações muito extensas.

Responder comentários de revisão.

Atualizar a descrição caso o escopo seja alterado.

Não utilizar Pull Requests para discutir requisitos; essas discussões devem
ocorrer na Issue correspondente.


Resumo
======

O Pull Request representa a unidade oficial de entrega do projeto. Ele consolida
o trabalho realizado em uma Sub-Issue, registra a justificativa técnica da
implementação e garante que toda alteração seja revisada antes de integrar a
branch principal.
