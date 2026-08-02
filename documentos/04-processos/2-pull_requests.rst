=========================================
Pull Requests
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define o processo de criação, revisão e aprovação de Pull
Requests no desenvolvimento do Sistema de Gestão de Atividades Complementares.

O objetivo é garantir que todas as alterações integradas ao projeto possuam
qualidade técnica, estejam alinhadas à arquitetura definida e não comprometam o
funcionamento do sistema.


====================
Objetivos
====================

O processo de Pull Request busca:

* melhorar qualidade do código;
* compartilhar conhecimento entre desenvolvedores;
* detectar problemas antes da integração;
* garantir padronização;
* manter histórico das decisões técnicas.


====================
Quando Criar um Pull Request
================================

Um Pull Request deverá ser criado quando:


* uma funcionalidade estiver implementada;
* uma correção estiver pronta;
* uma documentação relevante for finalizada;
* uma alteração arquitetural precisar ser integrada.


Exemplos:


::

    feature/login-jwt


    feature/upload-certificado


    docs/modelo-dominio



====================
Responsabilidade do Autor
=========================

O desenvolvedor responsável pela branch deverá garantir:


* código funcionando;
* testes criados;
* commits organizados;
* descrição adequada;
* documentação atualizada quando necessário.


Antes de abrir o Pull Request, o autor deverá realizar uma revisão própria.


====================
Título do Pull Request
======================

O título deverá ser objetivo e seguir o padrão dos commits.


Exemplos:


::

    feat: implementar autenticação JWT


    fix: corrigir validação de certificado



====================
Descrição do Pull Request
============================

Todo Pull Request deverá explicar:


Contexto
--------

Qual problema está sendo resolvido.


Exemplo:


::

    Implementa autenticação dos usuários do sistema.



Alterações realizadas
---------------------

Lista das principais mudanças.


Exemplo:


::

    - Criado AuthService

    - Implementado JWT

    - Adicionados testes



Testes realizados
-----------------

Informar como a alteração foi validada.


Exemplo:


::

    Testes unitários executados com sucesso.



Impacto
-------

Informar possíveis impactos.


Exemplo:


::

    Alteração afeta fluxo de login.



====================
Processo de Revisão
====================

O fluxo será:


::

    Desenvolvedor


        |

        v


    Cria Pull Request


        |

        v


    Pipeline executa


        |

        v


    Revisor analisa


        |

        v


    Ajustes necessários


        |

        v


    Aprovação


        |

        v


    Merge



====================
Quantidade de Revisores
======================

Recomenda-se:


Mínimo:

::

    1 desenvolvedor revisor



Para alterações críticas:


::

    2 revisores



Exemplos:

* segurança;
* arquitetura;
* alterações estruturais.


====================
Critérios de Revisão
====================


Arquitetura
-----------

Verificar:


* responsabilidade das camadas;
* organização dos módulos;
* ausência de acoplamento inadequado.


Exemplo:


Incorreto:


::

    Controller contendo regra de negócio.



Correto:


::

    Controller chama Service.



====================
Código
======

Verificar:


* legibilidade;
* nomenclatura;
* duplicação;
* complexidade desnecessária.


====================
Testes
======

Verificar:


* existência de testes;
* qualidade dos cenários;
* cobertura das regras importantes.


====================
Segurança
==========

Verificar:


* exposição de dados;
* validações;
* permissões;
* tratamento de informações sensíveis.



====================
Documentação
============

Alterações que impactam arquitetura ou comportamento deverão atualizar
documentação.


Exemplos:


* novo módulo;
* novo fluxo;
* mudança de regra.


====================
Comentários de Revisão
======================

Comentários deverão ser:


* claros;
* técnicos;
* construtivos.


Evitar comentários:


::

    "Está errado"



Preferir:


::

    "Essa regra deveria permanecer no Service porque representa uma regra de
    negócio."



====================
Aprovação
==========

Um Pull Request poderá ser aprovado quando:


[ ] Implementação atende a issue.

[ ] Código segue arquitetura.

[ ] Testes passam.

[ ] Não existem problemas críticos.

[ ] Revisões foram resolvidas.



====================
Solicitação de Alterações
=========================

Caso existam problemas, o revisor deverá solicitar mudanças.


Situações comuns:


* regra implementada na camada errada;
* ausência de testes;
* código duplicado;
* quebra de padrão.


====================
Merge
======

Após aprovação, o Pull Request poderá ser integrado.


O merge deverá preservar:

* histórico;
* rastreabilidade;
* associação com a issue.


====================
Relacionamento com Issues
========================

Todo Pull Request deverá estar relacionado a uma issue.


Exemplo:


::

    Closes #25



Benefícios:

* rastrear implementação;
* identificar responsável;
* manter histórico do projeto.


====================
Papel Rotativo de Revisão
==========================

Durante cada sprint, um desenvolvedor poderá assumir o papel adicional de:


* acompanhar qualidade dos PRs;
* verificar padrões;
* auxiliar decisões arquiteturais.


Esse papel não substitui a revisão dos demais integrantes.


====================
Checklist de Pull Request
=========================


Autor:


[ ] Branch correta.

[ ] Commits organizados.

[ ] Testes adicionados.

[ ] Descrição preenchida.

[ ] Issue vinculada.



Revisor:


[ ] Arquitetura validada.

[ ] Código revisado.

[ ] Testes avaliados.

[ ] Segurança considerada.

[ ] Documentação atualizada.



====================
Resumo
====================

O processo de Pull Request garante que todas as alterações passem por uma etapa
de validação antes de chegar ao código principal.

Essa prática permite que os cinco desenvolvedores trabalhem em paralelo sem
perder qualidade, mantendo conhecimento compartilhado e evolução controlada do
sistema.
