=========================================
Segurança da Aplicação
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as diretrizes de segurança adotadas no backend do Sistema
de Gestão de Atividades Complementares.

O objetivo é estabelecer mecanismos para garantir:

* identificação segura dos usuários;
* controle de acesso aos recursos;
* proteção das informações acadêmicas;
* integridade das operações realizadas no sistema.


====================
Princípios de Segurança
====================

A segurança da aplicação deverá seguir os seguintes princípios:

* negar acesso por padrão;
* validar permissões no backend;
* nunca confiar exclusivamente no frontend;
* proteger informações sensíveis;
* registrar eventos relevantes;
* separar autenticação de autorização.


====================
Autenticação
====================

Autenticação é o processo responsável por identificar quem está realizando uma
requisição.


Exemplos:

* login com usuário e senha;
* validação de token;
* recuperação de identidade do usuário.


Fluxo:


::

    Usuário

        |

        v

    Credenciais

        |

        v

    Sistema valida identidade

        |

        v

    Token gerado



====================
Autorização
====================

Autorização determina quais ações um usuário autenticado pode executar.


Exemplo:


Usuário autenticado:

::

    Thayson



Pergunta:

::

    Pode aprovar uma atividade?



Resposta:

::

    Depende da permissão atribuída.



====================
Modelo de Controle de Acesso
============================

O sistema utilizará controle baseado em papéis
(Role-Based Access Control - RBAC).


Cada usuário possuirá um perfil que determinará suas permissões.


Exemplo:


::

    USUARIO


    MONITOR


    PROFESSOR


    ADMINISTRADOR



====================
Perfis do Sistema
====================


Usuário Estudante
=================

Responsabilidades:

* cadastrar informações pessoais;
* enviar certificados;
* acompanhar progresso;
* consultar atividades.


Não possui permissão para:

* aprovar atividades;
* alterar configurações institucionais.


====================

Monitor
=======

Responsabilidades:

* analisar solicitações;
* validar documentos;
* acompanhar processos.


====================

Professor
=========

Responsabilidades:

* realizar aprovações institucionais;
* supervisionar processos acadêmicos.


====================

Administrador
=============

Responsabilidades:

* gerenciar configurações;
* administrar usuários;
* controlar parâmetros do sistema.



====================
Spring Security
====================

A aplicação utilizará Spring Security como framework responsável pelo controle
de autenticação e autorização.


Responsabilidades:

* interceptar requisições;
* validar tokens;
* aplicar regras de acesso;
* proteger endpoints.


====================
Autenticação JWT
=================

O sistema utilizará JSON Web Token (JWT) para autenticação stateless.


Fluxo:


::

    Login


      |

      v


    Backend valida credenciais


      |

      v


    JWT gerado


      |

      v


    Cliente envia token nas próximas requisições



====================
Estrutura do Token
====================

O token poderá conter informações como:


::

    {
        "sub": "usuario@email.com",
        "role": "ESTUDANTE",
        "iat": 1720000000,
        "exp": 1720003600
    }



Informações sensíveis não deverão ser armazenadas no token.


====================
Envio do Token
====================

O token deverá ser enviado através do header HTTP:


::

    Authorization: Bearer {token}



====================
Validação do Token
====================

Toda requisição protegida deverá passar por validação.


Fluxo:


::

    Requisição


        |

        v


    JWT Filter


        |

        v


    Validação do token


        |

        v


    Controller



====================
Proteção de Endpoints
======================

Endpoints deverão definir explicitamente seus requisitos de acesso.


Exemplo:


Público:


::

    POST /api/v1/auth/login



Protegido:


::

    GET /api/v1/atividades



Administrativo:


::

    POST /api/v1/configuracoes



====================
Autorização por Recurso
========================

Além do perfil, algumas operações deverão validar propriedade do recurso.


Exemplo:


Um estudante pode acessar:


::

    Suas próprias atividades



Mas não:


::

    Atividades de outro estudante



Essa validação pertence ao Service.


====================
Senhas
====================

Senhas nunca deverão ser armazenadas em texto puro.


Deverá ser utilizado algoritmo seguro de hash.


Exemplo:


::

    BCrypt



====================
Dados Sensíveis
====================

O sistema deverá proteger:

* senhas;
* tokens;
* informações pessoais;
* documentos acadêmicos.


Esses dados não deverão:

* aparecer em logs;
* ser retornados sem necessidade;
* ser expostos em exceções.


====================
CORS
====================

A comunicação entre frontend e backend deverá possuir configuração adequada de
CORS.


Objetivo:

Permitir somente origens autorizadas.


====================
Proteção Contra Ataques Comuns
==============================

A aplicação deverá considerar proteção contra:


SQL Injection
-------------

Mitigado através do uso correto de JPA e consultas parametrizadas.



Cross-Site Request Forgery
--------------------------

Avaliar conforme estratégia de autenticação adotada.



Exposição de Dados
------------------

Controlada através do uso de DTOs.



====================
Auditoria
====================

Operações críticas poderão possuir registro de auditoria.


Exemplos:

* aprovação de atividade;
* alteração administrativa;
* remoção de documentos.


Informações registradas:

* usuário responsável;
* data;
* operação realizada.


====================
Tratamento de Falhas de Segurança
==================================

Falhas relacionadas à segurança deverão possuir respostas padronizadas.


Exemplos:


Token inválido:


::

    HTTP 401



Sem permissão:


::

    HTTP 403



====================
Estrutura Recomendada
====================


::

    autenticacao/

        controller/

        service/

        security/

        dto/

        exception/



Componentes esperados:

* AuthController;
* AuthService;
* JwtService;
* SecurityFilter;
* UserDetailsService.


====================
Checklist de Segurança
====================


[ ] Senhas possuem hash seguro.

[ ] Endpoints protegidos possuem autorização.

[ ] JWT é validado.

[ ] Frontend não é considerado confiável.

[ ] Dados sensíveis não são expostos.

[ ] Perfis possuem permissões definidas.

[ ] Operações críticas possuem auditoria quando necessário.


====================
Resumo
====================

A segurança da aplicação é baseada na separação entre autenticação e
autorização, utilizando Spring Security, JWT e controle de acesso baseado em
papéis.

Essa abordagem garante que cada usuário tenha acesso somente aos recursos
permitidos, protegendo as informações acadêmicas e mantendo a integridade do
sistema.
