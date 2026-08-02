=========================================
Entidades do Domínio
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as regras de utilização das entidades do domínio no
backend do Sistema de Gestão de Atividades Complementares.

As entidades representam os principais conceitos do negócio e possuem papel
fundamental na persistência e modelagem das informações do sistema.

A correta utilização das entidades é essencial para manter a separação entre
domínio, infraestrutura e apresentação.


====================
Conceito de Entidade
====================

Uma entidade representa um objeto do sistema que possui:

* identidade própria;
* estado;
* ciclo de vida;
* relacionamentos com outros objetos do domínio.


Exemplo de entidades do sistema:

::

    Usuario

    Estudante

    AtividadeComplementar

    Certificado

    Avaliacao

    Notificacao


Uma entidade não é apenas uma tabela do banco de dados.

Ela representa um conceito existente no domínio da aplicação.


====================
Responsabilidade das Entidades
====================

Uma entidade deverá:

* representar dados relevantes do domínio;
* manter consistência do próprio estado;
* representar relacionamentos;
* encapsular comportamentos simples relacionados ao próprio objeto.


Exemplo:


Uma atividade complementar pode possuir um comportamento:


::

    atividade.aprovar()


Esse comportamento é relacionado ao próprio estado da atividade.


====================
Responsabilidades Proibidas
====================

Uma entidade não deverá:

* acessar banco de dados;
* chamar APIs externas;
* enviar notificações;
* controlar requisições HTTP;
* conhecer DTOs;
* depender de Controllers;
* implementar casos de uso completos.


Exemplo incorreto:

::

    class Atividade {

        enviarEmail();

        salvarBanco();

        gerarRelatorio();

    }


Essas responsabilidades pertencem a outras camadas.


====================
Organização das Entidades
====================

Cada módulo deverá possuir suas próprias entidades.


Exemplo:


::

    atividades/

        entity/

            Atividade.java

            CategoriaAtividade.java



    certificados/

        entity/

            Certificado.java


Essa organização evita concentração de todas as entidades em um único pacote.


====================
Identidade da Entidade
====================

Toda entidade persistida deverá possuir um identificador único.


Exemplo:


::

    Atividade

        id

        titulo

        cargaHoraria



O identificador deverá ser utilizado para diferenciar instâncias do mesmo tipo.


====================
Relacionamentos Entre Entidades
====================

Relacionamentos deverão representar relações reais existentes no domínio.


Exemplos:


Um estudante possui atividades:


::

    Estudante

        1

        |

        *

    Atividade


Uma atividade possui certificado:


::

    Atividade

        1

        |

        *

    Certificado


Os relacionamentos deverão ser definidos considerando:

* cardinalidade;
* ciclo de vida;
* necessidade de carregamento;
* impacto de performance.


====================
Uso de JPA/Hibernate
====================

O projeto utilizará JPA através do Spring Data.


As entidades poderão utilizar recursos como:


* @Entity;
* @Id;
* @OneToMany;
* @ManyToOne;
* @OneToOne;
* @ManyToMany.


Entretanto, esses recursos deverão ser utilizados com cuidado.


====================
Boas Práticas de Mapeamento
====================


Lazy Loading
------------

Relacionamentos deverão utilizar carregamento preguiçoso quando adequado.


Objetivo:

Evitar carregamento desnecessário de grandes volumes de dados.


Exemplo:


::

    @ManyToOne(fetch = FetchType.LAZY)



====================

Controle de Exposição
=====================

Entidades nunca deverão ser retornadas diretamente pela API.


Fluxo correto:


::

    Entity

       |

       v

    Mapper

       |

       v

    ResponseDTO


Isso evita:

* exposição de dados internos;
* problemas de serialização;
* acoplamento frontend/backend.


====================
Encapsulamento
================

Os atributos das entidades deverão possuir controle adequado de acesso.


Preferencialmente:

* atributos privados;
* métodos de alteração controlados;
* estados válidos preservados.


Exemplo:


::

    atividade.finalizar()


é preferível a:


::

    atividade.status = FINALIZADA


quando houver regras associadas.


====================
Estados das Entidades
====================

Entidades que possuem ciclo de vida deverão representar seus estados
explicitamente.


Exemplo:


::

    Certificado


        PENDENTE


        APROVADO


        REJEITADO



Alterações de estado deverão respeitar regras do domínio.


Exemplo:


Uma atividade aprovada não deverá retornar para o estado inicial sem uma ação
administrativa específica.


====================
Entidades e Regras de Negócio
====================

Existe uma divisão entre regras simples e regras complexas.


Regras relacionadas exclusivamente ao próprio estado:

::

    Entity


Exemplo:


::

    certificado.isValido()



Regras envolvendo múltiplas entidades:

::

    Service


Exemplo:


::

    aprovarAtividade()


Porque envolve:

* estudante;
* certificado;
* carga horária;
* avaliação.


====================
Entidades de Auditoria
====================

Operações relevantes poderão possuir informações de auditoria.


Exemplos:

* data de criação;
* data de atualização;
* usuário responsável.


Quando necessário, entidades deverão possuir campos como:


::

    createdAt

    updatedAt



====================
Entidades e Testes
====================

Entidades deverão possuir testes quando possuírem comportamentos próprios.


Exemplos:

* mudança de estado;
* validações internas;
* regras de consistência.


====================
Exemplo Estrutural
====================


Exemplo simplificado:


::

    atividades/

        entity/

            Atividade.java


    Atividade


        id

        titulo

        cargaHoraria

        status


        aprovar()

        rejeitar()



====================
Checklist de Revisão
====================

Antes de adicionar ou modificar uma entidade:


[ ] A classe representa um conceito real do domínio.

[ ] Possui identidade própria.

[ ] Não contém lógica de infraestrutura.

[ ] Não acessa serviços externos.

[ ] Não é retornada diretamente pela API.

[ ] Relacionamentos foram avaliados.

[ ] Estados inválidos são protegidos.


====================
Resumo
====================

As entidades representam a base do domínio da aplicação.

Elas devem manter informações e comportamentos diretamente relacionados ao
próprio conceito representado, enquanto regras de negócio complexas permanecem
centralizadas nos serviços.

Essa separação mantém o backend organizado, testável e preparado para evolução.
