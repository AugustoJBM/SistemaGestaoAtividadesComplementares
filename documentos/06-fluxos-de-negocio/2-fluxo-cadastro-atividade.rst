=========================================
Fluxo de Cadastro de Atividade
=========================================

.. contents::
   :local:


Introdução
==========

Este fluxo representa o processo realizado pelo estudante para cadastrar uma
nova atividade complementar no sistema.

O objetivo é permitir que atividades ACC e ACEX sejam registradas, classificadas
e posteriormente utilizadas no cálculo de carga horária.


Objetivo
========

Permitir que o estudante registre uma atividade complementar informando os
dados necessários para controle institucional.


Atores
======

Estudante
---------

Responsável pelo cadastro da atividade.


Sistema
-------

Responsável pela validação das informações e armazenamento dos dados.


Pré-condições
=============

Para executar este fluxo, é necessário que:


* o estudante esteja autenticado;
* o usuário possua permissão de cadastro;
* o sistema esteja disponível.


Fluxo Principal
===============

O fluxo ocorre da seguinte maneira:


1. O estudante acessa a área de atividades complementares.

2. O sistema apresenta as atividades cadastradas.

3. O estudante seleciona a opção de adicionar nova atividade.

4. O sistema apresenta o formulário de cadastro.

5. O estudante informa os dados da atividade.

6. O estudante seleciona a natureza da atividade.

7. O estudante informa a carga horária realizada.

8. O sistema valida os dados informados.

9. O sistema registra a atividade.

10. A atividade fica disponível para associação de certificado.


Representação do Fluxo
======================

.. code-block:: text

    Estudante

        |

        v

    Tela de cadastro

        |

        v

    Validação dos dados

        |

        v

    Serviço de atividades

        |

        v

    Persistência

        |

        v

    Atividade criada


Dados Informados
================

Durante o cadastro, podem ser informados:


* título da atividade;
* descrição;
* natureza da atividade;
* data de realização;
* carga horária;
* instituição responsável.


Exemplo:

.. code-block:: text

    Atividade:
    Participação em evento acadêmico

    Natureza:
    Extensão

    Carga horária:
    20 horas


Regras de Negócio
=================


RN-CAD-ATV-01
-------------

Toda atividade deve possuir um estudante responsável.


RN-CAD-ATV-02
-------------

Toda atividade deve possuir uma natureza definida.


RN-CAD-ATV-03
-------------

A carga horária informada deve possuir valor válido.


RN-CAD-ATV-04
-------------

Atividades cadastradas devem permanecer associadas ao usuário responsável.


RN-CAD-ATV-05
-------------

O sistema deve impedir cadastro de informações obrigatórias vazias.


Fluxos Alternativos
===================


Dados inválidos
---------------

Caso informações obrigatórias estejam incorretas:


1. Sistema identifica erro.

2. Sistema informa o problema ao usuário.

3. Usuário corrige os dados.

4. Cadastro é enviado novamente.


Usuário sem permissão
---------------------

Caso o usuário não possua autorização:


1. Sistema bloqueia a operação.

2. Usuário recebe mensagem de acesso negado.


Relacionamento com Módulos
==========================


Este fluxo utiliza os seguintes módulos:


Usuários
--------

Responsável pela identificação do estudante.


Atividades Complementares
-------------------------

Responsável pelo gerenciamento da atividade cadastrada.


Autenticação
------------

Responsável pela proteção do acesso.


Relacionamento com Arquitetura
==============================


Frontend:

.. code-block:: text

    AtividadeFormComponent

            |

            v

    AtividadeService


Backend:

.. code-block:: text

    Controller

        |

        v

    Service

        |

        v

    Repository


Critérios de Aceitação
======================


O fluxo será considerado concluído quando:


* estudante conseguir cadastrar atividade;
* dados obrigatórios forem validados;
* atividade for armazenada corretamente;
* atividade aparecer na listagem;
* testes automatizados forem implementados.


Relação com Desenvolvimento
===========================


Este fluxo pode gerar:


Epic
----

.. code-block:: text

    Gerenciamento de atividades complementares


Features
--------

.. code-block:: text

    Cadastro de atividade

    Validação de dados

    Classificação por natureza


Issues
------

Exemplos:


* Criar entidade Atividade.
* Criar endpoint de cadastro.
* Criar formulário Angular.
* Implementar validações.
* Criar testes.


Resumo
======

O fluxo de cadastro de atividade representa o primeiro passo do ciclo das
atividades complementares.

Ele permite que estudantes registrem suas experiências acadêmicas, fornecendo
as informações necessárias para validação, acompanhamento e geração de
relatórios.
