=========================================
Controle de Versão com Git
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as práticas de utilização do Git no desenvolvimento do
Sistema de Gestão de Atividades Complementares.

O objetivo é estabelecer um fluxo colaborativo que permita que os cinco
desenvolvedores trabalhem simultaneamente mantendo organização, rastreabilidade
e qualidade do código.


====================
Objetivos
====================

O fluxo Git busca garantir:

* histórico organizado;
* rastreamento das alterações;
* isolamento das funcionalidades;
* revisão antes da integração;
* redução de conflitos.


====================
Estratégia de Branches
====================

O projeto utilizará uma estratégia baseada em branches por funcionalidade.


Estrutura:


::

    main

      |

      +-- feature/autenticacao

      |

      +-- feature/certificados

      |

      +-- feature/relatorios



====================
Branch Principal
====================

A branch:

::

    main



representa a versão estável do sistema.


Regras:

* código deve estar funcionando;
* testes devem passar;
* alterações entram através de Pull Request.


Desenvolvimento direto na main não é permitido.


====================
Branches de Funcionalidade
============================

Cada nova funcionalidade deverá possuir sua própria branch.


Padrão:


::

    feature/nome-da-funcionalidade



Exemplos:


::

    feature/login-jwt


    feature/upload-certificado


    feature/geracao-relatorio



====================
Branches de Correção
======================

Correções deverão utilizar o prefixo:


::

    fix/



Exemplos:


::

    fix/erro-validacao-certificado


    fix/problema-login



====================
Branches de Documentação
===========================

Alterações exclusivamente documentais:


::

    docs/nome-documentacao



Exemplo:


::

    docs/arquitetura-backend



====================
Branches de Testes
====================

Alterações focadas em testes:


::

    test/nome-teste



Exemplo:


::

    test/certificado-service



====================
Commits
====================

Commits deverão representar alterações pequenas e significativas.


Evitar:


::

    ajustes


    mudanças


    correções



Preferir:


::

    adicionar validação de certificado


    implementar autenticação JWT


    corrigir cálculo de carga horária



====================
Padrão de Commit
=================

O projeto utilizará Conventional Commits.


Formato:


::

    tipo: descrição



Tipos:


feat
----

Nova funcionalidade.


Exemplo:


::

    feat: adicionar envio de certificado



fix
---

Correção de problema.


Exemplo:


::

    fix: corrigir validação de carga horária



docs
----

Alteração documental.


Exemplo:


::

    docs: atualizar modelo de domínio



test
----

Criação ou alteração de testes.


Exemplo:


::

    test: adicionar testes de atividade



refactor
--------

Refatoração sem alteração de comportamento.


Exemplo:


::

    refactor: reorganizar service de usuário



====================
Tamanho dos Commits
===================

Commits deverão ser pequenos e focados.


Evitar:


::

    50 arquivos modificados em um commit



Preferir:


::

    adicionar entidade atividade


    criar repository


    implementar testes



====================
Pull Request
================

Toda alteração relevante deverá passar por Pull Request.


Fluxo:


::

    Branch de feature


          |

          v


    Commit


          |

          v


    Push


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
Revisão de Código
================

Pull Requests deverão ser revisados por outro desenvolvedor.


A revisão deverá verificar:


Código:

* organização;
* padrões;
* legibilidade.


Arquitetura:

* responsabilidade correta;
* ausência de acoplamento.


Qualidade:

* testes;
* documentação.


====================
Proteção da Main
=================

A branch main deverá possuir proteção.


Regras recomendadas:


* impedir push direto;
* exigir Pull Request;
* exigir aprovação;
* exigir pipeline passando.


====================
Sincronização das Branches
============================

Antes de abrir Pull Requests, desenvolvedores deverão manter suas branches
atualizadas.


Fluxo:


::

    main atualizada


          |

          v


    atualizar feature


          |

          v


    resolver conflitos


          |

          v


    abrir Pull Request



====================
Merge
====================

Após aprovação, a alteração poderá ser integrada.


O histórico deverá permanecer compreensível.


Preferência:


::

    Squash Merge



quando a equipe desejar manter histórico mais limpo.


====================
Conflitos
=================

Conflitos deverão ser resolvidos pelo responsável pela alteração.


Nunca deverá ocorrer:


* apagar código sem entender;
* aceitar automaticamente alterações conflitantes.


====================
Tags e Releases
=================

Versões importantes deverão possuir tags.


Exemplo:


::

    v1.0.0


    v1.1.0



As versões devem representar entregas significativas.


====================
Responsabilidade dos Desenvolvedores
=====================================

Cada desenvolvedor deverá:


* criar sua branch;
* manter commits organizados;
* revisar código de colegas;
* resolver conflitos;
* manter testes funcionando.


====================
Checklist Antes do Merge
=========================


[ ] Branch criada corretamente.

[ ] Commits possuem padrão.

[ ] Código revisado.

[ ] Testes passando.

[ ] Documentação atualizada.

[ ] Pipeline aprovado.



====================
Resumo
====================

O fluxo Git definido permite desenvolvimento paralelo entre os cinco
integrantes, mantendo organização e qualidade.

A utilização de branches, commits padronizados e Pull Requests reduz conflitos,
melhora rastreabilidade e aproxima o projeto de práticas utilizadas em equipes
profissionais de desenvolvimento.
