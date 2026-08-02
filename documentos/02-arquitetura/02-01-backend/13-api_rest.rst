=========================================
API REST
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define os padrões utilizados para construção da API REST do
Sistema de Gestão de Atividades Complementares.

A API representa a interface pública de comunicação entre o backend Spring Boot
e os consumidores externos, principalmente a aplicação frontend Angular.


====================
Objetivos
====================

A padronização da API tem como objetivos:

* garantir consistência entre endpoints;
* facilitar integração frontend/backend;
* reduzir ambiguidades;
* melhorar manutenção;
* permitir evolução futura.


====================
Princípios REST
====================

A API deverá seguir princípios REST:

* recursos representam entidades do domínio;
* operações utilizam métodos HTTP adequados;
* respostas possuem códigos HTTP corretos;
* comunicação é baseada em representação de recursos.


====================
Estrutura dos Endpoints
====================

Os endpoints deverão utilizar substantivos no plural.


Padrão:


::

    /api/{recurso}



Exemplos:


::

    /api/usuarios


    /api/atividades


    /api/certificados



Evitar:


::

    /api/getUsuarios


    /api/criarAtividade



O verbo já é representado pelo método HTTP.


====================
Versionamento
====================

A API deverá possuir versionamento para permitir evolução sem quebra de
clientes existentes.


Padrão:


::

    /api/v1/{recurso}



Exemplo:


::

    /api/v1/atividades



Alterações incompatíveis deverão gerar uma nova versão.


====================
Métodos HTTP
====================


GET
===

Utilizado para consulta de recursos.


Exemplo:


::

    GET /api/v1/atividades



Retorna atividades disponíveis.


====================

POST
====

Utilizado para criação de novos recursos.


Exemplo:


::

    POST /api/v1/atividades



Cria uma nova atividade.


====================

PUT
===

Utilizado para substituição completa de um recurso.


Exemplo:


::

    PUT /api/v1/usuarios/{id}



====================

PATCH
=====

Utilizado para alterações parciais.


Exemplo:


::

    PATCH /api/v1/atividades/{id}/status



====================

DELETE
======

Utilizado para remoção de recursos.


Exemplo:


::

    DELETE /api/v1/certificados/{id}



====================
Padrão de URLs
====================

URLs deverão representar recursos.


Exemplos:


Usuários:


::

    GET /usuarios


Atividades:


::

    GET /actividades



Certificados:


::

    GET /certificados



Relacionamentos:


::

    GET /usuarios/{id}/atividades



====================
Controllers REST
====================

Cada módulo deverá possuir seus próprios Controllers.


Exemplo:


::

    atividades/

        controller/

            AtividadeController.java



O Controller deverá:

* receber requisições;
* validar entrada;
* chamar Services;
* retornar respostas.


====================
Respostas HTTP
====================

A API deverá utilizar códigos HTTP adequados.


Sucesso:


200 OK
------

Consulta realizada com sucesso.


201 Created
-----------

Recurso criado.


204 No Content
--------------

Operação realizada sem retorno.


Erro:


400 Bad Request
---------------

Dados inválidos.


401 Unauthorized
----------------

Usuário não autenticado.


403 Forbidden
-------------

Usuário sem permissão.


404 Not Found
-------------

Recurso inexistente.


409 Conflict
------------

Conflito de estado.


500 Internal Server Error
-------------------------

Erro inesperado.


====================
Formato de Respostas
====================

As respostas de sucesso deverão utilizar DTOs.


Exemplo:


::

    {
        "id": 15,
        "titulo": "Monitoria",
        "categoria": "ENSINO",
        "cargaHoraria": 40
    }



Entidades nunca deverão ser retornadas diretamente.


====================
Paginação
====================

Listagens deverão utilizar paginação quando houver possibilidade de grande
volume de dados.


Exemplo:


::

    GET /api/v1/atividades?page=0&size=20



Resposta:


::

    {
        "content": [],
        "page": 0,
        "size": 20,
        "totalElements": 100
    }



====================
Filtros
====================

Filtros deverão ser enviados através de parâmetros de consulta.


Exemplo:


::

    GET /atividades?categoria=ENSINO



Evitar:


::

    /atividadesEnsino



====================
Ordenação
====================

Ordenações deverão utilizar parâmetros explícitos.


Exemplo:


::

    GET /atividades?sort=dataCriacao,desc



====================
Upload de Arquivos
====================

Operações envolvendo arquivos deverão utilizar multipart/form-data.


Exemplo:


::

    POST /api/v1/certificados/upload



O backend deverá:

* validar arquivo;
* verificar tamanho;
* verificar formato;
* armazenar referência.


====================
Autenticação na API
====================

Endpoints protegidos deverão exigir autenticação.


Fluxo:


::

    Cliente

        |

        v

    Token JWT

        |

        v

    Spring Security

        |

        v

    Controller



====================
Documentação da API
====================

A API deverá possuir documentação automática utilizando uma ferramenta como:

* OpenAPI;
* Swagger.


Objetivos:

* facilitar integração;
* permitir testes;
* documentar contratos.


====================
Exemplo Completo
====================


Criar atividade:


Requisição:


::

    POST /api/v1/atividades



Body:


::

    {
        "titulo": "Projeto de extensão",
        "categoria": "EXTENSAO",
        "cargaHoraria": 60
    }



Fluxo:


::

    Controller

        |

        v

    DTO

        |

        v

    Service

        |

        v

    Repository



Resposta:


::

    HTTP 201 Created



====================
Boas Práticas
====================


A API deverá:

[ ] Utilizar DTOs.

[ ] Utilizar nomes consistentes.

[ ] Utilizar códigos HTTP corretos.

[ ] Possuir documentação.

[ ] Evitar exposição de detalhes internos.

[ ] Seguir padrão REST.


====================
Checklist de Pull Request
====================


[ ] Endpoint representa um recurso.

[ ] Método HTTP está correto.

[ ] Resposta utiliza DTO.

[ ] Erros seguem padrão global.

[ ] Documentação foi atualizada.

[ ] Segurança foi considerada.


====================
Resumo
====================

A API REST representa o contrato externo do backend.

A padronização definida neste documento garante comunicação previsível entre
frontend e backend, reduz conflitos entre desenvolvedores e permite evolução
segura do sistema.
