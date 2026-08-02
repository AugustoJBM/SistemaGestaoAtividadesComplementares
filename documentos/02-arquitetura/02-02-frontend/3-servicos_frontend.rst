=========================================
Serviços Frontend Angular
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define os padrões de criação e utilização dos Services Angular
no frontend do Sistema de Gestão de Atividades Complementares.

Services são responsáveis por concentrar lógica compartilhada, comunicação com
a API e gerenciamento de dados utilizados pela aplicação.


====================
Objetivos
====================

A utilização adequada dos Services busca:

* separar interface e lógica;
* reduzir duplicação;
* centralizar comunicação HTTP;
* facilitar testes;
* melhorar manutenção.


====================
Responsabilidade dos Services
==============================

Um Service deve ser responsável por:


* comunicação com backend;
* transformação de dados;
* compartilhamento de informações;
* gerenciamento de operações relacionadas a um domínio.


Exemplo:


::

    CertificadoService



Responsável por:


* buscar certificados;
* enviar certificados;
* remover certificados;
* atualizar informações.



====================
O que não pertence ao Service
==============================

Services não devem controlar:


* elementos visuais;
* abertura de modais;
* manipulação direta do DOM;
* comportamento específico de uma tela.


Exemplo incorreto:


::

    CertificadoService


        abreModal()



A abertura do modal pertence ao componente.


====================
Organização por Domínio
=======================

Services devem ser organizados próximos das funcionalidades que representam.


Exemplo:


::

    features/


        certificados/


            services/


                certificado.service.ts



====================
Service Global
================

Alguns serviços possuem responsabilidade geral.


Exemplos:


::

    AuthService


    NotificationService


    ConfigService



Esses serviços podem permanecer em:


::

    core/services



====================
Comunicação HTTP
=================

A comunicação com o backend deverá ocorrer através do:


::

    HttpClient



Fluxo:


::

    Component


        |

        v


    Service


        |

        v


    HttpClient


        |

        v


    Spring Boot API



====================
Exemplo de Service
==================


::

    export class CertificadoService {


        listar() {

        }


        enviar(certificado) {

        }


    }



O componente não conhece detalhes da requisição.


====================
DTOs e Interfaces
=================

Os Services deverão utilizar modelos que representem os contratos da API.


Exemplo:


::

    CertificadoDTO



Responsabilidade:

Representar dados recebidos ou enviados.


====================
Evitar Retorno de Entidades Diretas
=====================================

O frontend não deve depender diretamente da estrutura interna do backend.


Evitar:


::

    Banco


       |

       v


    Entidade JPA


       |

       v


    Angular



Preferir:


::

    Banco


       |

       v


    DTO


       |

       v


    Angular



====================
Tratamento de Respostas
========================

Services devem centralizar tratamento de respostas HTTP.


Exemplo:


Sucesso:


::

    retorna dados



Erro:


::

    encaminha erro tratado



O componente decide apenas como apresentar.


====================
Tratamento de Erros
===================

Erros da API deverão ser tratados através de uma estratégia comum.


Possibilidades:


* interceptor HTTP;
* serviço de notificações;
* tratamento específico de domínio.


====================
Observables
===========

A comunicação assíncrona deverá utilizar os mecanismos reativos do Angular.


Principalmente:


::

    Observable



Exemplo:


::

    listarAtividades()

        retorna

    Observable<Atividade[]>



====================
Assinaturas
===========

Componentes devem controlar inscrição em observables adequadamente.


Objetivos:

* evitar vazamento de memória;
* cancelar operações desnecessárias.


====================
Interceptors
===========

Operações globais HTTP deverão utilizar interceptors.


Exemplos:


* adicionar JWT;
* registrar erros;
* controlar loading global.


====================
Estado Compartilhado
====================

Quando dados precisam ser compartilhados entre várias telas, o Service poderá
gerenciar estado.


Exemplos:


* usuário autenticado;
* permissões;
* notificações.


====================
Service por Feature
==================

Cada funcionalidade deverá possuir seus próprios serviços.


Exemplo:


::

    atividades/


        services/


            atividade.service.ts



Responsabilidades:


* operações relacionadas à atividade;
* chamadas específicas;
* regras de comunicação.



====================
Testes de Services
===================

Services deverão possuir testes automatizados.


Casos:


* chamada correta da API;
* tratamento de erro;
* transformação de dados.


====================
Exemplo de Fluxo
=================


Usuário acessa certificados:


::

    CertificadoPage



       |

       v



    CertificadoService



       |

       v



    HttpClient



       |

       v



    API Spring Boot



       |

       v



    PostgreSQL



====================
Boas Práticas
=============


Services devem:


[ ] Ter responsabilidade clara.

[ ] Ser organizados por domínio.

[ ] Utilizar HttpClient corretamente.

[ ] Possuir testes.

[ ] Evitar lógica visual.

[ ] Evitar duplicação.



====================
Checklist de Pull Request
=========================


[ ] Comunicação HTTP está no Service.

[ ] Componentes não possuem chamadas diretas.

[ ] Tratamento de erro está padronizado.

[ ] Modelos estão definidos.

[ ] Testes foram adicionados.



====================
Resumo
====================

Os Services Angular funcionam como camada intermediária entre a interface e a
API backend.

Essa separação mantém os componentes simples, facilita testes e permite que os
cinco desenvolvedores implementem funcionalidades diferentes mantendo um padrão
único de desenvolvimento.
