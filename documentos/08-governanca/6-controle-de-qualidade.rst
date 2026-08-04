=========================================
Controle de Qualidade
=========================================

.. contents::
   :local:
   :depth: 2

Introdução
==========

Este documento define os critérios de qualidade adotados pelo projeto.

Seu objetivo é garantir que todas as funcionalidades entregues pela equipe
atendam aos requisitos funcionais, arquiteturais e de documentação antes de
serem integradas à branch principal.

A qualidade do projeto é responsabilidade de toda a equipe, não apenas dos
revisores.

Objetivos
=========

O controle de qualidade busca:

* reduzir defeitos;
* aumentar a confiabilidade do software;
* padronizar as entregas;
* facilitar revisões;
* preservar a arquitetura do sistema;
* garantir consistência da documentação.

Princípios
==========

Toda funcionalidade deve:

* atender aos requisitos definidos;
* seguir a arquitetura do projeto;
* respeitar os padrões de implementação;
* possuir documentação consistente;
* ser revisada antes da integração.

Definition of Ready
===================

Uma Issue ou Sub-Issue somente poderá iniciar seu desenvolvimento quando
atender aos seguintes critérios:

* possuir descrição clara;
* possuir objetivo definido;
* possuir critérios de aceitação;
* possuir prioridade;
* estar vinculada a uma Epic;
* possuir responsável definido;
* não apresentar dúvidas pendentes.

Caso algum desses critérios não seja atendido, a atividade deverá retornar ao
refinamento.

Definition of Done
==================

Uma Sub-Issue somente será considerada concluída quando:

* toda a implementação estiver finalizada;
* os requisitos forem atendidos;
* a documentação necessária estiver atualizada;
* os testes aplicáveis forem executados;
* o Pull Request for aprovado;
* a branch for integrada à ``main``.

Checklist do Desenvolvedor
==========================

Antes de abrir um Pull Request, o desenvolvedor deve verificar:

* a implementação está concluída;
* o código segue os padrões do projeto;
* não existem arquivos temporários;
* não existem comentários desnecessários;
* a documentação foi atualizada, quando necessário;
* os testes aplicáveis foram executados;
* a branch está sincronizada com a ``main``.

Checklist do Revisor
====================

Durante a revisão devem ser avaliados:

Arquitetura

* respeito às camadas;
* baixo acoplamento;
* alta coesão.

Implementação

* legibilidade;
* organização;
* reutilização;
* simplicidade.

Regras de Negócio

* aderência aos requisitos;
* consistência da implementação.

Persistência

* consultas adequadas;
* integridade dos dados;
* tratamento de erros.

API

* contratos consistentes;
* códigos HTTP corretos;
* validações.

Frontend

* reutilização de componentes;
* acessibilidade;
* consistência visual.

Documentação

* atualização dos documentos impactados.

Critérios para Aprovação
========================

Um Pull Request somente poderá ser aprovado quando:

* todas as alterações solicitadas forem resolvidas;
* não existirem comentários pendentes;
* a implementação estiver completa;
* os padrões definidos forem respeitados;
* a documentação estiver consistente.

Critérios para Rejeição
=======================

Um Pull Request deverá ser rejeitado quando apresentar:

* implementação incompleta;
* defeitos conhecidos;
* violações arquiteturais;
* duplicação significativa de código;
* documentação inconsistente;
* ausência de validações obrigatórias.

Indicadores de Qualidade
========================

Durante o desenvolvimento recomenda-se acompanhar, sempre que possível:

* número de Issues concluídas por Sprint;
* tempo médio de revisão de Pull Requests;
* quantidade de Pull Requests rejeitados;
* quantidade de defeitos identificados durante a revisão;
* cobertura de testes;
* tempo médio entre abertura e integração de Pull Requests.

Melhoria Contínua
=================

Ao término de cada Sprint, a equipe deve avaliar:

* dificuldades encontradas;
* gargalos no fluxo de desenvolvimento;
* oportunidades de melhoria;
* ajustes necessários na documentação;
* necessidade de revisão dos padrões estabelecidos.

As melhorias aprovadas devem ser incorporadas a esta documentação.

Resumo
======

O controle de qualidade estabelece critérios objetivos para início,
desenvolvimento, revisão e conclusão das atividades, garantindo que todas as
entregas apresentem um padrão mínimo de qualidade técnica e documental.
