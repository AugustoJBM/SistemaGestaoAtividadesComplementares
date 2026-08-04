=========================================
Fluxo da Requisição
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento descreve o fluxo de execução de uma requisição dentro do backend
do Sistema de Gestão de Atividades Complementares.

O objetivo é demonstrar como uma solicitação realizada pelo usuário percorre os
diferentes componentes da aplicação até a obtenção da resposta final.

A compreensão deste fluxo é fundamental para garantir que novas funcionalidades
sejam implementadas respeitando a arquitetura definida.


====================
Visão Geral do Fluxo
====================

Uma requisição seguirá o seguinte fluxo geral:

::

    Usuário

       |

       v

    Frontend Angular

       |

       v

    API REST

       |

       v

    Controller

       |

       v

    Service

       |

       v

    Repository

       |

       v

    Banco de Dados


Após o processamento:

::

    Banco de Dados

       |

       v

    Repository

       |

       v

    Service

       |

       v

    Mapper

       |

       v

    DTO Response

       |

       v

    Controller

       |

       v

    Frontend


====================
Etapas da Execução
====================


1. Interação do Usuário
=======================

A execução inicia quando o usuário realiza uma ação através da interface
Angular.

Exemplos:

* cadastrar uma atividade;
* enviar certificado;
* consultar relatório;
* atualizar dados pessoais.


O frontend será responsável por:

* coletar informações;
* realizar validações básicas;
* enviar requisições HTTP;
* apresentar respostas ao usuário.


Validações realizadas no frontend não substituem validações no backend.


====================
2. Comunicação com a API
========================

O frontend envia uma requisição HTTP para a API REST.

Exemplo:

::

    POST /api/atividades


A requisição poderá conter:

* headers;
* token JWT;
* parâmetros;
* corpo JSON.


Exemplo:

::

    {
        "titulo": "Monitoria",
        "categoria": "ENSINO",
        "cargaHoraria": 40
    }


====================
3. Interceptação de Segurança
=============================

Antes da execução da lógica da aplicação, a requisição passa pelo mecanismo de
segurança.

Responsabilidades:

* validar token JWT;
* identificar usuário;
* verificar permissões;
* bloquear acessos inválidos.


Fluxo:

::

    Requisição

        |

        v

    Spring Security

        |

        v

    Controller


Caso a autenticação falhe:

::

    Requisição

        |

        v

    Erro HTTP 401 ou 403


====================
4. Camada Controller
====================

Após passar pela segurança, a requisição chega ao Controller responsável pelo
recurso solicitado.


O Controller deverá:

* receber a requisição;
* converter dados recebidos;
* validar entrada;
* encaminhar execução.


Exemplo:

::

    AtividadeController

            |

            v

    AtividadeService


O Controller não deverá executar regras de negócio.


====================
5. Conversão de Dados
=====================

Dados recebidos através da API deverão ser representados utilizando DTOs.


Fluxo:

::

    JSON recebido

        |

        v

    RequestDTO

        |

        v

    Mapper

        |

        v

    Entity


Essa separação evita que estruturas externas sejam acopladas diretamente ao
modelo interno da aplicação.


====================
6. Camada Service
==================

A camada Service representa o núcleo da execução da funcionalidade.


Nesta etapa são executadas:

* regras de negócio;
* validações de domínio;
* verificações de estado;
* chamadas para outros módulos;
* controle transacional.


Exemplo:

Cadastro de atividade:

::

    AtividadeService

        verifica estudante

        valida categoria

        calcula carga horária

        salva atividade


====================
7. Comunicação com Repository
================================

Quando necessário, o Service solicita operações de persistência ao Repository.


Exemplo:

::

    AtividadeService

            |

            v

    AtividadeRepository


O Repository é responsável apenas pela comunicação com o banco de dados.


====================
8. Persistência
===============

O Repository realiza operações utilizando as entidades do sistema.


Exemplo:

::

    Entity

       |

       v

    PostgreSQL


A camada de persistência deverá garantir:

* integridade dos dados;
* relacionamento entre entidades;
* operações transacionais.


====================
9. Retorno da Informação
========================

Após a conclusão da operação, o resultado retorna pelo mesmo caminho inverso.


Fluxo:

::

    Banco de Dados

          |

          v

    Repository

          |

          v

    Service

          |

          v

    Mapper

          |

          v

    ResponseDTO

          |

          v

    Controller


====================
10. Resposta HTTP
=================

O Controller retorna a resposta final para o frontend.


Exemplo:

::

    HTTP 201 Created


ou


::

    HTTP 200 OK


ou


::

    HTTP 400 Bad Request


ou


::

    HTTP 404 Not Found


====================
Exemplo Completo
====================


Caso de uso:

Um estudante envia um certificado.


Fluxo:


::

    1. Usuário seleciona arquivo

          |

    2. Angular envia requisição

          |

    3. JWT é validado

          |

    4. CertificadoController recebe dados

          |

    5. CertificadoService verifica regras

          |

    6. CertificadoRepository salva informação

          |

    7. Banco registra certificado

          |

    8. Sistema gera resposta

          |

    9. Usuário recebe confirmação


====================
Comunicação Entre Módulos
=========================

Quando uma funcionalidade precisar utilizar outro módulo, a comunicação deverá
ocorrer através de serviços públicos.


Exemplo:

Ao aprovar uma atividade, o sistema poderá gerar uma notificação.


Fluxo correto:

::

    AvaliacaoService

          |

          v

    NotificacaoService


Fluxo incorreto:

::

    AvaliacaoService

          |

          v

    NotificacaoRepository


O módulo consumidor não deve acessar detalhes internos de outro módulo.


====================
Tratamento de Erros
===================

Falhas ocorridas durante o fluxo deverão ser tratadas de forma padronizada.


Exemplo:


::

    Repository

        erro de banco

             |

             v

    Exception específica

             |

             v

    Exception Handler

             |

             v

    Resposta HTTP adequada


====================
Observabilidade
================

Durante a execução deverão ser registrados eventos importantes para diagnóstico
e manutenção.


Exemplos:

* erros;
* falhas de autenticação;
* operações críticas;
* alterações relevantes.


====================
Checklist de Implementação
==========================

Antes de implementar uma nova funcionalidade:

[ ] O módulo responsável foi identificado.

[ ] O Controller possui apenas responsabilidade HTTP.

[ ] O Service concentra regras de negócio.

[ ] O Repository trata somente persistência.

[ ] DTOs são utilizados na comunicação externa.

[ ] Exceções possuem tratamento adequado.

[ ] Comunicação entre módulos segue os limites definidos.


====================
Resumo
====================

O fluxo de requisição definido neste documento estabelece como os dados devem
percorrer a aplicação desde a interface do usuário até a persistência.

Esse padrão garante previsibilidade, facilita manutenção e permite que todos os
desenvolvedores implementem funcionalidades seguindo o mesmo modelo arquitetural.
