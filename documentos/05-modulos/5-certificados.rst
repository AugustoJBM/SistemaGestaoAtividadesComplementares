=========================================
Módulo de Certificados
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

O módulo de certificados é responsável pelo gerenciamento dos documentos
comprobatórios associados às atividades complementares realizadas pelos
estudantes.

Este módulo permite o envio, armazenamento, consulta e controle dos documentos
utilizados para comprovar a realização das atividades ACC e ACEX.


====================
Objetivo
====================

O objetivo deste módulo é fornecer mecanismos para que estudantes possam
anexar documentos comprobatórios às suas atividades e permitir que o sistema
controle essas informações durante o processo institucional.


O módulo deve permitir:


* upload de certificados;
* armazenamento dos arquivos;
* associação com atividades;
* consulta de documentos;
* controle de status.


====================
Usuários Envolvidos
====================


Estudante
=========

Responsável pelo envio dos certificados relacionados às suas atividades.


Gestor Institucional
====================

Responsável pela análise e validação dos documentos quando aplicável.


Administrador
=============

Responsável pelo gerenciamento geral do módulo.


====================
Responsabilidades
==================


Gerenciamento de Arquivos
-------------------------

Controlar o envio e armazenamento dos documentos enviados pelos usuários.


Associação com Atividades
-------------------------

Relacionar certificados às atividades complementares correspondentes.


Controle de Status
------------------

Acompanhar a situação do documento durante o processo.


Exemplos:


::

    Pendente


    Aprovado


    Rejeitado



Validação Documental
--------------------

Permitir verificar se os documentos atendem aos critérios institucionais.


====================
Conceitos do Domínio
====================


Certificado
===========

Representa um documento utilizado para comprovar a realização de uma atividade.


Exemplos:


::

    Certificado de evento


    Declaração de monitoria


    Comprovante de projeto



Arquivo
=======

Representa o conteúdo digital armazenado pelo sistema.


Possíveis formatos:


::

    PDF


    PNG


    JPG



Status do Certificado
=====================

Representa a situação atual do documento.


Exemplos:


::

    PENDENTE


    APROVADO


    REJEITADO



====================
Funcionalidades
================


====================
Upload de Certificado
=====================

Permite ao estudante enviar um documento comprobatório.


Informações necessárias:


* arquivo;
* nome;
* atividade relacionada.


Fluxo:


::

    Usuário seleciona arquivo


          |


          v


    Angular envia multipart/form-data


          |


          v


    Backend valida arquivo


          |


          v


    Armazenamento



====================
Visualização de Certificado
===========================

Permite consultar documentos enviados.


Possibilidades:


* visualizar informações;
* baixar arquivo;
* verificar status.


====================
Associação com Atividade
========================

Permite vincular um certificado a uma atividade complementar.


Exemplo:


::

    Certificado


          pertence a


    Atividade de extensão



====================
Remoção de Certificado
======================

Permite excluir documentos quando permitido pelas regras do sistema.


====================
Controle de Validação
=====================

Permite alterar o estado do certificado durante análise.


Exemplo:


::

    PENDENTE


        |


        v


    APROVADO



ou


::

    PENDENTE


        |


        v


    REJEITADO



====================
Regras de Negócio
==================


RN-CERT-01
----------

Todo certificado deve estar associado a uma atividade.


RN-CERT-02
----------

Arquivos enviados devem possuir formatos permitidos.


RN-CERT-03
----------

O tamanho máximo dos arquivos deve respeitar a configuração do sistema.


RN-CERT-04
----------

Certificados rejeitados não devem contabilizar carga horária.


RN-CERT-05
----------

Apenas usuários autorizados podem alterar o status de validação.


RN-CERT-06
----------

Um certificado aprovado deve permitir contabilização da atividade associada.


====================
Entidades Relacionadas
======================


Certificado
===========

Representa o documento comprobatório.


Possíveis atributos:


::

    id


    nomeArquivo


    caminhoArquivo


    tipoArquivo


    tamanho


    status


    dataEnvio



Atividade
=========

Representa a atividade associada ao certificado.


Usuário
=======

Representa o estudante proprietário do documento.



====================
Relacionamento com Outros Módulos
==================================


Atividades Complementares
-------------------------

Principal relacionamento do módulo.


::

    Atividade


        1


        |


        *


    Certificado



Usuários
--------

Define o proprietário do documento.


::

    Usuário


        1


        |


        *


    Certificado



Relatórios
----------

Utiliza informações dos certificados para gerar documentos institucionais.


====================
Integrações Backend
===================


Controller
----------

Responsável pelos endpoints de gerenciamento de arquivos.


Service
-------

Responsável pelas regras de upload, validação e associação.


Repository
----------

Responsável pela persistência dos dados do certificado.


Armazenamento
-------------

Responsável pela guarda dos arquivos enviados.


Possíveis soluções:


* armazenamento local;
* armazenamento em serviço externo;
* armazenamento em nuvem.


====================
Endpoints Esperados
===================


Enviar certificado
------------------


::

    POST /api/v1/certificados/upload



Consultar certificados
----------------------


::

    GET /api/v1/certificados



Consultar certificado
---------------------


::

    GET /api/v1/certificados/{id}



Baixar certificado
------------------


::

    GET /api/v1/certificados/{id}/download



Atualizar status
----------------


::

    PATCH /api/v1/certificados/{id}/status



Excluir certificado
-------------------


::

    DELETE /api/v1/certificados/{id}



====================
Componentes Frontend
====================


CertificadosPage
----------------

Tela principal de gerenciamento.


::

    CertificadoService



Responsável pela comunicação com API.


::

    UploadCertificadoComponent



Responsável pelo envio de arquivos.


::

    CertificadoCardComponent



Responsável pela apresentação do documento.



====================
Fluxo Principal
===============


Envio de certificado:


::

    Estudante


        |


        v


    Seleciona arquivo


        |


        v


    Upload Component


        |


        v


    CertificadoService


        |


        v


    API REST


        |


        v


    Validação


        |


        v


    Armazenamento



====================
Relação com Desenvolvimento
===========================


Epic
====


::

    Gerenciamento de certificados



Features
========


::

    Implementar upload de arquivos


    Criar associação com atividades


    Implementar consulta de certificados


    Criar validação documental



Issues
======


Exemplos:


::

    Criar entidade Certificado


    Implementar upload multipart


    Criar tela de envio


    Implementar validação de arquivo


    Criar testes de certificado



====================
Critérios de Aceitação
=====================


O módulo será considerado concluído quando:


[ ] Usuários conseguem enviar certificados.

[ ] Arquivos são validados corretamente.

[ ] Certificados são associados às atividades.

[ ] Status de aprovação funciona.

[ ] Documentos podem ser consultados.

[ ] Testes automatizados existem.



====================
Resumo
====================

O módulo de certificados é responsável pelo gerenciamento dos documentos
comprobatórios das atividades complementares.

Ele garante que as informações cadastradas pelos estudantes possuam evidências
documentais, permitindo controle institucional e integração com relatórios e
acompanhamento acadêmico.
