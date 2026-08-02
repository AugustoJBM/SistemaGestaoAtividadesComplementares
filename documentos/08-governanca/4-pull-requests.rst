=========================================
Pull Requests
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento define o processo oficial de criação, revisão, aprovação e
integração de Pull Requests do projeto.

Todo código incorporado à branch principal deve obrigatoriamente passar por um
Pull Request.

O Pull Request representa a unidade oficial de entrega de uma Sub-Issue,
servindo como registro técnico da implementação realizada.


Objetivos
=========

O processo de Pull Requests possui os seguintes objetivos:

* garantir qualidade do código;
* assegurar rastreabilidade das alterações;
* permitir revisão técnica colaborativa;
* preservar a estabilidade da branch principal;
* registrar as decisões tomadas durante o desenvolvimento.


Fluxo Geral
===========

Todo Pull Request segue obrigatoriamente o fluxo abaixo.

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

    Desenvolvimento

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

    Correções

        |

        v

    Aprovação

        |

        v

    Merge

Cada Pull Request está vinculado a apenas uma Sub-Issue.


Criação do Pull Request
=======================

O desenvolvedor responsável pela Sub-Issue deve criar o Pull Request quando:

* toda a implementação estiver concluída;
* os testes locais forem executados;
* não existirem conflitos conhecidos;
* a documentação necessária tiver sido atualizada.

Não é permitido abrir Pull Requests contendo funcionalidades incompletas, salvo
quando utilizados Draft Pull Requests.


Draft Pull Requests
===================

Draft Pull Requests podem ser utilizados quando:

* houver necessidade de discussão técnica;
* o desenvolvedor desejar revisão antecipada;
* a implementação ainda não estiver finalizada.

Draft Pull Requests não podem ser integrados à branch principal.

Antes da aprovação, o Draft deve ser convertido para Pull Request convencional.


Responsabilidades
=================

Autor
-----

O autor do Pull Request é responsável por:

* implementar a Sub-Issue;
* manter a branch atualizada;
* responder comentários da revisão;
* realizar as correções solicitadas;
* atualizar a documentação quando necessário.

O autor não pode aprovar o próprio Pull Request.


Revisor Principal
-----------------

O Revisor Principal é definido durante a Sprint.

Suas responsabilidades são:

* revisar qualidade do código;
* verificar aderência aos padrões do projeto;
* identificar defeitos;
* solicitar alterações quando necessário;
* aprovar tecnicamente a implementação.


Arquiteto da Sprint
-------------------

O Arquiteto da Sprint participa apenas quando o Pull Request modifica:

* arquitetura;
* módulos;
* contratos da API;
* decisões estruturais.

Compete ao Arquiteto:

* validar impactos arquiteturais;
* preservar a consistência da arquitetura;
* rejeitar alterações incompatíveis.


Responsável por Testes
----------------------

Participa quando houver alteração de comportamento do sistema.

Compete a este papel:

* validar os testes executados;
* verificar necessidade de novos testes;
* confirmar cobertura adequada.


Responsável pela Documentação
-----------------------------

Participa quando houver impacto na documentação.

Compete a este papel:

* verificar atualização dos documentos;
* garantir consistência entre implementação e documentação.


Product Owner
-------------

O Product Owner não realiza revisão técnica de código.

Sua participação ocorre apenas para:

* esclarecer requisitos;
* validar regras de negócio;
* responder dúvidas funcionais.


Critérios para Aprovação
========================

Um Pull Request somente poderá ser aprovado quando:

* a implementação estiver concluída;
* todos os comentários obrigatórios estiverem resolvidos;
* não existirem conflitos;
* a documentação estiver consistente;
* os revisores obrigatórios tiverem aprovado.


Situações que Exigem Solicitação de Alterações
==============================================

O revisor deve solicitar alterações quando identificar:

* defeitos;
* violação dos padrões definidos;
* ausência de testes necessários;
* documentação inconsistente;
* implementação incompleta;
* código duplicado;
* impacto arquitetural não tratado.


Merge
=====

Após todas as aprovações obrigatórias, o Pull Request poderá ser integrado à
branch principal.

O projeto adota como estratégia padrão:

* Squash and Merge.

Essa estratégia mantém o histórico da branch principal organizado e facilita a
rastreabilidade das entregas.


Fechamento Automático de Issues
===============================

Sempre que possível, o Pull Request deve utilizar palavras-chave do GitHub.

Exemplo:

.. code-block:: text

    Closes #52

    Fixes #81

    Resolves #104

Após o merge, a Issue correspondente será encerrada automaticamente.


Rastreabilidade
===============

Cada Pull Request deve possuir relacionamento com:

* uma Epic;
* uma Issue;
* uma Sub-Issue;
* uma Branch;
* uma Milestone;
* um Sprint;
* um responsável.

Essa estrutura garante rastreabilidade completa durante todo o ciclo de
desenvolvimento.


Fluxo de Aprovação
==================

O processo de aprovação segue o fluxo abaixo.

.. code-block:: text

    Desenvolvedor

        |

        v

    Pull Request

        |

        v

    Revisão Técnica

        |

        v

    Ajustes

        |

        v

    Nova Revisão

        |

        v

    Aprovação

        |

        v

    Squash and Merge


Resumo
======

O Pull Request representa a entrega oficial de uma Sub-Issue.

Além de integrar código ao projeto, ele registra decisões técnicas,
possibilita revisão colaborativa, garante qualidade da implementação e mantém
a rastreabilidade entre requisitos, desenvolvimento e entrega.
