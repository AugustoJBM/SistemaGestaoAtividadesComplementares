=========================================
Tratamento de Exceções
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define o padrão de tratamento de erros utilizado pelo backend do
Sistema de Gestão de Atividades Complementares.

O objetivo é garantir que falhas sejam tratadas de maneira consistente,
previsível e compreensível para os consumidores da API.


====================
Objetivos
====================

O tratamento padronizado de exceções busca:

* evitar respostas inconsistentes;
* facilitar manutenção;
* melhorar diagnóstico de problemas;
* separar erros técnicos de erros de negócio;
* fornecer mensagens adequadas ao frontend.


====================
Princípio Arquitetural
====================

Exceções devem representar situações excepcionais ou inválidas durante a
execução do sistema.

A aplicação não deverá utilizar valores nulos ou códigos genéricos para
representar falhas.


Exemplo incorreto:


::

    return null;



Exemplo correto:


::

    throw new UsuarioNaoEncontradoException();



====================
Categorias de Exceções
====================


As exceções serão divididas em categorias.


====================
Exceções de Domínio
====================

Representam violações das regras de negócio.


Exemplos:


::

    AtividadeJaAprovadaException


    CargaHorariaExcedidaException


    CertificadoRejeitadoException



Essas exceções normalmente são lançadas pela camada Service.


====================
Exceções de Validação
====================

Representam dados de entrada inválidos.


Exemplos:


::

    DadosInvalidosException


    CampoObrigatorioException



Normalmente originadas durante validações.


====================
Exceções de Autenticação
====================

Representam problemas relacionados à identidade do usuário.


Exemplos:


::

    TokenExpiradoException


    CredenciaisInvalidasException



====================
Exceções de Autorização
====================

Representam tentativas de acesso sem permissão.


Exemplos:


::

    UsuarioSemPermissaoException



====================
Exceções de Infraestrutura
====================

Representam falhas técnicas.


Exemplos:


::

    ErroComunicacaoBancoException


    ServicoIndisponivelException



Essas exceções não devem revelar detalhes internos ao usuário.


====================
Hierarquia de Exceções
====================

A aplicação deverá possuir uma hierarquia padronizada.


Exemplo:


::

    RuntimeException

          |

          v

    ApplicationException

          |

          +----------------+

          |                |

    DomainException   InfrastructureException



====================
Local de Lançamento
====================


A camada responsável por identificar o problema deverá lançar a exceção.


Exemplo:


Regra de negócio:


::

    AtividadeService


        verifica carga horária


        lança:


        CargaHorariaExcedidaException



Não deverá ocorrer:


::

    Controller


        verifica regra de negócio



====================
Global Exception Handler
====================

O tratamento das exceções HTTP deverá ser centralizado utilizando um mecanismo
global.


Tecnologia recomendada:


::

    @RestControllerAdvice



Responsabilidades:

* capturar exceções;
* converter para respostas HTTP;
* padronizar mensagens;
* registrar erros quando necessário.


====================
Fluxo de Tratamento
====================


::

    Service


        |

        v


    lança exceção


        |

        v


    GlobalExceptionHandler


        |

        v


    ErrorResponse


        |

        v


    Cliente HTTP



====================
Formato Padronizado de Erro
========================


Todas as respostas de erro deverão seguir um formato consistente.


Exemplo:


::

    {
        "timestamp": "2026-08-02T10:30:00",
        "status": 400,
        "error": "VALIDATION_ERROR",
        "message": "Carga horária inválida",
        "path": "/api/atividades"
    }



Campos:


timestamp
---------

Momento em que ocorreu o erro.


status
------

Código HTTP retornado.


error
-----

Código interno do erro.


message
-------

Descrição amigável.


path
----

Endpoint onde ocorreu.


====================
Mapeamento HTTP
====================


As exceções deverão possuir códigos HTTP adequados.


====================
400 Bad Request
====================

Dados enviados são inválidos.


Exemplo:


::

    DTO inválido



====================
401 Unauthorized
====================

Usuário não autenticado.


Exemplo:


::

    Token ausente



====================
403 Forbidden
====================

Usuário autenticado sem permissão.


Exemplo:


::

    Estudante tentando acessar área administrativa



====================
404 Not Found
====================

Recurso não encontrado.


Exemplo:


::

    Certificado inexistente



====================
409 Conflict
====================

Conflito com estado atual.


Exemplo:


::

    Atividade já aprovada



====================
500 Internal Server Error
========================

Erro inesperado da aplicação.


Esses erros deverão ser registrados para investigação.


====================
Exceções e Logs
====================

Nem todo erro deve possuir o mesmo nível de log.


Erros esperados:


::

    INFO ou WARN



Exemplo:

Usuário tentando acessar recurso sem permissão.


Erros inesperados:


::

    ERROR



Exemplo:

Falha desconhecida no banco.


====================
Exposição de Informações
====================

Mensagens retornadas ao cliente não devem expor detalhes internos.


Incorreto:


::

    SQLException:
    column usuario.password does not exist



Correto:


::

    Erro interno ao processar solicitação.



====================
Exemplo de Fluxo Completo
====================


Caso:

Usuário tenta enviar certificado inválido.


Fluxo:


::

    Controller

        |

        v

    CertificadoService


        |

        v


    valida certificado


        |

        v


    lança CertificadoInvalidoException


        |

        v


    GlobalExceptionHandler


        |

        v


    HTTP 400



====================
Checklist de Pull Request
====================


[ ] Exceções possuem significado de negócio.

[ ] Não existem RuntimeExceptions genéricas.

[ ] Erros são tratados globalmente.

[ ] Respostas seguem padrão definido.

[ ] Informações internas não são expostas.

[ ] Código HTTP está correto.


====================
Resumo
====================

O tratamento padronizado de exceções garante que o backend apresente
comportamento previsível diante de falhas.

A separação entre erros de negócio, validação, segurança e infraestrutura torna
a aplicação mais segura, fácil de manter e mais simples de integrar com o
frontend Angular.
