=========================================
Padrões de Interface
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define os padrões visuais e de experiência de usuário utilizados
na aplicação frontend do Sistema de Gestão de Atividades Complementares.

O objetivo é garantir uma interface consistente, responsiva e fácil de utilizar
independentemente do desenvolvedor responsável pela implementação.


====================
Objetivos
====================

Os padrões de interface buscam:

* manter consistência visual;
* melhorar experiência do usuário;
* reduzir duplicação de estilos;
* facilitar manutenção;
* permitir evolução da aplicação.


====================
Tecnologia de Estilização
============================

A aplicação utilizará:

::

    Tailwind CSS



O Tailwind será responsável pela criação dos estilos através de classes
utilitárias.


Benefícios:


* desenvolvimento rápido;
* padronização;
* redução de CSS repetido;
* responsividade facilitada.


====================
Organização Visual
===================

A interface deverá seguir uma estrutura hierárquica:


::

    Layout


       |


       +-- Página


             |


             +-- Seções


                    |


                    +-- Componentes



====================
Layout Global
================

O sistema deverá possuir uma estrutura visual comum.


Exemplo:


::

    Header


    Sidebar


    Conteúdo principal


    Footer



Componentes globais deverão permanecer em:


::

    shared/components


ou


::

    layout/



====================
Responsividade
==============

Todas as telas deverão funcionar em diferentes tamanhos de dispositivo.


Prioridade:


1. Desktop

2. Tablet

3. Mobile



A interface deve utilizar os recursos responsivos do Tailwind.


Exemplo:


::

    grid-cols-1

    md:grid-cols-2

    lg:grid-cols-4



====================
Componentes Visuais
====================

Elementos utilizados frequentemente deverão ser componentes reutilizáveis.


Exemplos:


Botões
======

::

    ButtonComponent



Campos
======

::

    InputComponent



Mensagens
=========

::

    AlertComponent



Carregamento
============

::

    LoadingComponent



====================
Padrão de Botões
================

Botões devem possuir comportamento consistente.


Tipos:


Primário
--------

Ação principal.


Exemplo:

::

    Salvar



Secundário
----------

Ações alternativas.


Exemplo:

::

    Cancelar



Perigo
------

Ações destrutivas.


Exemplo:

::

    Excluir



====================
Formulários
===========

Formulários devem seguir padrão único.


Devem possuir:


* label;
* campo;
* mensagem de erro;
* estado de carregamento;
* feedback ao usuário.


Exemplo:


::

    Nome


    [____________]


    Campo obrigatório



====================
Validação Visual
=================

Erros devem ser apresentados próximos ao campo relacionado.


Exemplo:


::

    Email


    [usuario]


    Email inválido



Evitar:


Mensagem genérica distante do problema.


====================
Tabelas
========

Tabelas deverão ser utilizadas para visualização de dados estruturados.


Devem possuir:


* cabeçalho;
* paginação quando necessário;
* estados vazios;
* carregamento.


Exemplo:


::

    Lista de Certificados


    Nome | Status | Data



====================
Estados da Interface
===================

Toda tela deverá considerar diferentes estados.


Carregando
----------

Exibir indicador visual.


Exemplo:


::

    Loading



Vazio
-----

Quando não existem dados.


Exemplo:


::

    Nenhum certificado encontrado



Erro
----

Quando ocorre falha.


Exemplo:


::

    Não foi possível carregar os dados



Sucesso
-------

Confirmar operações concluídas.


====================
Modais
=======

Modais deverão ser utilizados para ações que não necessitam de uma nova página.


Exemplos:


* confirmação de exclusão;
* visualização rápida;
* edição simples.


====================
Notificações
============

Mensagens ao usuário deverão seguir um padrão.


Tipos:


Sucesso:


::

    Certificado enviado com sucesso.



Erro:


::

    Não foi possível enviar certificado.



Aviso:


::

    Documento aguardando aprovação.



====================
Acessibilidade
==============

A interface deverá considerar acessibilidade.


Práticas:


* utilizar textos claros;
* manter contraste adequado;
* utilizar labels em formulários;
* permitir navegação por teclado.


====================
Nomenclatura Visual
===================

Classes e componentes devem possuir nomes claros.


Exemplo:


Correto:


::

    certificado-card



Evitar:


::

    box1



====================
Evitar Duplicação
=================

Antes de criar um novo elemento visual, verificar componentes existentes.


Evitar:


::

    Novo botão em cada tela



Preferir:


::

    ButtonComponent compartilhado



====================
Design System
============

O projeto poderá evoluir para um conjunto de componentes padronizados.


Exemplo:


::

    Design System


        |

        +-- Botões

        +-- Inputs

        +-- Cards

        +-- Modais

        +-- Alertas



====================
Responsabilidade dos Desenvolvedores
=====================================

Todos os desenvolvedores devem:


* utilizar componentes existentes;
* seguir padrões definidos;
* evitar estilos isolados;
* manter responsividade;
* considerar experiência do usuário.


====================
Checklist de Pull Request
=========================


[ ] Interface segue padrão visual.

[ ] Componentes reutilizados quando possível.

[ ] Tela é responsiva.

[ ] Estados de carregamento e erro existem.

[ ] Formulários possuem validação visual.

[ ] Não existem estilos duplicados.



====================
Resumo
====================

A padronização da interface garante que o frontend Angular mantenha uma
experiência consistente durante toda a evolução do sistema.

A utilização de Tailwind CSS, componentes reutilizáveis e regras visuais
definidas permite que os cinco desenvolvedores criem novas funcionalidades sem
comprometer a identidade da aplicação.
