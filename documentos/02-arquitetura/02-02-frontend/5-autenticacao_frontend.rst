=========================================
Autenticação Frontend
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define o fluxo de autenticação e autorização no frontend Angular
do Sistema de Gestão de Atividades Complementares.

A autenticação permite identificar usuários no sistema, enquanto a autorização
controla quais funcionalidades podem ser acessadas.


====================
Objetivos
====================

A implementação de autenticação deve:

* proteger áreas privadas;
* controlar acesso às funcionalidades;
* integrar com Spring Security;
* manter sessão do usuário;
* evitar exposição de recursos protegidos.


====================
Arquitetura de Segurança
=========================

A autenticação seguirá o modelo baseado em JWT.


Fluxo:


::

    Usuário


       |

       v


    Login Angular


       |

       v


    API Spring Boot


       |

       v


    Validação


       |

       v


    JWT


       |

       v


    Angular armazena sessão



====================
Responsabilidades
==================


Frontend
=========

Responsável por:


* tela de login;
* armazenamento do token;
* envio automático do JWT;
* proteção de rotas;
* controle visual de permissões.


Backend
=======

Responsável por:


* validar credenciais;
* gerar JWT;
* validar permissões;
* proteger recursos.


====================
AuthService
===========

O AuthService será responsável pelo gerenciamento da autenticação.


Responsabilidades:


* realizar login;
* realizar logout;
* armazenar usuário autenticado;
* verificar sessão.


Exemplo:


::

    AuthService


        login()


        logout()


        getUsuarioAtual()



====================
Fluxo de Login
=============


Fluxo:


::

    Usuário informa dados


          |

          v


    LoginComponent


          |

          v


    AuthService


          |

          v


    POST /auth/login


          |

          v


    Spring Security


          |

          v


    JWT retornado



====================
Token JWT
==========

O token JWT será utilizado para identificar requisições autenticadas.


Formato:


::

    Authorization:

    Bearer <token>



====================
Armazenamento do Token
======================

O token deverá ser armazenado considerando segurança e necessidade da
aplicação.


Possíveis estratégias:


LocalStorage
------------

Permite persistência entre sessões.


Possui maior exposição em ataques XSS.


SessionStorage
--------------

Mantém sessão apenas durante o navegador aberto.


Cookies HttpOnly
----------------

Maior segurança, pois o JavaScript não acessa diretamente o token.


A escolha final deverá considerar as necessidades do projeto.


====================
HTTP Interceptor
================

O interceptor adicionará automaticamente o token nas requisições protegidas.


Fluxo:


::

    Requisição HTTP


          |

          v


    AuthInterceptor


          |

          v


    Adiciona JWT


          |

          v


    API



====================
Exemplo
========


Antes:


::

    GET /api/v1/atividades



Depois:


::

    GET /api/v1/atividades


    Authorization: Bearer TOKEN



====================
Proteção de Rotas
=================

Rotas privadas deverão utilizar Guards.


Exemplo:


::

    /dashboard


    /certificados


    /atividades



Usuários não autenticados deverão ser redirecionados.


====================
AuthGuard
==========

O AuthGuard verifica se existe uma sessão válida antes de permitir acesso.


Fluxo:


::

    Usuário acessa rota


          |

          v


    AuthGuard


          |

          +---- autenticado

          |

          v

        Página



          |

          +---- não autenticado

          |

          v


        Login



====================
Controle de Permissões
======================

Além da autenticação, o sistema deverá controlar permissões.


Exemplo:


Usuário comum:


::

    consultar atividades


    enviar certificados



Administrador:


::

    gerenciar usuários


    aprovar documentos



====================
RoleGuard
==========

Rotas específicas poderão utilizar controle por papel.


Exemplo:


::

    /administracao



Permitido:


::

    ROLE_ADMIN



====================
Estado do Usuário
=================

Informações do usuário autenticado poderão ser mantidas em um serviço global.


Exemplo:


::

    UsuarioAtualService



Dados possíveis:


* nome;
* matrícula;
* papel;
* permissões.


====================
Logout
=======

O logout deverá:


* remover sessão local;
* limpar usuário atual;
* redirecionar para login.


Fluxo:


::

    Logout


       |

       v


    Limpar token


       |

       v


    Redirecionar



====================
Expiração do Token
==================

Quando o JWT expirar:


O sistema deverá:


* detectar resposta HTTP 401;
* remover sessão inválida;
* solicitar novo login.


====================
Tratamento de Erros
===================


Erros comuns:


401
===

Usuário não autenticado.


403
===

Usuário autenticado sem permissão.


400
===

Dados inválidos.



====================
Boas Práticas
=============


[ ] Nunca confiar apenas no frontend para segurança.

[ ] Regras definitivas permanecem no backend.

[ ] Utilizar interceptor para JWT.

[ ] Proteger rotas privadas.

[ ] Controlar permissões visualmente.

[ ] Tratar expiração de sessão.



====================
Checklist de Pull Request
=========================


[ ] Login integrado com API.

[ ] Token tratado corretamente.

[ ] Rotas protegidas.

[ ] Erros tratados.

[ ] Permissões consideradas.

[ ] Testes adicionados.



====================
Resumo
====================

A autenticação frontend será responsável pela interação do usuário com o
sistema, enquanto a segurança real permanecerá no backend.

O uso de JWT, interceptors e guards permite uma aplicação organizada, segura e
integrada ao Spring Boot.
