=========================================
Comunicação com API Backend
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define os padrões de comunicação entre o frontend Angular e a
API REST desenvolvida utilizando Spring Boot.

O objetivo é garantir uma integração consistente, previsível e independente
entre as camadas da aplicação.


====================
Objetivos
====================

A comunicação frontend/backend deve:

* utilizar contratos bem definidos;
* evitar dependência de implementação interna;
* padronizar requisições e respostas;
* facilitar desenvolvimento paralelo;
* reduzir erros de integração.


====================
Arquitetura de Comunicação
==========================

O fluxo geral da aplicação será:


::

    Usuário


       |


       v


    Angular


       |


       v


    HTTP/REST


       |


       v


    Spring Boot


       |


       v


    Banco de Dados



====================
Responsabilidades
==================


Frontend Angular
----------------

Responsável por:

* apresentar informações;
* coletar entradas;
* validar dados básicos;
* consumir endpoints.


Backend Spring Boot
-------------------

Responsável por:

* regras de negócio;
* validações definitivas;
* autenticação;
* autorização;
* persistência.


====================
Comunicação HTTP
=================

A comunicação será realizada através de requisições HTTP utilizando API REST.


Métodos utilizados:


GET
===

Consulta de informações.


Exemplo:


::

    GET /api/v1/atividades



POST
====

Criação de recursos.


Exemplo:


::

    POST /api/v1/certificados



PUT
===

Atualização completa.


Exemplo:


::

    PUT /api/v1/usuario/{id}



PATCH
=====

Atualização parcial.


Exemplo:


::

    PATCH /api/v1/atividade/{id}/status



DELETE
======

Remoção de recursos.


Exemplo:


::

    DELETE /api/v1/certificados/{id}



====================
Contratos de Dados
==================

A comunicação deverá utilizar DTOs.


O fluxo será:


::

    Backend


       |

       v


    DTO


       |

       v


    JSON


       |

       v


    Angular



====================
Modelos Frontend
=================

O frontend deverá possuir interfaces ou classes representando os contratos da
API.


Exemplo:


::

    certificado.model.ts



Representa:


::

    CertificadoDTO



====================
Exemplo de Contrato
====================


Resposta da API:


::

    {
        "id": 10,
        "nomeArquivo": "certificado.pdf",
        "status": "PENDENTE",
        "cargaHoraria": 20
    }



Modelo Angular:


::

    interface Certificado {

        id: number;

        nomeArquivo: string;

        status: string;

        cargaHoraria: number;

    }



====================
Versionamento da API
====================

Todas as chamadas deverão utilizar a versão definida pelo backend.


Exemplo:


::

    /api/v1/atividades



Mudanças incompatíveis deverão gerar nova versão.


====================
Configuração da URL
=================

A URL da API não deverá estar espalhada pelo código.


Incorreto:


::

    http://localhost:8080/api



Correto:


::

    environment.ts



Exemplo:


::

    API_URL = "http://localhost:8080/api/v1"



====================
Tratamento de Erros
===================

Erros deverão seguir o padrão definido pelo backend.


Exemplo:


::

    {
        "status": 400,
        "error": "VALIDATION_ERROR",
        "message": "Arquivo inválido"
    }



====================
Tratamento no Frontend
========================

O tratamento deverá ocorrer em níveis adequados.


Erros gerais:


::

    HTTP Interceptor



Erros específicos:


::

    Component ou Service



====================
HTTP Interceptor
================

Interceptors serão responsáveis por comportamentos globais.


Responsabilidades:


* adicionar JWT;
* capturar erros;
* controlar carregamento;
* registrar requisições.


Fluxo:


::

    Component


        |

        v


    Service


        |

        v


    Interceptor


        |

        v


    API



====================
Autenticação nas Requisições
================================

Requisições protegidas deverão enviar token JWT.


Formato:


::

    Authorization: Bearer TOKEN



O interceptor será responsável por adicionar automaticamente.


====================
Paginação
===========

Listagens grandes deverão utilizar paginação.


Exemplo:


Requisição:


::

    GET /atividades?page=0&size=20



Resposta:


::

    {
        "content": [],
        "totalElements": 100,
        "page": 0
    }



====================
Filtros
=========

Filtros deverão ser enviados como parâmetros.


Exemplo:


::

    GET /atividades?categoria=EXTENSAO



====================
Upload de Arquivos
==================

Funcionalidades envolvendo documentos deverão utilizar multipart/form-data.


Exemplo:


::

    POST /certificados/upload



Fluxo:


::

    Usuário seleciona arquivo


          |

          v


    Angular cria FormData


          |

          v


    HTTP Request


          |

          v


    Spring Boot valida arquivo



====================
Download de Arquivos
===================

Downloads deverão utilizar endpoints específicos.


Exemplo:


::

    GET /certificados/{id}/download



O frontend deverá controlar:

* carregamento;
* mensagens de erro;
* nome do arquivo.


====================
Concorrência de Desenvolvimento
================================

Frontend e backend poderão evoluir separadamente.


Quando um endpoint ainda não existir, poderá ser utilizado:


* mock temporário;
* contrato definido previamente;
* documentação OpenAPI.


====================
OpenAPI / Swagger
=================

A documentação da API deverá ser utilizada como fonte de integração.


Benefícios:


* visualizar endpoints;
* testar requisições;
* reduzir erros;
* alinhar frontend e backend.


====================
Boas Práticas
=============


[ ] Utilizar DTOs.

[ ] Não acessar regras internas do backend.

[ ] Centralizar URL da API.

[ ] Utilizar interceptor.

[ ] Tratar erros padronizados.

[ ] Documentar mudanças de contrato.



====================
Checklist de Pull Request
=========================


[ ] Endpoint utilizado está documentado.

[ ] Modelo frontend corresponde ao DTO.

[ ] Tratamento de erro implementado.

[ ] Autenticação considerada.

[ ] Testes realizados.



====================
Resumo
====================

A comunicação entre Angular e Spring Boot será baseada em contratos REST bem
definidos.

Essa separação permite que os desenvolvedores trabalhem de forma independente
em frontend e backend, mantendo integração previsível e reduzindo acoplamento
entre as partes do sistema.
