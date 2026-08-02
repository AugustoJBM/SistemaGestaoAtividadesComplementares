=========================================
Modelo de Domínio
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento descreve o modelo conceitual do domínio do Sistema de Gestão de
Atividades Complementares.

Seu objetivo é identificar os principais conceitos do negócio, suas
responsabilidades, relacionamentos e regras de domínio.

O modelo de domínio representa a visão do problema que o sistema pretende
resolver, sendo independente de detalhes de implementação, banco de dados ou
tecnologias utilizadas.

Todas as funcionalidades do sistema deverão ser construídas a partir dos
conceitos apresentados neste documento.

====================
Objetivo
====================

O Sistema de Gestão de Atividades Complementares possui como finalidade
centralizar o gerenciamento das Atividades Complementares Curriculares (ACC) e
das Atividades Curriculares de Extensão (ACEX) realizadas pelos estudantes da
UFAPE.

O domínio compreende desde o cadastro das atividades pelo estudante até sua
avaliação pelos responsáveis institucionais.

====================
Visão Geral do Domínio
====================

O domínio pode ser representado pelos seguintes conceitos principais.

::

    Usuário
        │
        ├── Estudante
        ├── Avaliador
        └── Administrador
                │
                ▼
         Atividade Complementar
                │
                ▼
           Certificado
                │
                ▼
           Solicitação
                │
                ▼
            Avaliação
                │
                ▼
           Notificação

Além desses elementos, o sistema possui entidades auxiliares responsáveis pela
organização das atividades, categorias e geração de relatórios.

====================
Atores do Domínio
====================

Estudante
---------

Representa o aluno matriculado na instituição.

É o principal usuário do sistema e responsável por cadastrar atividades,
submeter certificados e acompanhar o andamento das solicitações.

Responsabilidades:

* cadastrar atividades;
* editar atividades;
* excluir atividades;
* enviar certificados;
* consultar carga horária;
* acompanhar solicitações;
* emitir relatórios.

Avaliador
---------

Representa o usuário responsável por analisar as solicitações enviadas pelos
estudantes.

Responsabilidades:

* analisar certificados;
* aprovar atividades;
* rejeitar atividades;
* solicitar correções;
* registrar observações.

Administrador
-------------

Responsável pela administração do sistema.

Responsabilidades:

* gerenciar usuários;
* configurar categorias;
* manter parâmetros do sistema;
* administrar permissões.

====================
Conceitos do Domínio
====================

Usuário
--------

Representa qualquer pessoa autenticada no sistema.

Todo usuário possui identidade própria e permissões associadas ao seu perfil.

O comportamento específico dependerá do papel exercido dentro da aplicação.

Categoria de Atividade
----------------------

Representa uma classificação institucional utilizada para organizar as
atividades complementares.

Cada categoria define regras próprias, como limites de carga horária ou tipos
de atividades permitidas.

Atividade Complementar
----------------------

Representa uma atividade realizada pelo estudante e submetida para validação.

É o principal elemento do domínio.

Uma atividade possui:

* categoria;
* descrição;
* carga horária;
* data de realização;
* situação atual.

Certificado
-----------

Representa o documento comprobatório associado a uma atividade.

Cada certificado comprova a realização de uma única atividade complementar.

O certificado permanece vinculado à atividade durante todo seu ciclo de vida.

Solicitação
-----------

Representa o processo administrativo iniciado quando uma atividade é submetida
para avaliação.

A solicitação controla o fluxo entre estudante e avaliador.

Avaliação
---------

Representa a análise realizada sobre uma solicitação.

A avaliação registra:

* decisão;
* observações;
* responsável pela análise;
* data da avaliação.

Relatório
---------

Representa um documento consolidado contendo informações sobre as atividades do
estudante.

Os relatórios poderão ser utilizados para formalização dos processos
institucionais.

Notificação
-----------

Representa uma comunicação enviada ao usuário.

Uma notificação informa acontecimentos relevantes durante o ciclo de vida das
solicitações.

====================
Relacionamentos
====================

Os principais relacionamentos do domínio são apresentados a seguir.

Estudante

* possui diversas atividades complementares.

Atividade Complementar

* pertence a um estudante;
* pertence a uma categoria;
* pode possuir um certificado;
* gera uma solicitação.

Certificado

* pertence a uma atividade complementar.

Solicitação

* pertence a uma atividade;
* recebe avaliações;
* possui um estado.

Avaliação

* pertence a uma solicitação;
* é realizada por um avaliador.

Notificação

* pertence a um usuário.

====================
Estados da Solicitação
====================

Uma solicitação poderá assumir os seguintes estados durante seu ciclo de vida.

::

    Rascunho
         │
         ▼
    Submetida
         │
         ▼
    Em Avaliação
      │       │
      │       │
      ▼       ▼
  Aprovada  Correção Solicitada
                 │
                 ▼
           Reenviada
                 │
                 ▼
           Em Avaliação
                 │
                 ▼
             Aprovada

Ou, alternativamente:

::

    Em Avaliação
          │
          ▼
      Rejeitada

====================
Regras de Negócio
====================

As seguintes regras representam o comportamento esperado do domínio.

RN-01

Uma atividade complementar deve pertencer a exatamente um estudante.

RN-02

Toda atividade complementar deve estar vinculada a uma categoria.

RN-03

Uma atividade somente poderá ser submetida para avaliação quando possuir um
certificado válido.

RN-04

Uma solicitação somente poderá existir para uma atividade cadastrada.

RN-05

Somente avaliadores poderão aprovar ou rejeitar solicitações.

RN-06

Toda avaliação deverá registrar seu responsável e data de realização.

RN-07

Uma solicitação aprovada não poderá retornar ao estado de rascunho.

RN-08

Sempre que ocorrer alteração no estado de uma solicitação deverá ser gerada uma
notificação ao estudante.

RN-09

Os relatórios deverão refletir apenas atividades efetivamente aprovadas,
conforme as regras institucionais definidas para cada categoria.

====================
Eventos do Domínio
====================

Durante o funcionamento do sistema poderão ocorrer os seguintes eventos de
negócio.

* Atividade cadastrada.
* Atividade atualizada.
* Certificado enviado.
* Solicitação criada.
* Solicitação submetida.
* Solicitação aprovada.
* Solicitação rejeitada.
* Correção solicitada.
* Certificado reenviado.
* Relatório emitido.
* Notificação enviada.

====================
Resumo do Modelo de Domínio
====================

O Sistema de Gestão de Atividades Complementares é estruturado em torno da
atividade complementar realizada pelo estudante.

Todo o fluxo do sistema inicia-se no cadastro da atividade, passa pelo envio do
certificado, geração da solicitação, avaliação institucional e culmina na
atualização da carga horária e emissão de relatórios.

Este modelo de domínio estabelece os conceitos fundamentais do sistema e
servirá como base para definição dos módulos, requisitos funcionais, modelo de
dados e arquitetura detalhada da aplicação.
