=========================================
Configuração do Repositório
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento descreve a organização do repositório GitHub e os recursos
utilizados para apoiar o processo de desenvolvimento.

Seu objetivo é garantir que todos os desenvolvedores utilizem a mesma estrutura
de trabalho, reduzindo inconsistências e facilitando a colaboração.

Estrutura Geral
===============

A estrutura principal do repositório é composta pelos seguintes elementos.

.. code-block:: text

    .
    ├── .github/
    ├── documentos/
    ├── src/
    ├── .editorconfig
    ├── .gitattributes
    ├── .gitignore
    ├── .gitmessage
    ├── README.md
    └── LICENSE

Diretório .github
=================

Toda a configuração colaborativa do projeto encontra-se no diretório
``.github``.

Estrutura sugerida:

.. code-block:: text

    .github/

    ├── ISSUE_TEMPLATE/
    │   ├── bug.yml
    │   ├── feature.yml
    │   ├── documentacao.yml
    │   ├── refatoracao.yml
    │   ├── pesquisa.yml
    │   └── configuracao.yml
    │
    ├── PULL_REQUEST_TEMPLATE.md
    ├── CODEOWNERS
    ├── CONTRIBUTING.md
    ├── SECURITY.md
    ├── SUPPORT.md
    └── workflows/

O diretório ``workflows`` permanecerá vazio enquanto o projeto não utilizar
GitHub Actions.

Templates de Issues
===================

Cada tipo de trabalho possui um template específico.

Feature
--------

Utilizado para novas funcionalidades.

Bug
---

Utilizado para correções.

Documentação
------------

Utilizado para alterações na documentação.

Refatoração
-----------

Utilizado para melhorias estruturais.

Pesquisa
---------

Utilizado para estudos técnicos e experimentações.

Configuração
------------

Utilizado para alterações de infraestrutura do projeto.

Template de Pull Request
========================

Todo Pull Request deve utilizar um template padronizado contendo,
obrigatoriamente:

* objetivo;
* contexto;
* alterações realizadas;
* forma de validação;
* documentação impactada;
* Issues relacionadas.

CODEOWNERS
==========

O arquivo ``CODEOWNERS`` define os responsáveis pela revisão das principais
áreas do projeto.

Durante cada Sprint os responsáveis poderão ser atualizados para refletir os
papéis rotativos definidos pela equipe.

CONTRIBUTING.md
===============

Define as orientações para contribuição.

Entre elas:

* fluxo de desenvolvimento;
* convenções adotadas;
* padrão de commits;
* abertura de Pull Requests;
* organização das branches.

SECURITY.md
===========

Define como vulnerabilidades de segurança devem ser reportadas.

SUPPORT.md
==========

Apresenta os canais de suporte utilizados pelo projeto.

.editorconfig
=============

Padroniza aspectos básicos da edição dos arquivos.

Exemplos:

* codificação UTF-8;
* indentação;
* tamanho de tabulação;
* quebra de linha.

Seu objetivo é evitar diferenças entre editores.

.gitattributes
==============

Define atributos de versionamento.

Exemplos:

* normalização de finais de linha;
* tratamento de arquivos binários;
* estratégias de diff.

.gitignore
==========

Define arquivos que não devem ser versionados.

Exemplos:

* arquivos temporários;
* diretórios de compilação;
* dependências locais;
* arquivos de configuração específicos da máquina.

.gitmessage
===========

Define o modelo utilizado para criação de commits.

Seu objetivo é incentivar mensagens padronizadas utilizando Conventional
Commits.

Proteção da Branch Principal
============================

A branch ``main`` deve possuir as seguintes restrições:

* impedir commits diretos;
* impedir force push;
* impedir exclusão;
* exigir Pull Request;
* exigir resolução das conversas;
* exigir pelo menos uma aprovação;
* exigir branch atualizada antes do merge.

Essas configurações devem ser realizadas utilizando Rulesets do GitHub.

Projetos
=========

O acompanhamento das atividades será realizado através do GitHub Projects.

Fluxo sugerido:

.. code-block:: text

    Backlog

        |

        v

    Refinamento

        |

        v

    Pronto

        |

        v

    Em Desenvolvimento

        |

        v

    Em Revisão

        |

        v

    Ajustes

        |

        v

    Concluído

Releases
========

As versões do sistema devem ser publicadas utilizando o mecanismo de Releases
do GitHub.

Cada Release deverá conter:

* versão;
* data;
* funcionalidades entregues;
* correções realizadas;
* observações relevantes.

Resumo
======

A configuração do repositório implementa as regras de governança definidas para
o projeto, garantindo padronização, rastreabilidade e colaboração durante todo
o desenvolvimento.
