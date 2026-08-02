=========================================
Persistência de Dados
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as estratégias utilizadas para armazenamento,
gerenciamento e acesso aos dados no backend do Sistema de Gestão de Atividades
Complementares.

A persistência será realizada utilizando o ecossistema Spring Boot com:

* Spring Data JPA;
* Hibernate;
* PostgreSQL.


====================
Objetivos
====================

A estratégia de persistência possui como objetivos:

* garantir consistência dos dados;
* reduzir acoplamento com o banco;
* facilitar evolução do sistema;
* padronizar acesso às informações;
* permitir manutenção colaborativa entre os desenvolvedores.


====================
Tecnologias
====================


Spring Data JPA
===============

Responsável por fornecer abstração para acesso aos dados.


Hibernate
=========

Responsável pelo mapeamento objeto-relacional (ORM).


PostgreSQL
==========

Banco de dados relacional utilizado para armazenamento persistente.



====================
Modelo Objeto-Relacional
==========================

O sistema utiliza o conceito de ORM.


A relação será:


::

    Classe Java


        |


        v


    Entidade JPA


        |


        v


    Tabela PostgreSQL



Cada entidade representa uma estrutura persistida no banco.


====================
Responsabilidade das Entidades
==============================

As entidades deverão:

* representar dados persistentes;
* definir relacionamentos;
* manter identidade;
* representar estados do domínio.


As entidades não deverão:

* executar consultas;
* controlar transações complexas;
* possuir lógica de infraestrutura.


====================
Mapeamento JPA
=================

As entidades deverão utilizar anotações JPA.


Exemplo:


::

    @Entity

    @Table(name="atividade")



Identificadores:


::

    @Id

    @GeneratedValue



Relacionamentos:


::

    @ManyToOne

    @OneToMany

    @OneToOne



====================
Estratégia de Chaves
====================

As entidades deverão possuir identificadores únicos.


Padrão recomendado:


::

    Long id



O identificador deverá ser gerenciado pelo banco através de geração automática.


====================
Relacionamentos
================

Os relacionamentos deverão representar corretamente as regras do domínio.


Exemplo:


Um estudante possui atividades:


::

    Estudante

        1

        |

        *

    Atividade



====================
Cardinalidade
=============

Os relacionamentos deverão definir corretamente:


One-to-One
----------

Relacionamento um para um.


Exemplo:


::

    Usuário

        |

    Perfil



One-to-Many
-----------

Um objeto possui vários registros relacionados.


Exemplo:


::

    Estudante

        |

        *

    Certificados



Many-to-One
-----------

Vários registros pertencem a um objeto.


Exemplo:


::

    Atividade

        |

        1

    Categoria



====================
Fetch de Dados
================

Os relacionamentos deverão ser avaliados considerando desempenho.


Preferencialmente:


::

    FetchType.LAZY



Objetivo:

Evitar carregamento automático de informações não utilizadas.


====================
Evitar Excesso de Relacionamentos
==================================

Nem todo relacionamento deve ser representado diretamente.


Exemplo:


Evitar:


::

    Usuario


        possui todas as notificações


        possui todos certificados


        possui todas atividades


        possui todos relatórios



Relacionamentos excessivos podem gerar consultas pesadas.


====================
Transações
==========

Operações que modificam dados deverão possuir controle transacional.


Exemplo:


Aprovar atividade:


::

    1. Atualizar atividade

    2. Registrar avaliação

    3. Atualizar progresso



Essas ações devem ocorrer como uma única operação.


====================
Controle Transacional
====================

O controle deverá ocorrer preferencialmente na camada Service.


Exemplo:


::

    @Transactional

    public void aprovarAtividade()



O Repository não deve controlar regras transacionais complexas.


====================
Migração de Banco
==================

Alterações estruturais do banco deverão ser versionadas.


Exemplos:

* criação de tabelas;
* alteração de colunas;
* novos relacionamentos.


Cada mudança deverá possuir histórico.


====================
Versionamento do Banco
======================

O esquema do banco deverá acompanhar a evolução do código.


Exemplo:


::

    V1__criacao_usuario.sql


    V2__criacao_atividade.sql


    V3__adicionar_certificado.sql



O objetivo é permitir reprodução do ambiente.


====================
Integridade dos Dados
====================

A integridade deverá ser garantida em diferentes níveis.


Aplicação:

::

    Regras de negócio



Banco:

::

    Constraints



Exemplos:

* chave estrangeira;
* campos obrigatórios;
* unicidade.


====================
Consultas
==========

Consultas deverão ser realizadas através dos Repositories.


Permitido:


::

    AtividadeRepository.findByEstudante()



Evitar:


::

    EntityManager espalhado pelo projeto



====================
Dados Sensíveis
=================

Dados sensíveis deverão possuir tratamento adequado.


Exemplos:

* senhas;
* documentos;
* informações pessoais.


Regras:

* nunca armazenar senha original;
* evitar exposição;
* limitar acesso.


====================
Backup e Recuperação
====================

O ambiente de produção deverá possuir estratégias de:

* backup;
* restauração;
* recuperação de dados.


Essas práticas garantem continuidade do sistema.


====================
Ambiente de Desenvolvimento
================================

Cada desenvolvedor deverá conseguir executar o banco localmente.


Recomendação:


::

    Docker Compose


com:


::

    Spring Boot


+

::

    PostgreSQL



====================
Organização dos Pacotes
=======================


Exemplo:


::

    modulo/

        entity/

        repository/

        service/

        controller/



A persistência deverá permanecer próxima ao módulo responsável.


====================
Checklist de Revisão
====================


[ ] Entidade representa corretamente o domínio.

[ ] Relacionamentos foram avaliados.

[ ] Repository contém apenas acesso a dados.

[ ] Alterações do banco são versionadas.

[ ] Não existem SQLs espalhados.

[ ] Transações estão na camada correta.

[ ] Dados sensíveis possuem proteção.


====================
Resumo
====================

A persistência do sistema será baseada em JPA/Hibernate com PostgreSQL,
mantendo o banco de dados isolado através dos Repositories.

A organização definida permite que os cinco desenvolvedores trabalhem em
diferentes módulos sem gerar conflitos estruturais ou inconsistências no
modelo de dados.
