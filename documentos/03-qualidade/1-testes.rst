=========================================
Estratégia de Testes
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define a estratégia de testes utilizada no desenvolvimento do
Sistema de Gestão de Atividades Complementares.

O objetivo é garantir qualidade, confiabilidade e segurança das funcionalidades
desenvolvidas durante o ciclo de desenvolvimento.


====================
Objetivos
====================

A estratégia de testes busca:

* identificar problemas antes da entrega;
* reduzir regressões;
* garantir funcionamento das regras de negócio;
* facilitar manutenção;
* aumentar confiança nas alterações realizadas.


====================
Princípio Geral
====================

Os testes fazem parte do desenvolvimento da funcionalidade.

Uma tarefa somente será considerada concluída quando possuir:

* implementação;
* validação;
* testes adequados;
* revisão.


Fluxo:


::

    Issue


      |

      v


    Desenvolvimento


      |

      v


    Testes


      |

      v


    Revisão


      |

      v


    Merge



====================
Pirâmide de Testes
====================

O projeto seguirá o conceito de pirâmide de testes.


::

              /\
             /  \
            /E2E \
           /------\
          /        \
         /Integração\
        /------------\
       /              \
      /    Unitários   \
     /------------------\



A maior quantidade de testes deverá ser composta por testes unitários.


====================
Testes Unitários
==================

Responsabilidade
----------------

Testam componentes isolados da aplicação.


Exemplos:

* Services;
* regras de negócio;
* validações;
* métodos de entidades.


Características:

* execução rápida;
* não dependem de banco;
* utilizam mocks quando necessário.


====================
Exemplo
=======


Caso:


::

    Validar aprovação de atividade



Teste:


::

    dado atividade pendente

    quando aprovar

    então status deve ser aprovado



====================
Ferramentas
===========

Tecnologias recomendadas:


Backend:

* JUnit;
* Mockito;
* Spring Test.


====================
Testes de Integração
====================

Responsabilidade
----------------

Validam a comunicação entre componentes.


Exemplos:


* Service + Repository;
* Controller + Service;
* persistência JPA.


Objetivo:

Garantir que partes integradas funcionem corretamente.


====================
Exemplo
=======


Fluxo:


::

    HTTP Request


        |

        v


    Controller


        |

        v


    Service


        |

        v


    Banco



====================
Testes de API
================

Os endpoints REST deverão possuir testes para validar:

* códigos HTTP;
* formato de resposta;
* validações;
* permissões.


Exemplo:


::

    POST /api/v1/certificados



Esperado:


::

    HTTP 201 Created



====================
Testes de Segurança
=====================

Funcionalidades relacionadas à segurança deverão possuir testes específicos.


Exemplos:

* login válido;
* login inválido;
* token expirado;
* acesso sem permissão;
* tentativa de acesso indevido.


====================
Testes de Regras de Negócio
================================

As regras críticas do sistema deverão possuir testes obrigatórios.


Exemplos:


Carga horária:


::

    Estudante não ultrapassa limite permitido.



Aprovação:


::

    Apenas usuários autorizados aprovam atividades.



====================
Cobertura de Código
====================

A cobertura de testes deverá ser acompanhada continuamente.


A cobertura não deve ser analisada apenas como porcentagem.


O objetivo principal é garantir cobertura de:

* regras importantes;
* fluxos críticos;
* componentes de maior risco.


====================
Meta de Cobertura
=================

Recomendação inicial:


Código crítico:

::

    >= 80%



Código geral:

::

    >= 70%



Valores poderão ser ajustados conforme evolução do projeto.


====================
Responsabilidade dos Desenvolvedores
=====================================

Cada desenvolvedor responsável por uma feature deverá:


* implementar testes da funcionalidade;
* corrigir testes quebrados;
* garantir que alterações não gerem regressões.


====================
Papel Rotativo de Qualidade
============================

A cada sprint um desenvolvedor poderá assumir responsabilidade adicional por:


* revisar testes;
* acompanhar cobertura;
* verificar pipeline;
* sugerir melhorias.


Esse papel não substitui a responsabilidade individual dos desenvolvedores.


====================
Testes e Pull Requests
=====================

Pull Requests deverão verificar:


[ ] Código compilando.

[ ] Testes existentes passando.

[ ] Novos testes adicionados quando necessário.

[ ] Cobertura mantida.

[ ] Nenhuma funcionalidade crítica sem teste.



====================
Integração Contínua (CI)
========================

Os testes deverão ser executados automaticamente através de pipeline.


Fluxo:


::

    Push


      |

      v


    Build


      |

      v


    Testes


      |

      v


    Análise


      |

      v


    Merge



====================
Falhas no Pipeline
===================

Um Pull Request com testes falhando não deverá ser integrado.


O responsável pela alteração deverá corrigir:

* erros de implementação;
* testes quebrados;
* problemas de integração.


====================
Testes e Documentação
=====================

Funcionalidades complexas deverão possuir documentação complementar.


Exemplo:


Uma regra de aprovação deverá possuir:

* descrição do comportamento;
* casos esperados;
* casos de erro.


====================
Definition of Done
==================

Uma tarefa será considerada finalizada quando:


[ ] Implementação concluída.

[ ] Testes criados.

[ ] Testes executando com sucesso.

[ ] Código revisado.

[ ] Documentação atualizada quando necessário.

[ ] Pipeline aprovado.



====================
Checklist de Qualidade
======================


[ ] Existem testes para regras críticas.

[ ] Testes unitários são utilizados.

[ ] Integrações importantes são verificadas.

[ ] Segurança possui testes próprios.

[ ] Pipeline executa automaticamente.

[ ] Falhas impedem merge.


====================
Resumo
====================

A estratégia de testes garante que qualidade seja parte do processo de
desenvolvimento e não uma etapa final.

A combinação de testes unitários, integração, API e segurança permite que os
cinco desenvolvedores trabalhem em paralelo mantendo estabilidade e confiança
nas entregas.
