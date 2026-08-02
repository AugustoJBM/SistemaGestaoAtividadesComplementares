=========================================
Política de Revisão de Código
=========================================

.. contents::
   :local:
   :depth: 2


Introdução
==========

Este documento estabelece a política oficial de revisão de código adotada pelo
projeto.

Toda alteração integrada à branch principal deve ser revisada por outro membro
da equipe antes do merge.

O processo de revisão tem como objetivo garantir qualidade, padronização,
compartilhamento de conhecimento e redução de defeitos.


Objetivos
=========

A revisão de código busca:

* identificar defeitos antes da integração;
* verificar conformidade com os padrões do projeto;
* preservar a arquitetura definida;
* compartilhar conhecimento entre os desenvolvedores;
* reduzir riscos de manutenção futura.


Princípios
==========

A revisão deve ser:

* técnica;
* objetiva;
* colaborativa;
* respeitosa;
* fundamentada.

Comentários devem sempre justificar o motivo da alteração sugerida.


Papéis
=======

Autor
------

O autor é responsável por:

* implementar a Sub-Issue;
* explicar a solução proposta;
* responder comentários;
* realizar as alterações solicitadas;
* solicitar nova revisão quando necessário.


Revisor Principal
-----------------

Cada Pull Request possui um Revisor Principal.

Compete ao Revisor Principal:

* revisar toda a implementação;
* verificar aderência aos padrões definidos;
* aprovar ou solicitar alterações.


Arquiteto da Sprint
-------------------

Participa apenas quando houver impacto arquitetural.

Compete ao Arquiteto:

* validar decisões estruturais;
* preservar a consistência arquitetural;
* avaliar dependências entre módulos.


Responsável por Testes
----------------------

Participa quando alterações modificarem comportamento do sistema.

Compete a este papel:

* verificar testes existentes;
* solicitar novos testes;
* validar cobertura adequada.


Responsável pela Documentação
-----------------------------

Participa quando houver impacto documental.

Compete a este papel:

* verificar documentação atualizada;
* garantir coerência entre documentação e implementação.


O que Deve ser Revisado
=======================

Toda revisão deve considerar os seguintes aspectos.

Arquitetura
-----------

Verificar se a implementação respeita a arquitetura do projeto.

Código
-------

Verificar:

* legibilidade;
* simplicidade;
* duplicação;
* organização;
* modularidade.

Regras de Negócio
-----------------

Confirmar aderência aos requisitos.

Segurança
---------

Verificar:

* autenticação;
* autorização;
* validação;
* tratamento de entradas.

Persistência
------------

Verificar:

* consultas;
* transações;
* integridade dos dados.

API
---

Verificar:

* contratos;
* códigos HTTP;
* padronização das respostas.

Frontend
---------

Verificar:

* componentes;
* reutilização;
* acessibilidade;
* consistência visual.

Documentação
------------

Verificar atualização dos documentos afetados.


Critérios para Aprovação
========================

Um Pull Request somente poderá ser aprovado quando:

* todas as alterações solicitadas forem resolvidas;
* não existirem comentários pendentes;
* os padrões arquiteturais forem respeitados;
* os testes necessários estiverem presentes;
* a documentação estiver consistente.


Solicitação de Alterações
=========================

O revisor deve solicitar alterações quando identificar:

* defeitos;
* inconsistências arquiteturais;
* ausência de validações;
* código duplicado;
* baixa legibilidade;
* ausência de testes necessários;
* documentação desatualizada.


Aprovação
=========

Uma aprovação representa a confirmação de que o Pull Request atende aos
critérios técnicos definidos pelo projeto.

A aprovação não transfere a responsabilidade pelo código.

O autor continua sendo responsável pela implementação realizada.


Boas Práticas
=============

Durante a revisão, recomenda-se:

* revisar pequenas alterações com frequência;
* utilizar comentários objetivos;
* justificar sugestões;
* evitar alterações sem explicação;
* priorizar problemas relevantes.


Condutas Não Permitidas
=======================

Não são permitidas:

* aprovações sem revisão;
* revisões superficiais;
* comentários ofensivos;
* solicitações sem justificativa técnica;
* aprovação do próprio Pull Request.


Fluxo de Revisão
================

O processo de revisão segue a sequência abaixo.

.. code-block:: text

    Pull Request

        |

        v

    Revisor Principal

        |

        v

    Comentários

        |

        v

    Correções

        |

        v

    Nova Revisão

        |

        v

    Aprovação

        |

        v

    Merge


Resumo
======

A política de revisão estabelece critérios técnicos para aprovação das
alterações desenvolvidas pela equipe.

Seu objetivo é preservar a qualidade do software, garantir consistência da
arquitetura e promover compartilhamento de conhecimento entre os
desenvolvedores.
