=========================================
Fluxo de Acompanhamento do Estudante
=========================================

.. contents::
   :local:


Introdução
==========

Este fluxo representa o processo de consulta e acompanhamento da evolução do
estudante em relação às atividades complementares.

O objetivo é permitir que o estudante visualize sua situação acadêmica,
identificando atividades realizadas, carga horária cumprida e requisitos ainda
pendentes.


Objetivo
========

Permitir que o estudante acompanhe o progresso das suas atividades ACC e ACEX,
visualizando informações consolidadas a partir dos registros existentes no
sistema.


Atores
======

Estudante
---------

Responsável pela consulta do próprio progresso acadêmico.


Sistema
-------

Responsável pelo processamento das informações e apresentação dos indicadores.


Pré-condições
=============

Para executar este fluxo, é necessário que:


* o estudante esteja autenticado;
* existam atividades cadastradas;
* o sistema possua informações atualizadas;
* as regras institucionais estejam configuradas.


Fluxo Principal
===============

O fluxo ocorre da seguinte forma:


1. O estudante acessa a área de acompanhamento.

2. O sistema identifica o usuário autenticado.

3. O sistema busca as atividades cadastradas.

4. O sistema verifica os certificados associados.

5. O sistema calcula a carga horária válida.

6. O sistema calcula o percentual de conclusão.

7. O sistema apresenta os indicadores ao estudante.


Representação do Fluxo
======================

.. code-block:: text

    Estudante acessa acompanhamento

                |

                v

    Consulta atividades

                |

                v

    Consulta certificados válidos

                |

                v

    Calcula carga horária

                |

                v

    Calcula progresso

                |

                v

    Exibe dashboard



Informações Apresentadas
========================


Progresso Geral
---------------

Representa o percentual total concluído pelo estudante.


Exemplo:


.. code-block:: text

    Requisito:

    200 horas


    Realizado:

    150 horas


    Progresso:

    75%



Carga Horária
-------------

Apresenta a quantidade de horas realizadas e restantes.


Exemplo:


.. code-block:: text

    Carga concluída:

    150h


    Carga restante:

    50h



Progresso por Categoria
-----------------------

Apresenta a evolução separada pela natureza da atividade.


Exemplo:


.. code-block:: text

    Ensino:

    80%


    Pesquisa:

    60%


    Extensão:

    90%



Pendências
----------

Apresenta informações que precisam de atenção.


Exemplos:


* certificados aguardando validação;
* categorias incompletas;
* atividades sem documentação.


Regras de Negócio
=================


RN-ACO-01
---------

Somente atividades válidas devem ser consideradas no cálculo.


RN-ACO-02
---------

Certificados rejeitados não devem contabilizar carga horária.


RN-ACO-03
---------

O percentual de conclusão não pode ultrapassar 100%.


RN-ACO-04
---------

A carga horária deve respeitar limites definidos pela instituição.


RN-ACO-05
---------

Alterações em atividades ou certificados devem refletir no acompanhamento.


Fluxos Alternativos
===================


Nenhuma atividade cadastrada
----------------------------

Caso o estudante ainda não possua atividades:


1. Sistema informa ausência de registros.

2. Sistema orienta o cadastro de atividades.


Certificados pendentes
----------------------

Caso existam certificados aguardando análise:


1. Sistema apresenta pendências.

2. Horas não aprovadas não são contabilizadas.


Dados inconsistentes
--------------------

Caso existam problemas nos dados:


1. Sistema registra erro.

2. Usuário recebe mensagem informativa.


Relacionamento com Módulos
==========================


Atividades Complementares
-------------------------

Fornece as atividades cadastradas pelo estudante.


Certificados
------------

Define quais atividades possuem comprovação válida.


Acompanhamento
--------------

Responsável pelo processamento dos indicadores.


Relatórios
----------

Pode utilizar os dados consolidados para geração de documentos.


Relacionamento com Arquitetura
==============================


Frontend:

.. code-block:: text

    DashboardAcompanhamentoComponent

                |

                v

    AcompanhamentoService


Backend:

.. code-block:: text

    AcompanhamentoController

                |

                v

    AcompanhamentoService

                |

                v

    Consultas de atividades e certificados



Cálculo de Progresso
====================

O cálculo do progresso deve considerar somente informações válidas.


Exemplo:


.. code-block:: text

    progresso =

    (horas realizadas / horas necessárias) * 100



Exemplo:


.. code-block:: text

    (150 / 200) * 100

    Resultado:

    75%



Critérios de Aceitação
======================


O fluxo será considerado concluído quando:


* estudante visualizar seu progresso;
* carga horária for calculada corretamente;
* categorias forem apresentadas;
* pendências forem identificadas;
* dados forem atualizados automaticamente;
* testes automatizados existirem.


Relação com Desenvolvimento
===========================


Este fluxo pode gerar:


Epic
----

.. code-block:: text

    Acompanhamento acadêmico


Features
--------

.. code-block:: text

    Dashboard de progresso

    Cálculo de carga horária

    Visualização por categoria

    Controle de pendências


Issues
------

Exemplos:


* Criar serviço de cálculo de progresso.
* Criar endpoint de acompanhamento.
* Criar componentes de dashboard.
* Implementar gráficos.
* Criar testes de cálculo.


Resumo
======

O fluxo de acompanhamento do estudante representa a etapa de visualização dos
resultados obtidos durante o processo de atividades complementares.

Ele integra informações de atividades e certificados para fornecer uma visão
clara da evolução acadêmica do estudante.
