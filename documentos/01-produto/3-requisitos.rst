=========================
Requisitos do Sistema
=========================

.. contents::
   :local:
   :depth: 2

Introdução
----------

Este documento apresenta os requisitos funcionais e não funcionais do Sistema
de Gestão de Atividades Complementares.

Os requisitos descritos neste documento representam as capacidades esperadas do
sistema sob a perspectiva dos usuários e da instituição.

Os requisitos foram organizados de acordo com a divisão modular definida na
arquitetura do sistema, garantindo rastreabilidade entre domínio, arquitetura e
implementação.

Objetivo
---------

Definir de maneira clara e objetiva as funcionalidades e características que
deverão ser implementadas durante o desenvolvimento do sistema.

Este documento servirá como referência para o planejamento das Sprints, criação
das Epics, Features, User Stories e Issues do projeto.

Convenções
----------

Os requisitos funcionais utilizam o padrão:

::

   RF-<MÓDULO>-NN

Exemplo:

::

   RF-AUT-01

Os requisitos não funcionais utilizam o padrão:

::

   RNF-NN

Requisitos Funcionais
---------------------

Autenticação e Autorização
~~~~~~~~~~~~~~~~~~~~~~~~~~

RF-AUT-01
^^^^^^^^^

O sistema deverá permitir que usuários autenticados realizem login utilizando
suas credenciais.

RF-AUT-02
^^^^^^^^^

O sistema deverá permitir o encerramento da sessão do usuário.

RF-AUT-03
^^^^^^^^^

O sistema deverá permitir a recuperação de senha.

RF-AUT-04
^^^^^^^^^

O sistema deverá controlar o acesso às funcionalidades de acordo com o perfil do
usuário.

RF-AUT-05
^^^^^^^^^

O sistema deverá impedir o acesso de usuários não autenticados às áreas
protegidas.

Usuários
~~~~~~~~

RF-USR-01
^^^^^^^^^

O sistema deverá permitir o cadastro de usuários.

RF-USR-02
^^^^^^^^^

O sistema deverá permitir a atualização dos dados cadastrais.

RF-USR-03
^^^^^^^^^

O sistema deverá permitir a consulta das informações do usuário.

RF-USR-04
^^^^^^^^^

O sistema deverá permitir o gerenciamento dos perfis de acesso.

Atividades Complementares
~~~~~~~~~~~~~~~~~~~~~~~~~

RF-ATV-01
^^^^^^^^^

O sistema deverá permitir o cadastro de atividades complementares.

RF-ATV-02
^^^^^^^^^

O sistema deverá permitir a edição das atividades cadastradas.

RF-ATV-03
^^^^^^^^^

O sistema deverá permitir a exclusão de atividades ainda não submetidas para
avaliação.

RF-ATV-04
^^^^^^^^^

O sistema deverá permitir a consulta das atividades cadastradas.

RF-ATV-05
^^^^^^^^^

O sistema deverá calcular automaticamente a carga horária acumulada.

RF-ATV-06
^^^^^^^^^

O sistema deverá agrupar as atividades por categoria.

RF-ATV-07
^^^^^^^^^

O sistema deverá apresentar o percentual concluído em cada categoria.

Certificados
~~~~~~~~~~~~

RF-CER-01
^^^^^^^^^

O sistema deverá permitir o envio de certificados.

RF-CER-02
^^^^^^^^^

O sistema deverá permitir a substituição de certificados antes da conclusão da
avaliação.

RF-CER-03
^^^^^^^^^

O sistema deverá permitir a visualização dos certificados enviados.

RF-CER-04
^^^^^^^^^

O sistema deverá permitir o download dos certificados.

Avaliações
~~~~~~~~~~

RF-AVL-01
^^^^^^^^^

O sistema deverá permitir a submissão de atividades para avaliação.

RF-AVL-02
^^^^^^^^^

O sistema deverá permitir que avaliadores aprovem solicitações.

RF-AVL-03
^^^^^^^^^

O sistema deverá permitir que avaliadores rejeitem solicitações.

RF-AVL-04
^^^^^^^^^

O sistema deverá permitir solicitar correções ao estudante.

RF-AVL-05
^^^^^^^^^

O sistema deverá manter o histórico de avaliações.

Relatórios
~~~~~~~~~~

RF-REL-01
^^^^^^^^^

O sistema deverá gerar relatório consolidado das atividades aprovadas.

RF-REL-02
^^^^^^^^^

O sistema deverá apresentar a carga horária total do estudante.

RF-REL-03
^^^^^^^^^

O sistema deverá permitir exportar os relatórios.

Notificações
~~~~~~~~~~~~

RF-NOT-01
^^^^^^^^^

O sistema deverá notificar o estudante quando ocorrer alteração no estado de
uma solicitação.

RF-NOT-02
^^^^^^^^^

O sistema deverá permitir consultar notificações anteriores.

Administração
~~~~~~~~~~~~~

RF-ADM-01
^^^^^^^^^

O sistema deverá permitir o gerenciamento das categorias de atividades.

RF-ADM-02
^^^^^^^^^

O sistema deverá permitir a configuração dos parâmetros institucionais do
sistema.

RF-ADM-03
^^^^^^^^^

O sistema deverá permitir o gerenciamento de usuários.

Requisitos Não Funcionais
-------------------------

RNF-01
~~~~~~

A aplicação deverá possuir interface responsiva.

RNF-02
~~~~~~

A comunicação entre cliente e servidor deverá utilizar HTTPS.

RNF-03
~~~~~~

As senhas deverão ser armazenadas utilizando algoritmo de hash seguro.

RNF-04
~~~~~~

O sistema deverá utilizar autenticação baseada em JWT.

RNF-05
~~~~~~

A API deverá seguir o padrão REST.

RNF-06
~~~~~~

O backend deverá ser desenvolvido utilizando Java e Spring Boot.

RNF-07
~~~~~~

O frontend deverá ser desenvolvido utilizando Angular e TypeScript.

RNF-08
~~~~~~

A interface deverá utilizar Tailwind CSS.

RNF-09
~~~~~~

Os dados deverão ser persistidos em PostgreSQL.

RNF-10
~~~~~~

O sistema deverá possuir arquitetura modular.

RNF-11
~~~~~~

Toda funcionalidade deverá possuir tratamento adequado de erros.

RNF-12
~~~~~~

O sistema deverá manter registro de eventos relevantes para auditoria.

RNF-13
~~~~~~

O código-fonte deverá seguir os padrões definidos na documentação do projeto.

RNF-14
~~~~~~

Toda alteração deverá ser realizada através de Pull Request.

RNF-15
~~~~~~

O sistema deverá ser compatível com os navegadores modernos.

Rastreabilidade
---------------

A organização adotada neste documento estabelece a seguinte relação entre os
artefatos do projeto:

::

   Visão do Produto
           │
           ▼
   Modelo de Domínio
           │
           ▼
   Módulos do Sistema
           │
           ▼
   Requisitos
           │
           ▼
   Epics
           │
           ▼
   Features
           │
           ▼
   User Stories
           │
           ▼
   Issues
           │
           ▼
   Pull Requests
           │
           ▼
   Implementação

Considerações Finais
--------------------

Este documento constitui a referência oficial para identificação das
funcionalidades do sistema.

Qualquer novo requisito deverá ser analisado pelos Product Owners e, quando
aprovado, incorporado ao Product Backlog antes de sua implementação.
