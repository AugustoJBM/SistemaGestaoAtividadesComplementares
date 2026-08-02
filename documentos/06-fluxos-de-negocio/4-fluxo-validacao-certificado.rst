=========================================
Fluxo de Validação de Certificado
=========================================

.. contents::
   :local:


Introdução
==========

Este fluxo representa o processo de análise e validação dos certificados
enviados pelos estudantes.

A validação é responsável por garantir que os documentos apresentados possuem
as informações necessárias para comprovar a realização das atividades
complementares.


Objetivo
========

Permitir que certificados enviados sejam analisados, aprovados ou rejeitados,
garantindo que somente atividades devidamente comprovadas sejam contabilizadas
no acompanhamento acadêmico.


Atores
======

Estudante
---------

Responsável pelo envio do certificado e acompanhamento do resultado da análise.


Gestor Institucional
--------------------

Responsável pela avaliação dos documentos enviados.


Sistema
-------

Responsável pelo controle do fluxo, atualização de status e comunicação dos
resultados.


Pré-condições
=============

Para executar este fluxo, é necessário que:


* exista um certificado enviado;
* o certificado esteja associado a uma atividade;
* o usuário avaliador possua permissão;
* o documento esteja armazenado corretamente.


Fluxo Principal
===============

O fluxo ocorre da seguinte forma:


1. Gestor acessa os certificados pendentes.

2. O sistema apresenta os documentos enviados.

3. O gestor seleciona um certificado.

4. O sistema apresenta as informações da atividade associada.

5. O gestor analisa o documento.

6. O gestor aprova ou rejeita o certificado.

7. O sistema atualiza o status.

8. O estudante recebe a atualização.

9. Caso aprovado, a carga horária pode ser contabilizada.


Representação do Fluxo
======================

.. code-block:: text

    Certificado pendente

            |

            v

    Análise do documento

            |

            v

    Decisão do avaliador

            |

       +----+----+

       |         |

       v         v

    Aprovado   Rejeitado

       |         |

       v         v

    Contabiliza  Solicita correção



Estados do Certificado
======================

O certificado possui diferentes estados durante seu ciclo de vida.


.. code-block:: text

    ENVIADO

       |

       v

    PENDENTE

       |

    +--+--+

    |     |

    v     v

 APROVADO  REJEITADO



Status
------

PENDENTE
~~~~~~~~

Certificado aguardando análise.


APROVADO
~~~~~~~~

Certificado validado e considerado válido para contabilização.


REJEITADO
~~~~~~~~~

Certificado não aceito conforme regras institucionais.


Dados Avaliados
===============

Durante a análise podem ser verificados:


* autenticidade do documento;
* identificação do estudante;
* instituição emissora;
* período da atividade;
* carga horária;
* compatibilidade com a categoria.


Regras de Negócio
=================


RN-VAL-CERT-01
--------------

Somente usuários autorizados podem validar certificados.


RN-VAL-CERT-02
--------------

Certificados aprovados devem possuir documentação válida.


RN-VAL-CERT-03
--------------

Certificados rejeitados não devem contabilizar carga horária.


RN-VAL-CERT-04
--------------

Toda alteração de status deve ser registrada.


RN-VAL-CERT-05
--------------

O estudante deve ser informado sobre alterações no certificado.


Fluxos Alternativos
===================


Certificado inválido
--------------------

Caso o documento não atenda aos requisitos:


1. Gestor rejeita o certificado.

2. Sistema registra o motivo.

3. Estudante recebe notificação.

4. Estudante pode enviar novo documento.


Documento ilegível
------------------

Caso o arquivo não possa ser analisado:


1. Certificado é rejeitado.

2. Usuário recebe orientação para novo envio.


Sem permissão
-------------

Caso um usuário sem autorização tente validar:


1. Sistema bloqueia a ação.

2. Operação não é realizada.


Relacionamento com Módulos
==========================


Certificados
------------

Responsável pelo armazenamento e controle do documento.


Atividades Complementares
-------------------------

Recebe atualização da validação para contabilização.


Acompanhamento
--------------

Utiliza certificados aprovados para cálculo de progresso.


Notificações
------------

Comunica alterações ao estudante.


Relacionamento com Arquitetura
==============================


Frontend:

.. code-block:: text

    ValidacaoCertificadoComponent

            |

            v

    CertificadoService


Backend:

.. code-block:: text

    CertificadoController

            |

            v

    ValidacaoService

            |

            v

    Atualização de status


Auditoria
=========

O sistema deve manter informações sobre alterações importantes.


Exemplo:


.. code-block:: text

    Certificado:

    001


    Status anterior:

    PENDENTE


    Novo status:

    APROVADO


    Responsável:

    Gestor


    Data:

    01/01/2026


Critérios de Aceitação
======================


O fluxo será considerado concluído quando:


* gestor conseguir consultar certificados pendentes;
* validação puder ser realizada;
* status for atualizado corretamente;
* estudante receber resultado;
* certificados aprovados influenciarem o progresso;
* ações importantes forem registradas.


Relação com Desenvolvimento
===========================


Este fluxo pode gerar:


Epic
----

.. code-block:: text

    Validação de documentos acadêmicos


Features
--------

.. code-block:: text

    Listagem de certificados pendentes

    Aprovação de certificados

    Rejeição com justificativa

    Atualização automática do progresso


Issues
------

Exemplos:


* Criar tela de validação.
* Criar endpoint de alteração de status.
* Implementar regras de aprovação.
* Criar notificações de resultado.
* Implementar testes.


Resumo
======

O fluxo de validação de certificados garante a confiabilidade das informações
utilizadas pelo sistema.

Ele representa a etapa responsável por transformar documentos enviados em dados
válidos para acompanhamento e formalização institucional.
