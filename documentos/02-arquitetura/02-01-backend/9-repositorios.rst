=========================================
Repositórios
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as responsabilidades e regras de utilização da camada de
Repository no backend do Sistema de Gestão de Atividades Complementares.

A camada de persistência tem como objetivo fornecer uma abstração para acesso
aos dados, isolando os demais componentes da aplicação dos detalhes de
armazenamento.


====================
Conceito de Repository
====================

Repository representa o componente responsável pela comunicação entre a
aplicação e o mecanismo de persistência.

No contexto deste projeto, os repositories serão implementados utilizando:

* Spring Data JPA;
* Hibernate;
* PostgreSQL.


A responsabilidade principal do Repository é realizar operações relacionadas
a dados.


====================
Responsabilidade Principal
====================

Um Repository deverá responder perguntas como:

* Como salvar uma entidade?
* Como buscar uma entidade?
* Como atualizar informações persistidas?
* Como remover um registro?


Exemplo:

::

    salvarCertificado()


    buscarAtividadePorId()


    listarAtividadesDoEstudante()



====================
Responsabilidades Permitidas
====================

O Repository poderá:

* persistir entidades;
* consultar dados;
* criar filtros de busca;
* implementar consultas específicas;
* controlar paginação;
* ordenar resultados.


Exemplo:


::

    List<Atividade> findByEstudanteId(Long id);



====================
Responsabilidades Proibidas
====================

O Repository não deverá:

* implementar regras de negócio;
* validar permissões;
* alterar estados do domínio;
* enviar notificações;
* chamar outros módulos;
* controlar fluxo da aplicação.


Exemplo incorreto:


::

    certificadoRepository.aprovarCertificado();



A aprovação é uma regra de negócio e pertence ao Service.


====================
Organização dos Repositories
====================

Cada módulo deverá possuir seus próprios repositories.


Exemplo:


::

    atividades/

        repository/

            AtividadeRepository.java



    certificados/

        repository/

            CertificadoRepository.java



Isso mantém a responsabilidade de persistência próxima ao domínio relacionado.


====================
Herança com Spring Data
====================

Os repositories deverão utilizar as abstrações fornecidas pelo Spring Data.


Exemplo:


::

    public interface AtividadeRepository
        extends JpaRepository<Atividade, Long> {

    }



O framework fornecerá operações básicas como:

* save;
* findById;
* findAll;
* delete.


====================
Consultas Simples
====================

Consultas simples deverão utilizar os mecanismos derivados do Spring Data.


Exemplo:


::

    findByStatus(StatusAtividade status)



Essa abordagem reduz código desnecessário.


====================
Consultas Complexas
====================

Consultas complexas poderão utilizar:

* JPQL;
* Criteria API;
* Specifications;
* Query Methods.


Entretanto, a complexidade da consulta deverá permanecer relacionada apenas à
obtenção dos dados.


Exemplo:


Permitido:


::

    buscarAtividadesPorPeriodo()



Não permitido:


::

    calcularPercentualConclusaoDoCurso()



O cálculo pertence ao Service.


====================
Repository e Entidades
====================

Repositories trabalham diretamente com Entities.


Fluxo:


::

    Service

        |

        v

    Repository

        |

        v

    Entity



DTOs nunca deverão ser utilizados diretamente pelo Repository.


====================
Repository e Regras de Negócio
====================

Existe uma diferença importante:


Busca de informação:

::

    Repository



Decisão baseada em informação:

::

    Service



Exemplo:


Encontrar certificados:


::

    CertificadoRepository



Decidir se certificado pode ser aprovado:


::

    CertificadoService



====================
Transações
====================

Operações envolvendo múltiplas alterações deverão ser controladas pela camada
Service.


Exemplo:


Aprovação de atividade:


::

    1. Atualizar atividade

    2. Registrar avaliação

    3. Criar notificação



Essa operação representa um caso de uso e não deve estar no Repository.


====================
Paginação
====================

Consultas que retornam grandes volumes de dados deverão utilizar paginação.


Exemplo:


::

    Page<Atividade> findAll(Pageable pageable)



A paginação deverá ser definida considerando:

* quantidade de dados;
* desempenho;
* necessidade da interface.


====================
Relacionamentos JPA
====================

O Repository deverá evitar carregamentos desnecessários de relacionamentos.


Deverão ser avaliados:

* Lazy Loading;
* Fetch Join;
* Entity Graphs.


Objetivo:

Evitar problemas de desempenho como consultas excessivas ao banco.


====================
Tratamento de Erros
====================

Falhas de persistência deverão ser convertidas em exceções adequadas pela
aplicação.


Exemplo:


::

    Banco indisponível


        |

        v


    DataAccessException


        |

        v


    Tratamento global da aplicação



====================
Exemplo Completo
====================


Caso:

Buscar atividades de um estudante.


Fluxo correto:


::

    Controller

        |

        v

    AtividadeService

        |

        v

    AtividadeRepository

        |

        v

    PostgreSQL



Fluxo incorreto:


::

    Controller

        |

        v

    AtividadeRepository



====================
Checklist de Revisão
====================


Antes de aprovar um Repository:


[ ] Possui apenas responsabilidade de persistência.

[ ] Não possui regras de negócio.

[ ] Não acessa Controllers ou DTOs.

[ ] Está localizado no módulo correto.

[ ] Consultas complexas possuem justificativa.

[ ] Não existem métodos com nomes representando ações de negócio.


====================
Resumo
====================

Repositories representam a camada responsável pelo acesso aos dados da
aplicação.

Eles devem permanecer simples, previsíveis e focados exclusivamente em
persistência.

Todas as decisões relacionadas ao comportamento do sistema deverão permanecer
na camada Service, garantindo uma arquitetura organizada e de fácil evolução.
