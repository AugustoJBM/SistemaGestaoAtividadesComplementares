=========================================
Validação da Aplicação
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define as estratégias e responsabilidades relacionadas às
validações realizadas no backend do Sistema de Gestão de Atividades
Complementares.

O objetivo é estabelecer claramente onde cada tipo de validação deverá ser
implementado, evitando duplicação de regras e inconsistências no sistema.


====================
Princípio Geral
====================

A validação deverá ser distribuída conforme a responsabilidade de cada camada.

O sistema seguirá o seguinte modelo:

::

    Frontend

        |

        v

    DTO

        |

        v

    Service

        |

        v

    Entity

        |

        v

    Banco de Dados



Cada nível possui uma finalidade específica.


====================
Validação no Frontend
====================

Responsabilidade
----------------

O frontend Angular deverá realizar validações relacionadas à experiência do
usuário.

Objetivo:

* fornecer feedback rápido;
* evitar envio de dados obviamente inválidos;
* melhorar usabilidade.


Exemplos:

* campo obrigatório;
* formato de email;
* tamanho mínimo de texto.


Exemplo:

::

    Campo nome vazio


    Usuário recebe aviso imediatamente.



====================
Limitação do Frontend
====================

As validações realizadas no frontend não substituem as validações do backend.


Motivo:

O frontend pode ser manipulado pelo usuário.


Exemplos:

* chamadas HTTP manuais;
* scripts;
* clientes externos;
* alterações no código JavaScript.


Portanto:


::

    Frontend = melhoria de experiência


    Backend = garantia de segurança


====================
Validação em DTO
====================

Responsabilidade
----------------

DTOs deverão validar a estrutura dos dados recebidos pela aplicação.


São exemplos de validações adequadas:

* campos obrigatórios;
* formatos;
* tamanhos;
* padrões de entrada.


Exemplo:


::

    @NotBlank

    @Size(max = 100)

    @Email



====================
Exemplos de Validações de DTO
============================


Usuário:


::

    email obrigatório


    email possui formato válido



Certificado:


::

    nome do arquivo obrigatório


    extensão permitida



Atividade:


::

    carga horária deve possuir valor positivo



====================
Validação no Service
======================

Responsabilidade
----------------

O Service é responsável pelas validações relacionadas às regras de negócio.


Essas validações dependem do contexto do sistema.


Exemplos:

* usuário possui permissão;
* atividade pertence ao estudante;
* carga horária ainda disponível;
* certificado pode ser aprovado.


====================
Exemplos de Regras de Negócio
=============================


Regra:

"Um estudante não pode ultrapassar a carga horária máxima."


Local correto:


::

    AtividadeService



Não pertence:


::

    DTO


ou


::

    Controller



====================
Validação na Entity
====================

Responsabilidade
----------------

A Entity poderá proteger seu próprio estado.


São exemplos:

* impedir estados inválidos;
* garantir consistência interna;
* encapsular alterações.


Exemplo:


Incorreto:


::

    atividade.status = APROVADA



Correto:


::

    atividade.aprovar()



A entidade controla sua própria transição de estado.


====================
Validação no Banco de Dados
=========================

O banco deverá garantir integridade estrutural.


Exemplos:

* chave primária;
* chave estrangeira;
* campos obrigatórios;
* restrições de unicidade.


O banco não deverá substituir regras de negócio.


====================
Tabela de Responsabilidades
============================


+----------------------+--------------------------------+
| Local                | Responsabilidade               |
+======================+================================+
| Frontend             | Experiência do usuário         |
+----------------------+--------------------------------+
| DTO                  | Formato dos dados              |
+----------------------+--------------------------------+
| Service              | Regras de negócio              |
+----------------------+--------------------------------+
| Entity               | Estado consistente             |
+----------------------+--------------------------------+
| Banco                | Integridade estrutural         |
+----------------------+--------------------------------+


====================
Validações Duplicadas
====================

Algumas validações poderão existir em mais de uma camada.


Exemplo:


Campo obrigatório:


Frontend:

::

    evita erro visual


DTO:

::

    garante segurança da API



Essa duplicação é aceitável quando possui objetivos diferentes.


====================
Validações Proibidas
====================

Não deverão existir:


Regras de negócio no Controller:


::

    if(usuario.permissao)


Regras complexas no DTO:


::

    verificarCargaHorariaCompleta()



Consultas ao banco dentro de validações simples:


::

    DTO consulta Repository



====================
Validação de Permissões
======================

Validações relacionadas à autorização deverão ocorrer no backend.


Exemplos:

* estudante só altera suas próprias atividades;
* avaliador só aprova solicitações permitidas;
* administrador acessa configurações.


Essas verificações deverão ocorrer:

* Spring Security;
* Services;
* componentes específicos de autorização.


====================
Mensagens de Erro
==================

Erros de validação deverão possuir mensagens claras.


Exemplo:


::

    Carga horária inválida.



Preferir:


::

    A carga horária informada deve ser maior que zero.



Mensagens devem auxiliar o usuário e facilitar diagnóstico.


====================
Exceções de Validação
======================

Falhas de validação de negócio deverão utilizar exceções específicas.


Exemplos:


::

    CargaHorariaExcedidaException


    CertificadoInvalidoException


    UsuarioSemPermissaoException



====================
Fluxo de Validação Completo
============================


::

    Requisição

        |

        v

    DTO valida estrutura

        |

        v

    Service valida regras

        |

        v

    Entity mantém consistência

        |

        v

    Banco garante integridade



====================
Checklist de Revisão
====================


[ ] Validação está na camada correta.

[ ] Regras de negócio não estão no Controller.

[ ] DTO não possui lógica de negócio.

[ ] Frontend não é considerado fonte de segurança.

[ ] Mensagens de erro são claras.

[ ] Exceções representam problemas reais.


====================
Resumo
====================

A estratégia de validação definida neste documento garante que cada camada
possua responsabilidade adequada.

Essa organização evita duplicações desnecessárias, mantém o backend seguro e
permite que a aplicação evolua mantendo regras de negócio centralizadas.
