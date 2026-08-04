=========================================
Fluxo de Envio de Certificado
=========================================

.. contents::
   :local:


Introdução
==========

Este fluxo representa o processo realizado pelo estudante para enviar um
certificado ou documento comprobatório relacionado a uma atividade complementar.

O objetivo é permitir que o sistema associe evidências documentais às
atividades cadastradas, garantindo maior controle e formalização do processo
institucional.


Objetivo
========

Permitir que o estudante envie documentos comprobatórios vinculados às suas
atividades ACC e ACEX.


Atores
======

Estudante
---------

Responsável pelo envio do certificado.


Sistema
-------

Responsável pela validação, armazenamento e associação do documento.


Gestor Institucional
--------------------

Responsável pela análise do documento quando aplicável.


Pré-condições
=============

Para executar este fluxo, é necessário que:


* o estudante esteja autenticado;
* exista uma atividade cadastrada;
* o arquivo esteja em formato permitido;
* o usuário possua permissão para anexar documentos.


Fluxo Principal
===============

O fluxo ocorre da seguinte forma:


1. O estudante acessa uma atividade cadastrada.

2. O sistema apresenta a opção de anexar certificado.

3. O estudante seleciona o arquivo desejado.

4. O sistema recebe o arquivo enviado.

5. O sistema valida formato e tamanho do documento.

6. O sistema armazena o arquivo.

7. O certificado é associado à atividade correspondente.

8. O sistema registra o envio.

9. O certificado fica disponível para análise.


Representação do Fluxo
======================

.. code-block:: text

    Estudante

        |

        v

    Seleciona atividade

        |

        v

    Envia certificado

        |

        v

    Validação do arquivo

        |

        v

    Armazenamento

        |

        v

    Certificado associado


Dados Informados
================

Durante o envio podem ser informados:


* arquivo do certificado;
* nome do documento;
* atividade relacionada;
* descrição complementar.


Exemplo:

.. code-block:: text

    Documento:

    certificado-evento.pdf


    Atividade:

    Participação em congresso


    Carga horária:

    10 horas


Validações do Arquivo
=====================

Antes do armazenamento, o sistema deve validar:


Formato
-------

Exemplos de formatos aceitos:


* PDF;
* JPG;
* PNG.


Tamanho
-------

O sistema deve impedir envio de arquivos acima do limite definido.


Integridade
-----------

O arquivo enviado deve possuir conteúdo válido.


Regras de Negócio
=================


RN-ENV-CERT-01
--------------

Todo certificado deve estar associado a uma atividade existente.


RN-ENV-CERT-02
--------------

Somente o estudante responsável pela atividade pode enviar documentos.


RN-ENV-CERT-03
--------------

Arquivos devem respeitar as regras de formato e tamanho.


RN-ENV-CERT-04
--------------

Certificados enviados devem possuir status inicial de pendente.


RN-ENV-CERT-05
--------------

Documentos inválidos não devem ser armazenados.


Fluxos Alternativos
===================


Arquivo inválido
----------------

Caso o arquivo não seja aceito:


1. Sistema identifica o problema.

2. Sistema informa o motivo da rejeição.

3. Usuário seleciona outro arquivo.


Atividade inexistente
---------------------

Caso não exista atividade associada:


1. Sistema impede o envio.

2. Usuário deve selecionar uma atividade válida.


Falha no armazenamento
----------------------

Caso ocorra erro interno:


1. Sistema informa falha no processamento.

2. Documento não é associado à atividade.


Relacionamento com Módulos
==========================


Usuários
--------

Responsável pela identificação do estudante.


Atividades Complementares
-------------------------

Fornece a atividade que receberá o certificado.


Certificados
------------

Responsável pelo gerenciamento do documento enviado.


Notificações
------------

Pode informar eventos relacionados ao processamento.


Relacionamento com Arquitetura
==============================


Frontend:

.. code-block:: text

    UploadCertificadoComponent

            |

            v

    CertificadoService


Backend:

.. code-block:: text

    CertificadoController

            |

            v

    CertificadoService

            |

            v

    Armazenamento


Integração com Armazenamento
============================

O sistema deve abstrair o armazenamento dos arquivos.


Possíveis implementações:


* armazenamento local;
* serviço externo;
* armazenamento em nuvem.


O módulo deve manter separação entre:


.. code-block:: text

    Aplicação

        |

        v

    Serviço de arquivos

        |

        v

    Local de armazenamento


Critérios de Aceitação
======================


O fluxo será considerado concluído quando:


* usuário conseguir enviar certificado;
* arquivo possuir validação;
* documento for associado corretamente;
* certificado puder ser consultado;
* erros forem tratados;
* testes automatizados existirem.


Relação com Desenvolvimento
===========================


Este fluxo pode gerar:


Epic
----

.. code-block:: text

    Gerenciamento de certificados


Features
--------

.. code-block:: text

    Upload de certificado

    Validação de arquivo

    Associação com atividade

    Consulta de documentos


Issues
------

Exemplos:


* Criar entidade Certificado.
* Criar endpoint de upload.
* Implementar armazenamento.
* Criar componente Angular de upload.
* Criar testes de integração.


Resumo
======

O fluxo de envio de certificado representa a etapa responsável por comprovar
as atividades cadastradas pelo estudante.

Ele conecta o cadastro de atividades com a validação documental, garantindo que
as informações utilizadas pelo sistema possuam evidências associadas.
