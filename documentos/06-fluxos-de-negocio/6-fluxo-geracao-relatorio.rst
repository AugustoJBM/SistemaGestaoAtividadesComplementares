=========================================
Fluxo de Geração de Relatório
=========================================

.. contents::
   :local:


Introdução
==========

Este fluxo representa o processo de geração de relatórios e documentos
institucionais relacionados às atividades complementares dos estudantes.

O objetivo é permitir que informações registradas no sistema sejam consolidadas
em documentos utilizados para acompanhamento acadêmico e formalização do
processo institucional.


Objetivo
========

Permitir a geração de relatórios contendo informações das atividades
complementares realizadas pelo estudante, incluindo carga horária, categorias,
situação dos certificados e progresso acadêmico.


Atores
======

Estudante
---------

Responsável pela solicitação de relatórios relacionados às suas próprias
atividades.


Gestor Institucional
--------------------

Responsável pela consulta e geração de relatórios administrativos.


Sistema
-------

Responsável pela consolidação dos dados e criação do documento final.


Pré-condições
=============

Para executar este fluxo, é necessário que:


* o usuário esteja autenticado;
* existam informações cadastradas;
* o usuário possua permissão para gerar o relatório;
* os dados estejam disponíveis no sistema.


Fluxo Principal
===============

O fluxo ocorre da seguinte forma:


1. Usuário acessa a área de relatórios.

2. O sistema apresenta os tipos de relatórios disponíveis.

3. Usuário seleciona o relatório desejado.

4. O sistema coleta os dados necessários.

5. O sistema valida as permissões de acesso.

6. O sistema consolida as informações.

7. O sistema gera o documento.

8. O usuário realiza a visualização ou download.


Representação do Fluxo
======================

.. code-block:: text

    Usuário solicita relatório

            |

            v

    Validação de permissão

            |

            v

    Consulta de informações

            |

            v

    Consolidação dos dados

            |

            v

    Geração do documento

            |

            v

    Disponibilização ao usuário



Tipos de Relatórios
===================


Relatório Individual
--------------------

Apresenta informações completas de um estudante.


Pode conter:


* identificação do estudante;
* atividades realizadas;
* certificados associados;
* carga horária;
* situação atual.


Relatório de Atividades
-----------------------

Apresenta a lista de atividades cadastradas.


Informações:


* nome da atividade;
* categoria;
* período;
* carga horária;
* status.


Relatório de Progresso
----------------------

Apresenta informações do acompanhamento acadêmico.


Exemplo:


.. code-block:: text

    Carga necessária:

    200 horas


    Carga realizada:

    160 horas


    Percentual:

    80%



Documento Institucional
-----------------------

Representa documentos utilizados para formalização do processo.


Exemplos:


* comprovantes;
* declarações;
* relatórios acadêmicos.


Dados Utilizados
================

O relatório pode utilizar informações dos seguintes módulos:


Usuários
--------

Dados de identificação do estudante.


Atividades Complementares
-------------------------

Dados das atividades realizadas.


Certificados
------------

Dados de comprovação documental.


Acompanhamento
--------------

Dados calculados de progresso.


Regras de Negócio
=================


RN-REL-01
---------

Usuários somente podem gerar relatórios permitidos pelo seu perfil.


RN-REL-02
---------

Relatórios devem utilizar informações atualizadas.


RN-REL-03
---------

Somente atividades válidas devem ser consideradas.


RN-REL-04
---------

Certificados rejeitados não devem aparecer como válidos.


RN-REL-05
---------

Documentos gerados devem possuir identificação e data de emissão.


Fluxos Alternativos
===================


Usuário sem permissão
---------------------

Caso o usuário não possua autorização:


1. Sistema bloqueia a geração.

2. Sistema apresenta mensagem informativa.


Ausência de dados
-----------------

Caso não existam informações suficientes:


1. Sistema informa que não existem dados disponíveis.

2. Documento não é gerado.


Falha na geração
----------------

Caso ocorra erro:


1. Sistema registra a falha.

2. Usuário recebe aviso.


Relacionamento com Módulos
==========================


Atividades Complementares
-------------------------

Fornece informações das atividades realizadas.


Certificados
------------

Fornece documentos associados e validações.


Acompanhamento
--------------

Fornece indicadores de progresso.


Relatórios
----------

Responsável pela consolidação final.


Notificações
------------

Pode informar disponibilidade de documentos gerados.


Relacionamento com Arquitetura
==============================


Frontend:

.. code-block:: text

    RelatoriosComponent

            |

            v

    RelatorioService


Backend:

.. code-block:: text

    RelatorioController

            |

            v

    RelatorioService

            |

            v

    Gerador de documentos



Geração de Documento
====================

O processo de geração deve ser separado da regra de negócio.


Estrutura esperada:


.. code-block:: text

    Serviço de relatório

            |

            v

    Preparação dos dados

            |

            v

    Template do documento

            |

            v

    Arquivo final



Possíveis formatos:


* PDF;
* documento digital;
* arquivo para impressão.


Critérios de Aceitação
======================


O fluxo será considerado concluído quando:


* usuário conseguir solicitar relatórios;
* permissões forem verificadas;
* dados forem consolidados corretamente;
* documento for gerado;
* arquivo puder ser visualizado ou baixado;
* testes automatizados existirem.


Relação com Desenvolvimento
===========================


Este fluxo pode gerar:


Epic
----

.. code-block:: text

    Emissão de documentos institucionais


Features
--------

.. code-block:: text

    Geração de relatório individual

    Exportação de documentos

    Consulta de histórico

    Geração de PDF


Issues
------

Exemplos:


* Criar serviço de relatórios.
* Implementar geração de PDF.
* Criar componente Angular.
* Implementar controle de permissões.
* Criar testes de geração.


Resumo
======

O fluxo de geração de relatório representa a etapa final do ciclo das
atividades complementares.

Ele consolida todas as informações geradas durante o processo e transforma os
dados armazenados pelo sistema em documentos utilizados para acompanhamento e
formalização institucional.
