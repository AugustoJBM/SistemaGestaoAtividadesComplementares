=========================================
Serviços da Aplicação (Services)
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as responsabilidades e regras de utilização da camada
Service no backend do Sistema de Gestão de Atividades Complementares.

A camada Service representa o núcleo responsável pela execução dos casos de uso
da aplicação.

É nesta camada que as regras de negócio são aplicadas, os fluxos são
coordenados e as operações entre diferentes componentes são organizadas.


====================
Conceito de Service
====================

Um Service representa uma unidade responsável por executar uma operação
relevante do sistema.

Ele não representa apenas uma classe auxiliar, mas uma implementação de um
caso de uso do domínio.


Exemplos:

::

    cadastrarAtividade()


    enviarCertificado()


    aprovarSolicitacao()


    gerarRelatorio()



Cada operação representa uma ação que possui significado para o negócio.


====================
Responsabilidade Principal
====================

O Service deverá:

* implementar regras de negócio;
* coordenar operações;
* controlar o fluxo de execução;
* validar condições do domínio;
* comunicar módulos quando necessário.


O Service representa o ponto central onde uma funcionalidade é executada.


====================
Exemplo de Caso de Uso
====================

Caso:

Um estudante envia um certificado.


Fluxo:


::

    CertificadoService


        verifica usuário


        valida informações


        registra certificado


        atualiza atividade relacionada


        cria notificação



Esse fluxo representa uma operação completa do sistema.


====================
Responsabilidades Permitidas
====================

O Service poderá:

* acessar repositories;
* chamar outros services;
* executar regras de negócio;
* controlar transações;
* transformar dados quando necessário;
* disparar eventos internos.


====================
Responsabilidades Proibidas
====================

O Service não deverá:

* conhecer HTTP;
* retornar ResponseEntity;
* manipular diretamente requisições;
* acessar banco sem Repository;
* depender do frontend.


Exemplo incorreto:


::

    public ResponseEntity aprovarAtividade()



O retorno HTTP pertence ao Controller.


====================
Organização dos Services
====================

Cada módulo deverá possuir seus próprios services.


Exemplo:


::

    atividades/

        service/

            AtividadeService.java



    certificados/

        service/

            CertificadoService.java



Essa organização mantém as responsabilidades próximas ao domínio.


====================
Service e Casos de Uso
====================

Cada método público de um Service deverá representar uma ação significativa para
o negócio.


Exemplo:


Adequado:


::

    aprovarAtividade()


    rejeitarCertificado()


    calcularProgressoAluno()



Evitar:


::

    atualizarBanco()


    executarProcesso()



Métodos devem expressar intenção.


====================
Divisão de Responsabilidades
====================

Um Service não deverá se tornar uma classe gigante contendo todo o sistema.


Exemplo incorreto:


::

    SistemaService


        cadastrarUsuario()

        enviarCertificado()

        gerarRelatorio()

        aprovarAtividade()

        enviarEmail()



Cada domínio deverá possuir seus próprios serviços.


====================
Comunicação Entre Services
====================

Quando uma funcionalidade depender de outro módulo, a comunicação deverá
ocorrer através de Services.


Exemplo:


Aprovação de atividade gera notificação.


Fluxo correto:


::

    AvaliacaoService


            |

            v


    NotificacaoService



Fluxo incorreto:


::

    AvaliacaoService


            |

            v


    NotificacaoRepository



O módulo não deve conhecer detalhes internos de outro domínio.


====================
Regras de Negócio
====================

Regras relacionadas ao comportamento do sistema deverão permanecer nos Services.


Exemplos:


Validação simples:


::

    DTO


"Campo obrigatório."


Regra de negócio:


::

    Service


"Estudante não pode ultrapassar carga horária exigida."


====================
Controle Transacional
====================

Operações que alteram múltiplas informações deverão possuir controle
transacional.


Exemplo:


Aprovação de atividade:


::

    1. Atualizar atividade

    2. Registrar avaliação

    3. Atualizar progresso

    4. Criar notificação



Todas essas ações representam uma única operação de negócio.


====================
Uso de @Transactional
====================

Transações deverão ser utilizadas na camada Service.


Exemplo:


::

    @Transactional
    public void aprovarAtividade()



O objetivo é garantir consistência dos dados.


====================
Services e Entidades
====================

Services deverão utilizar entidades para executar regras de domínio.


Exemplo:


::

    atividade.aprovar()



é preferível a:


::

    atividade.setStatus(APROVADA)



quando existir comportamento associado.


====================
Services e DTOs
====================

Services não deverão depender diretamente de DTOs externos.


Fluxo recomendado:


::

    Controller

        |

        v

    RequestDTO

        |

        v

    Mapper

        |

        v

    Service



O Service deverá trabalhar com objetos do domínio.


====================
Services e Validações
====================

As validações devem ser divididas.


Validação estrutural:


::

    DTO


Exemplo:

"Nome possui pelo menos 3 caracteres."


Validação de negócio:


::

    Service


Exemplo:

"Atividade pertence ao estudante autenticado."


====================
Services e Exceções
====================

Quando uma regra de negócio não puder ser cumprida, o Service deverá lançar uma
exceção específica.


Exemplo:


::

    AtividadeJaAprovadaException



Evitar:


::

    Exception("Erro")



Exceções devem representar situações reais do domínio.


====================
Exemplo Estrutural
====================


::

    atividades/

        service/

            AtividadeService.java



Classe:


::

    AtividadeService


        cadastrar()


        atualizar()


        remover()


        aprovar()



Cada método representa um comportamento do domínio.


====================
Testabilidade
====================

Services deverão ser desenvolvidos considerando testes automatizados.


Dependências externas deverão ser isoladas utilizando:

* mocks;
* interfaces;
* injeção de dependência.


Exemplo:


Testar:


::

    Aprovação de atividade



Sem depender:

* banco real;
* frontend;
* serviços externos.


====================
Checklist de Pull Request
====================


[ ] Service representa um caso de uso real.

[ ] Regras de negócio estão centralizadas.

[ ] Não existe lógica HTTP.

[ ] Não acessa banco diretamente.

[ ] Comunicação entre módulos está correta.

[ ] Exceções representam situações reais.

[ ] Método possui nome baseado em intenção.


====================
Resumo
====================

A camada Service representa o coração da aplicação.

Ela concentra decisões, regras de negócio e coordenação das operações,
mantendo Controllers simples e Repositories focados exclusivamente em
persistência.

Uma boa organização dos Services é fundamental para garantir a evolução
saudável do Sistema de Gestão de Atividades Complementares.
