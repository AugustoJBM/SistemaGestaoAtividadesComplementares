=========================================
Componentes Angular
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define os padrões de criação, organização e utilização dos
componentes Angular no frontend do Sistema de Gestão de Atividades
Complementares.

Componentes são os principais elementos responsáveis pela construção da
interface da aplicação.


====================
Objetivos
====================

A utilização adequada de componentes busca:

* aumentar reutilização;
* reduzir duplicação;
* facilitar manutenção;
* separar responsabilidades;
* permitir desenvolvimento paralelo.


====================
Conceito de Componente
========================

Um componente Angular representa uma unidade independente da interface.


Ele possui:


Template
--------

Responsável pela estrutura HTML.


Style
-----

Responsável pela aparência visual.


Classe TypeScript
-----------------

Responsável pelo comportamento.


Exemplo:


::

    atividade-card.component.ts


    atividade-card.component.html


    atividade-card.component.css



====================
Tipos de Componentes
====================

Os componentes serão divididos em três categorias:


====================
Componentes de Página
====================

Representam telas completas da aplicação.


Exemplos:


::

    LoginPage


    DashboardPage


    CertificadosPage



Responsabilidades:

* organizar a tela;
* carregar dados;
* controlar fluxo da página.


Não devem possuir:

* componentes visuais complexos;
* regras reutilizáveis.


====================
Componentes de Domínio
=====================

Representam elementos específicos de uma funcionalidade.


Exemplos:


::

    CertificadoCard


    AtividadeTabela


    ProgressoCategoria



Responsabilidades:

* apresentar informações;
* controlar interações locais;
* emitir eventos.


====================
Componentes Compartilhados
===========================

São componentes utilizados em várias partes do sistema.


Exemplos:


::

    Button


    Modal


    Loading


    Alert



Devem permanecer em:


::

    shared/components



====================
Componentes Inteligentes
==========================

Componentes inteligentes são responsáveis por controlar dados.


Também chamados de:

::

    Smart Components



Responsabilidades:

* consumir services;
* controlar estado;
* preparar dados.


Exemplo:


::

    ListaCertificadosPage



Obtém dados:


::

    CertificadoService



e envia para componentes visuais.


====================
Componentes Apresentacionais
================================

Componentes apresentacionais possuem foco apenas na interface.


Também chamados de:


::

    Dumb Components



Responsabilidades:


* receber dados;
* mostrar informações;
* emitir eventos.


Exemplo:


::

    CertificadoCard



Recebe:


::

    certificado



Retorna:


::

    evento clique



====================
Comunicação entre Componentes
================================


A comunicação deverá ocorrer através de:


====================
Input
=====

Utilizado para enviar dados do componente pai para o filho.


Exemplo:


::

    <app-certificado-card

        [certificado]="certificado">

    </app-certificado-card>



====================
Output
======

Utilizado para enviar eventos do filho para o pai.


Exemplo:


::

    certificadoSelecionado.emit(id)



====================
Evitar Acoplamento
=================

Componentes não devem acessar diretamente outros componentes.


Evitar:


::

    Componente A


        acessa


    Componente B



Preferir:


::

    Pai


      |

      v


    Filho



====================
Organização de Componentes
===========================


Exemplo:


::

    certificados/


        pages/


            certificados-page/


        components/


            certificado-card/


            certificado-upload/


        services/


            certificado.service.ts



====================
Tamanho dos Componentes
=======================

Componentes devem possuir responsabilidade limitada.


Evitar componentes gigantes:


::

    dashboard.component.ts


    2000 linhas



Preferir:


::

    dashboard


        |

        +-- resumo-card


        +-- grafico-progresso


        +-- notificacoes



====================
Reutilização
=============

Antes de criar um novo componente, verificar se já existe um componente
semelhante.


Exemplo:


Evitar:


::

    usuario-button


    certificado-button


    atividade-button



Preferir:


::

    shared/button



====================
Formulários
===========

Formulários deverão utilizar componentes específicos quando possuírem
complexidade.


Exemplo:


::

    certificado-form.component



Responsabilidades:

* controlar campos;
* exibir validações;
* emitir submissão.


====================
Estado Interno
=============

Componentes podem possuir estado local.


Exemplos:


* carregando;
* item selecionado;
* modal aberto.


Estados compartilhados devem permanecer em Services ou mecanismos específicos de
estado.


====================
Tratamento de Erros
===================

Componentes não devem tratar erros diretamente da API.


Fluxo correto:


::

    Service


       |

       v


    Tratamento


       |

       v


    Component



====================
Boas Práticas
=============


Componentes devem:


[ ] Ter uma responsabilidade clara.

[ ] Ser pequenos.

[ ] Evitar lógica de negócio.

[ ] Utilizar Inputs e Outputs corretamente.

[ ] Reutilizar componentes existentes.

[ ] Possuir nomenclatura padronizada.



====================
Checklist de Pull Request
=========================


[ ] Componente possui responsabilidade definida.

[ ] Template está organizado.

[ ] Não existem chamadas HTTP diretas.

[ ] Código duplicado foi evitado.

[ ] Componentes compartilhados foram considerados.



====================
Resumo
====================

A utilização correta de componentes Angular permite construir uma aplicação
modular, reutilizável e fácil de manter.

A separação entre componentes de página, domínio e compartilhados permite que
os cinco desenvolvedores trabalhem em diferentes funcionalidades sem criar
dependências desnecessárias.
