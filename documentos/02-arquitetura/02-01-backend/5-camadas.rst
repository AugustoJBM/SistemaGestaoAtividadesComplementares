=========================================
Camadas da Aplicação
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as responsabilidades, regras e limites das camadas
utilizadas na implementação do backend do Sistema de Gestão de Atividades
Complementares.

A separação em camadas tem como objetivo organizar responsabilidades,
reduzir acoplamento e facilitar a evolução do sistema.

Cada módulo deverá seguir a mesma estrutura arquitetural definida neste
documento.


====================
Visão Geral
====================

A arquitetura interna de cada módulo seguirá o seguinte fluxo:

::

    Cliente

       |

       v

    Controller

       |

       v

    Service

       |

       v

    Repository

       |

       v

    Banco de Dados


Durante esse fluxo também poderão participar componentes auxiliares:

::

    Controller

        |
        |
        +--> DTO
        |
        +--> Validator


    Service

        |
        +--> Mapper
        |
        +--> Domain Rules


    Repository

        |
        +--> Entity


====================
Princípios das Camadas
====================

As camadas deverão seguir os seguintes princípios:

* uma camada deve conhecer apenas as camadas inferiores necessárias;
* regras de negócio não devem ser espalhadas;
* componentes externos não devem acessar diretamente o domínio;
* cada classe deve possuir uma responsabilidade clara;
* alterações em uma camada não devem causar impactos desnecessários nas demais.


====================
Camada Controller
====================


Responsabilidade
----------------

A camada Controller representa o ponto de entrada da aplicação.

Sua responsabilidade é receber requisições HTTP, validar informações básicas,
encaminhar a execução para a camada de serviço e retornar uma resposta
adequada.


Responsabilidades permitidas
----------------------------

O Controller poderá:

* receber parâmetros HTTP;
* receber corpos de requisição;
* validar dados de entrada;
* converter informações para DTOs;
* retornar códigos HTTP adequados.


Responsabilidades proibidas
---------------------------

O Controller não deverá:

* implementar regras de negócio;
* acessar diretamente repositories;
* realizar operações complexas;
* manipular entidades diretamente;
* possuir lógica de cálculo.


Exemplo incorreto:

::

    Controller

        calculaCargaHoraria()


Exemplo correto:

::

    Controller

        chama AtividadeService


====================
Camada Service
====================


Responsabilidade
----------------

A camada Service representa a implementação dos casos de uso da aplicação.

Ela concentra as regras de negócio e coordena as operações necessárias para
realizar uma funcionalidade.


Responsabilidades permitidas
----------------------------

O Service poderá:

* aplicar regras de negócio;
* coordenar múltiplos repositories;
* validar estados do domínio;
* controlar transações;
* chamar outros serviços quando necessário.


Responsabilidades proibidas
---------------------------

O Service não deverá:

* conhecer detalhes HTTP;
* retornar respostas HTTP;
* depender diretamente do frontend;
* conter código específico de interface.


Exemplo:

::

    aprovarAtividade()

        verifica regras

        altera estado

        salva informação

        gera notificação


====================
Camada Repository
====================


Responsabilidade
----------------

A camada Repository representa a abstração responsável pela persistência dos
dados.


Responsabilidades permitidas
----------------------------

O Repository poderá:

* salvar entidades;
* buscar informações;
* atualizar registros;
* remover dados;
* executar consultas específicas.


Responsabilidades proibidas
---------------------------

O Repository não deverá:

* possuir regras de negócio;
* validar permissões;
* controlar fluxo da aplicação;
* realizar cálculos de domínio.


Exemplo incorreto:

::

    Repository

        aprovarCertificado()


Exemplo correto:

::

    Repository

        salvarCertificado()


====================
Camada Entity
====================


Responsabilidade
----------------

As Entities representam os objetos persistidos no banco de dados e os conceitos
principais do domínio.


Responsabilidades permitidas
----------------------------

Uma Entity poderá possuir:

* atributos;
* relacionamentos;
* regras simples relacionadas ao próprio estado.


Responsabilidades proibidas
---------------------------

Uma Entity não deverá:

* acessar banco de dados;
* controlar requisições HTTP;
* enviar notificações;
* depender de serviços externos.


====================
Camada DTO
====================


Responsabilidade
----------------

DTOs (Data Transfer Objects) representam objetos utilizados para comunicação
entre diferentes partes da aplicação.


Objetivos:

* evitar exposição direta das entidades;
* controlar dados recebidos;
* controlar dados retornados;
* facilitar evolução da API.


Tipos recomendados
------------------

Request DTO:

Representa dados recebidos pelo sistema.


Exemplo:

::

    CriarAtividadeRequest


Response DTO:

Representa dados enviados ao cliente.


Exemplo:

::

    AtividadeResponse


====================
Camada Mapper
====================


Responsabilidade
----------------

O Mapper realiza conversões entre diferentes representações de objetos.


Exemplos:

::

    Entity

       |

       v

    ResponseDTO


ou


::

    RequestDTO

       |

       v

    Entity


O Mapper não deverá conter regras de negócio.


====================
Camada Validator
====================


Responsabilidade
----------------

Responsável por validar informações antes da execução das operações.


Exemplos:

* campos obrigatórios;
* formatos;
* limites;
* consistência dos dados.


Validações relacionadas ao negócio deverão permanecer no Service.


Exemplo:

Validação de formato:

::

    Validator


Regra de aprovação:

::

    Service


====================
Camada Exception
====================


Responsabilidade
----------------

Responsável pelo tratamento padronizado dos erros da aplicação.


O sistema deverá utilizar exceções específicas para representar situações de
erro.


Exemplos:

::

    UsuarioNaoEncontradoException


    CertificadoInvalidoException


    AtividadeJaAprovadaException


Exceções não deverão ser substituídas por retornos genéricos.


====================
Dependências Permitidas
====================

As dependências deverão seguir o fluxo:

::

    Controller

        ↓

    Service

        ↓

    Repository


Componentes auxiliares poderão ser utilizados conforme necessidade.


====================
Dependências Proibidas
====================

Não são permitidas:

::

    Controller

        ↓

    Repository



    Entity

        ↓

    Service



    Repository

        ↓

    Controller


Essas dependências quebram a separação de responsabilidades.


====================
Fluxo Completo de Execução
====================

Uma operação típica deverá seguir o fluxo:

::

    1. Usuário envia requisição

    2. Controller recebe dados

    3. DTO valida entrada

    4. Service executa caso de uso

    5. Repository acessa persistência

    6. Entity representa estado atualizado

    7. Mapper transforma resposta

    8. Controller retorna resultado


====================
Checklist para Pull Request
====================

Antes de aprovar uma implementação, deverá ser verificado:

[ ] Controller possui apenas lógica de entrada e saída.

[ ] Regras de negócio estão no Service.

[ ] Repository não possui regras de negócio.

[ ] DTOs estão sendo utilizados corretamente.

[ ] Entidades não estão sendo expostas diretamente.

[ ] Código segue a organização modular.

[ ] Dependências entre camadas estão corretas.


====================
Resumo
====================

A arquitetura em camadas definida neste documento estabelece limites claros
entre responsabilidades técnicas e de negócio.

Essa separação permite que o sistema cresça mantendo organização, reduzindo
acoplamento e facilitando o trabalho colaborativo da equipe.
