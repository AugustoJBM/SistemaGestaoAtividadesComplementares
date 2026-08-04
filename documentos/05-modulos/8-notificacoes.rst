=========================================
Módulo de Notificações
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de notificações é responsável pelo envio de avisos e comunicações
relacionadas aos eventos do Sistema de Gestão de Atividades Complementares.

Este módulo permite informar os usuários sobre mudanças importantes no estado
das suas atividades, certificados e processos institucionais.


====================
Objetivo
====================

O objetivo deste módulo é garantir que os usuários sejam informados sobre
eventos relevantes ocorridos no sistema.

O módulo deve possibilitar:


* criação de notificações;
* visualização de avisos;
* controle de leitura;
* comunicação de eventos importantes;
* integração com outros módulos.


====================
Usuários Envolvidos
====================


Estudante
=========

Principal usuário do módulo.

Recebe notificações relacionadas às suas atividades, certificados e progresso.


Gestor Institucional
====================

Recebe informações relacionadas ao acompanhamento dos estudantes e processos
institucionais.


Administrador
=============

Responsável pelo gerenciamento das configurações de notificações.


====================
Responsabilidades
==================


Geração de Notificações
-----------------------

Criar notificações automaticamente a partir de eventos do sistema.


Exemplos:


::

    Certificado aprovado


    Certificado rejeitado


    Atividade atualizada


    Prazo próximo do vencimento



Entrega de Notificações
-----------------------

Disponibilizar notificações aos usuários.


Possíveis canais:


* notificações internas;
* mensagens no sistema;
* e-mail institucional.


Controle de Leitura
-------------------

Permitir identificar se uma notificação foi visualizada.


====================
Conceitos do Domínio
====================


Notificação
===========

Representa uma mensagem gerada pelo sistema para um usuário.


Possíveis informações:


::

    título


    mensagem


    data criação


    status leitura



Evento
======

Representa uma ação ocorrida no sistema capaz de gerar uma notificação.


Exemplos:


::

    Upload de certificado


    Aprovação de certificado


    Alteração de atividade



Status da Notificação
=====================

Representa a situação da notificação.


Exemplo:


::

    NÃO_LIDA


    LIDA



====================
Funcionalidades
================


====================
Criação Automática
==================

Permite gerar notificações a partir de eventos do sistema.


Exemplo:


::

    Certificado aprovado


          |


          v


    Criar notificação


          |


          v


    Informar estudante



====================
Consulta de Notificações
========================

Permite visualizar notificações recebidas.


Informações exibidas:


* título;
* mensagem;
* data;
* situação.


====================
Marcação como Lida
==================

Permite alterar o estado de uma notificação após visualização.


Fluxo:


::

    Usuário abre notificação


          |


          v


    Sistema atualiza status


          |


          v


    Notificação marcada como lida



====================
Notificações por Evento
=======================

Permite associar diferentes eventos às mensagens.


Exemplos:


Certificados
------------

::

    Certificado aprovado


    Certificado rejeitado



Atividades
----------

::

    Atividade criada


    Atividade atualizada



Acompanhamento
--------------

::

    Carga horária próxima do limite


    Categoria incompleta



====================
Regras de Negócio
==================


RN-NOT-01
---------

Notificações devem possuir um usuário destinatário.


RN-NOT-02
---------

Uma notificação deve possuir uma mensagem identificável.


RN-NOT-03
---------

O usuário somente pode visualizar suas próprias notificações.


RN-NOT-04
---------

Eventos importantes devem gerar notificações automaticamente.


RN-NOT-05
---------

Notificações lidas devem permanecer disponíveis para consulta histórica.


====================
Entidades Relacionadas
======================


Notificação
===========

Representa uma comunicação enviada ao usuário.


Possíveis atributos:


::

    id


    titulo


    mensagem


    dataCriacao


    lida


    usuarioDestino



Usuário
=======

Representa o destinatário da notificação.


Evento
======

Representa a ação que originou a notificação.


====================
Relacionamento com Outros Módulos
==================================


Certificados
------------

Eventos de certificados podem gerar notificações.


::

    Certificado aprovado


          |


          v


    Notificação



Atividades Complementares
-------------------------

Alterações em atividades podem gerar avisos.


::

    Atividade atualizada


          |


          v


    Notificação



Acompanhamento
--------------

Indicadores podem gerar alertas.


::

    Pendência encontrada


          |


          v


    Notificação



Usuários
--------

Define os destinatários das mensagens.


====================
Integrações Backend
===================


Controller
----------

Responsável pelos endpoints de consulta e gerenciamento.


Service
-------

Responsável pelas regras de criação e processamento.


Event System
------------

Responsável pela comunicação entre módulos.


Exemplo:


::

    CertificadoService


          |


          v


    NotificationService



Repository
----------

Responsável pela persistência das notificações.


====================
Endpoints Esperados
===================


Consultar notificações
----------------------


::

    GET /api/v1/notificacoes



Consultar quantidade de não lidas
---------------------------------


::

    GET /api/v1/notificacoes/nao-lidas



Marcar como lida
----------------


::

    PATCH /api/v1/notificacoes/{id}/lida



====================
Componentes Frontend
====================


NotificacoesComponent
---------------------

Apresenta a lista de notificações.


::

    NotificacaoService



Responsável pela comunicação com API.


::

    NotificationBadgeComponent



Exibe quantidade de notificações pendentes.


::

    NotificacaoCardComponent



Apresenta uma notificação individual.



====================
Fluxo Principal
===============


Geração de notificação:


::

    Evento do sistema


          |


          v


    Serviço responsável


          |


          v


    NotificationService


          |


          v


    Persistência


          |


          v


    Usuário recebe aviso



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Sistema de notificações



Features
========


::

    Criar estrutura de notificações


    Implementar consulta de avisos


    Criar marcação de leitura


    Integrar eventos do sistema



Issues
======


Exemplos:


::

    Criar entidade Notificação


    Implementar serviço de notificações


    Criar componente Angular


    Implementar testes



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Usuários recebem notificações.

[ ] Eventos importantes geram avisos.

[ ] Notificações podem ser consultadas.

[ ] Usuários conseguem marcar como lidas.

[ ] Permissões são respeitadas.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de notificações fornece comunicação entre o sistema e seus usuários.

Ele reduz a necessidade de acompanhamento manual, informando automaticamente
sobre mudanças importantes relacionadas às atividades complementares,
certificados e progresso acadêmico.
