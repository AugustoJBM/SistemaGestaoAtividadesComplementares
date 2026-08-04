=========================================
Objetos de Transferência de Dados (DTO)
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as regras de utilização dos Data Transfer Objects (DTOs)
na arquitetura do backend do Sistema de Gestão de Atividades Complementares.

Os DTOs representam os contratos de comunicação entre a aplicação e seus
consumidores externos, principalmente o frontend Angular.

A utilização adequada de DTOs garante isolamento entre o modelo interno da
aplicação e as interfaces externas.


====================
Conceito de DTO
====================

DTO (Data Transfer Object) é um objeto utilizado exclusivamente para transportar
dados entre diferentes camadas ou sistemas.

No contexto deste projeto, os DTOs serão utilizados principalmente para:

* receber dados enviados pelo frontend;
* retornar informações para o frontend;
* controlar quais informações são expostas;
* validar dados de entrada.


====================
Motivação para Utilização
====================

As entidades do sistema representam o domínio interno da aplicação.

Entretanto, elas não devem ser expostas diretamente para clientes externos.


Exemplo incorreto:


::

    Controller

        retorna

    AtividadeEntity



Essa abordagem cria acoplamento direto entre:

::

    Banco de Dados

            |

            |

    API

            |

            |

    Frontend



Qualquer alteração interna na entidade poderia quebrar consumidores externos.


====================
Fluxo com DTO
====================

O fluxo correto será:


::

    Frontend

        |

        v

    RequestDTO

        |

        v

    Mapper

        |

        v

    Entity

        |

        v

    Repository



Retorno:


::

    Repository

        |

        v

    Entity

        |

        v

    Mapper

        |

        v

    ResponseDTO

        |

        v

    Frontend


====================
Tipos de DTO
====================


Request DTO
===========

Representa dados recebidos pela aplicação.


Exemplo:


::

    CriarAtividadeRequest


Responsável por:

* receber informações;
* validar formato;
* representar intenção do usuário.


Exemplo:


::

    {
        "titulo": "Monitoria",
        "cargaHoraria": 40,
        "categoria": "ENSINO"
    }



====================

Response DTO
============

Representa dados enviados pela aplicação.


Exemplo:


::

    AtividadeResponse


Responsável por:

* controlar informações retornadas;
* evitar exposição de dados internos;
* definir contrato da API.


Exemplo:


::

    {
        "id": 10,
        "titulo": "Monitoria",
        "status": "APROVADA"
    }



====================
Organização dos DTOs
====================

Cada módulo deverá possuir seus próprios DTOs.


Exemplo:


::

    atividades/

        dto/

            request/

                CriarAtividadeRequest.java

                AtualizarAtividadeRequest.java


            response/

                AtividadeResponse.java



Esse padrão deverá ser seguido por todos os módulos.


====================
Nomenclatura
====================

Os DTOs deverão possuir nomes claros e descritivos.


Padrões recomendados:


Criação:

::

    CriarUsuarioRequest


Atualização:

::

    AtualizarUsuarioRequest


Resposta:

::

    UsuarioResponse


Consulta:

::

    UsuarioResumoResponse



Nomes genéricos como:


::

    UsuarioDTO

    DadosUsuario


deverão ser evitados quando não representarem claramente sua finalidade.


====================
DTOs e Casos de Uso
====================

Cada operação poderá possuir DTOs específicos.


Exemplo:


Não recomendado:


::

    UsuarioDTO


utilizado para:

* criar usuário;
* atualizar usuário;
* listar usuário;
* autenticar usuário.


Recomendado:


::

    CriarUsuarioRequest


    AtualizarUsuarioRequest


    UsuarioResponse


Cada DTO representa uma intenção específica.


====================
Validação em DTOs
====================

Validações relacionadas à entrada de dados deverão ocorrer nos Request DTOs.


Exemplos:


Campo obrigatório:


::

    @NotBlank


Tamanho:


::

    @Size


Formato:


::

    @Email



Essas validações garantem que dados inválidos não avancem no fluxo.


====================
Limite das Validações
====================

Nem toda validação pertence ao DTO.


Validação estrutural:


::

    DTO


Exemplo:


"Campo nome não pode ser vazio."


Validação de negócio:


::

    Service


Exemplo:


"Estudante não pode ultrapassar carga horária máxima."



====================
DTOs e Segurança
====================

DTOs também possuem papel de segurança.


Nunca deverão ser retornados:

* senhas;
* tokens internos;
* informações administrativas não autorizadas;
* dados sensíveis sem necessidade.


Exemplo incorreto:


::

    UsuarioResponse


        senhaHash



Exemplo correto:


::

    UsuarioResponse


        nome

        email

        perfil



====================
DTOs e Evolução da API
====================

A utilização de DTOs permite evolução independente entre backend e frontend.


Exemplo:


Versão inicial:


::

    AtividadeResponse

        titulo


Nova versão:


::

    AtividadeResponse

        titulo

        categoria

        cargaHoraria



Alterações internas não precisam refletir diretamente nas entidades.


====================
Mapeamento Entre DTO e Entity
==============================

A conversão entre DTOs e entidades deverá ser realizada por componentes
específicos chamados Mappers.


Fluxo:


::

    RequestDTO

        |

        v

    Mapper

        |

        v

    Entity



E:


::

    Entity

        |

        v

    Mapper

        |

        v

    ResponseDTO



O Mapper não deverá conter regras de negócio.


====================
DTOs e Testes
=================

DTOs deverão possuir testes quando apresentarem:

* validações;
* transformações;
* regras específicas de exposição.


====================
Checklist de Pull Request
==========================


[ ] Entity não está sendo retornada diretamente.

[ ] Existe DTO específico para a operação.

[ ] Dados sensíveis não estão expostos.

[ ] Validações estruturais estão no DTO.

[ ] Regras de negócio permanecem no Service.

[ ] Nomenclatura segue o padrão definido.


====================
Resumo
====================

DTOs representam a fronteira entre o backend e seus consumidores externos.

A utilização correta desses objetos mantém a independência entre domínio,
persistência e interface, permitindo evolução segura da aplicação e reduzindo
acoplamento entre frontend e backend.
