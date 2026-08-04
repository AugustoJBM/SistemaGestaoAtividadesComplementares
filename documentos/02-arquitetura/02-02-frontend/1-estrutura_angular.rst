=========================================
Estrutura Angular
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define a organização estrutural da aplicação frontend
desenvolvida utilizando Angular e TypeScript para o Sistema de Gestão de
Atividades Complementares.

O objetivo é estabelecer padrões que permitam o desenvolvimento paralelo entre
os cinco integrantes da equipe, mantendo organização, reutilização e
manutenção simplificada.


====================
Objetivos
====================

A estrutura frontend busca:

* separar responsabilidades;
* organizar funcionalidades por domínio;
* facilitar evolução do sistema;
* reduzir acoplamento;
* permitir desenvolvimento paralelo.


====================
Arquitetura Adotada
====================

O frontend seguirá uma arquitetura baseada em componentes Angular organizada
por funcionalidades.


O sistema será dividido em:


::

    Funcionalidades

          |

          v

    Componentes

          |

          v

    Serviços

          |

          v

    API Backend



====================
Organização por Domínio
======================

A aplicação deverá ser organizada preferencialmente por módulos de negócio.


Exemplo:


::

    src/app/


        autenticacao/


        atividades/


        certificados/


        usuarios/


        relatorios/



Cada domínio possui seus próprios arquivos relacionados.


====================
Estrutura Geral
================


Estrutura recomendada:


::

    src/

     app/

       core/

       shared/

       features/

       layout/

       app.routes.ts



====================
Core
====================

O diretório core contém funcionalidades globais da aplicação.


Exemplos:


* autenticação;
* interceptors;
* guards;
* configurações;
* serviços globais.


Estrutura:


::

    core/


        guards/


        interceptors/


        services/


        models/



====================
Shared
====================

Contém elementos reutilizáveis entre diferentes funcionalidades.


Exemplos:


* componentes genéricos;
* pipes;
* diretivas;
* elementos visuais.


Estrutura:


::

    shared/


        components/


        directives/


        pipes/



====================
Features
====================

Features representam módulos funcionais do sistema.


Exemplo:


::

    features/


        certificados/


            pages/


            components/


            services/


            models/



Cada feature deve possuir autonomia sobre seus elementos.


====================
Layout
====================

Responsável pela estrutura visual geral.


Exemplos:


* menu lateral;
* cabeçalho;
* navegação;
* containers.


Estrutura:


::

    layout/


        header/


        sidebar/


        main-layout/



====================
Componentes
=============

Componentes Angular representam unidades visuais independentes.


Responsabilidades:


* controlar interface;
* receber dados;
* emitir eventos;
* interagir com serviços.


Não devem:

* realizar chamadas HTTP diretamente;
* conter regras complexas de negócio.


====================
Pages
=====

Pages representam telas completas da aplicação.


Exemplos:


::

    certificados-lista-page


    login-page


    dashboard-page



Responsabilidades:


* organizar componentes;
* controlar fluxo da tela;
* carregar dados necessários.


====================
Services
========

Services concentram comunicação e lógica compartilhada.


Exemplos:


::

    CertificadoService


    UsuarioService


    AuthService



Responsabilidades:


* consumir API;
* armazenar dados temporários;
* compartilhar informações.


====================
Models
======

Models representam estruturas utilizadas no frontend.


Exemplos:


::

    Usuario


    Certificado


    Atividade



Devem representar contratos da API.


====================
Rotas
=====

As rotas serão organizadas utilizando Angular Router.


Exemplo:


::

    /login


    /dashboard


    /atividades


    /certificados



====================
Lazy Loading
===========

Funcionalidades maiores deverão utilizar carregamento sob demanda.


Objetivos:

* reduzir carregamento inicial;
* melhorar desempenho;
* separar módulos.


====================
Comunicação com Backend
=======================

O frontend não deverá acessar o banco ou regras internas.


Fluxo:


::

    Component


        |

        v


    Service


        |

        v


    HTTP Client


        |

        v


    Spring Boot API



====================
Integração com Tailwind
======================

Tailwind CSS será utilizado para estilização.


Responsabilidades:

* padronizar interface;
* criar layouts responsivos;
* reduzir CSS duplicado.


====================
Padrões de Nomenclatura
========================


Componentes:


::

    atividade-card.component.ts



Services:


::

    atividade.service.ts



Models:


::

    atividade.model.ts



Interfaces:


::

    usuario.interface.ts



====================
Responsabilidade dos Desenvolvedores
=====================================

Cada desenvolvedor deverá:

* seguir a estrutura definida;
* evitar criação de padrões próprios;
* reutilizar componentes existentes;
* manter organização por domínio.


====================
Checklist
===========


[ ] Código organizado por feature.

[ ] Componentes possuem responsabilidade clara.

[ ] Serviços isolam comunicação.

[ ] Modelos representam contratos.

[ ] Elementos compartilhados estão em shared.

[ ] Estrutura segue padrão definido.


====================
Resumo
====================

A arquitetura frontend será baseada em Angular organizado por funcionalidades,
utilizando componentes, serviços e modelos separados.

Essa abordagem permite que os cinco desenvolvedores trabalhem em diferentes
módulos simultaneamente, mantendo baixo acoplamento e facilidade de manutenção.
