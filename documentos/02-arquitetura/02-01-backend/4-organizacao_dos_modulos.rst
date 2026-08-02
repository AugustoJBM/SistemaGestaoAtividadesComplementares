=========================================
Organização dos Módulos
=========================================

.. contents::
   :local:
   :depth: 2


====================
Introdução
====================

Este documento define a organização dos módulos que compõem o backend do Sistema
de Gestão de Atividades Complementares.

A estrutura modular adotada tem como objetivo representar diretamente os
domínios existentes no sistema, permitindo maior organização, isolamento de
responsabilidades e facilidade de evolução.

Cada módulo representa uma área específica do negócio e deverá possuir os
componentes necessários para implementar suas próprias funcionalidades.


====================
Princípio de Organização
====================

O backend deverá ser organizado utilizando uma abordagem baseada em domínio
(*Domain-oriented organization*).

Isso significa que a estrutura do código deverá refletir os conceitos e
responsabilidades existentes no negócio, evitando uma separação puramente
técnica.

A organização deverá seguir o seguinte princípio:

::

    Correto:

    atividades/
        controller/
        service/
        repository/
        entity/
        dto/


    Incorreto:

    controller/
        AtividadeController
        CertificadoController
        UsuarioController

    service/
        AtividadeService
        CertificadoService
        UsuarioService


A primeira abordagem mantém os elementos relacionados próximos entre si,
enquanto a segunda espalha uma mesma funcionalidade por diversos locais do
projeto.


====================
Objetivos da Modularização
====================

A divisão modular possui os seguintes objetivos:

* reduzir o acoplamento entre funcionalidades;
* aumentar a coesão do código;
* permitir desenvolvimento paralelo entre integrantes da equipe;
* facilitar localização de componentes;
* facilitar testes isolados;
* permitir evolução independente dos domínios;
* preparar o sistema para possíveis mudanças arquiteturais futuras.


====================
Estrutura Geral dos Módulos
====================

O backend será organizado pelos seguintes módulos principais:

::

    br.ufape.sgac

        ├── autenticacao
        ├── usuarios
        ├── atividades
        ├── certificados
        ├── avaliacoes
        ├── relatorios
        ├── notificacoes
        └── administracao


Cada módulo representa uma responsabilidade de negócio específica.


====================
Módulo de Autenticação
====================

Responsabilidade
----------------

Responsável pelo controle de identidade e acesso dos usuários ao sistema.

Responsabilidades:

* login;
* geração e validação de tokens;
* controle de permissões;
* integração com Spring Security;
* gerenciamento da sessão lógica do usuário.


Não pertence a este módulo:

* cadastro completo de informações acadêmicas;
* gerenciamento de atividades;
* geração de relatórios.


====================
Módulo de Usuários
====================

Responsabilidade
----------------

Responsável pelo gerenciamento das informações dos usuários do sistema.

Responsabilidades:

* dados pessoais;
* perfis de usuário;
* informações institucionais;
* atualização cadastral.


Não pertence a este módulo:

* autenticação;
* validação de certificados;
* regras de aprovação de atividades.


====================
Módulo de Atividades Complementares
====================

Responsabilidade
----------------

Responsável pelo gerenciamento das atividades complementares realizadas pelos
estudantes.

Responsabilidades:

* cadastro de atividades;
* classificação por categoria;
* controle de carga horária;
* acompanhamento de progresso;
* regras relacionadas às atividades.


Este módulo representa o principal domínio do sistema.


====================
Módulo de Certificados
====================

Responsabilidade
----------------

Responsável pelo gerenciamento dos documentos comprobatórios enviados pelos
estudantes.

Responsabilidades:

* upload de certificados;
* armazenamento de referências dos arquivos;
* consulta de documentos;
* substituição de certificados;
* validação estrutural dos arquivos.


Não pertence a este módulo:

* aprovação acadêmica;
* cálculo de carga horária.


====================
Módulo de Avaliações
====================

Responsabilidade
----------------

Responsável pelo fluxo institucional de análise das atividades submetidas.

Responsabilidades:

* submissão de atividades;
* aprovação;
* reprovação;
* solicitação de ajustes;
* histórico de avaliações.


====================
Módulo de Relatórios
====================

Responsabilidade
----------------

Responsável pela geração de documentos e informações consolidadas.

Responsabilidades:

* relatórios de progresso;
* documentos institucionais;
* consolidação da carga horária;
* exportação de informações.


====================
Módulo de Notificações
====================

Responsabilidade
----------------

Responsável pela comunicação de eventos importantes do sistema.

Responsabilidades:

* criação de notificações;
* armazenamento de histórico;
* comunicação de mudanças de estado.


Exemplos de eventos:

* certificado aprovado;
* atividade rejeitada;
* solicitação corrigida.


====================
Módulo de Administração
====================

Responsabilidade
----------------

Responsável pelas funcionalidades administrativas do sistema.

Responsabilidades:

* gerenciamento de categorias;
* configurações institucionais;
* parâmetros do sistema;
* gerenciamento administrativo.


====================
Estrutura Interna dos Módulos
====================

Todos os módulos deverão seguir uma organização interna padronizada.

Exemplo:

::

    atividades/

        controller/

        service/

        repository/

        entity/

        dto/

        mapper/

        validator/

        exception/


A responsabilidade de cada componente será detalhada no documento
``camadas.rst``.


====================
Comunicação Entre Módulos
====================

A comunicação entre módulos deverá ocorrer de forma controlada.

Um módulo não deverá acessar diretamente componentes internos de outro módulo.

Exemplo proibido:

::

    CertificadoService

        chama diretamente

    AtividadeRepository


O correto é utilizar interfaces ou serviços públicos disponibilizados pelo
módulo responsável.


Exemplo:

::

    CertificadoService

        solicita informação

                ↓

        AtividadeService


====================
Dependências Entre Módulos
====================

As dependências deverão seguir uma direção definida.

Módulos de negócio não deverão depender de detalhes de infraestrutura.

Exemplo:

::

    Controller

        ↓

    Service

        ↓

    Repository


Essa regra evita que alterações internas de um módulo afetem todo o sistema.


====================
Critérios Para Criar Um Novo Módulo
====================

Um novo módulo somente deverá ser criado quando representar uma nova
responsabilidade de negócio.

Não deverão ser criados módulos apenas para agrupar classes tecnicamente
semelhantes.


Exemplo incorreto:

::

    validacoes/

    helpers/

    utilitarios/


Exemplo correto:

::

    pagamentos/

    auditoria/

    documentos/


====================
Impacto no Desenvolvimento da Equipe
====================

A organização modular permite que diferentes desenvolvedores trabalhem em
diferentes áreas do sistema simultaneamente.

Exemplo:

::

    Desenvolvedor 1

        Autenticação


    Desenvolvedor 2

        Atividades


    Desenvolvedor 3

        Certificados


    Desenvolvedor 4

        Avaliações


    Desenvolvedor 5

        Relatórios e Notificações


Cada integrante consegue evoluir seu domínio reduzindo conflitos de código e
facilitando revisões.


====================
Evolução Futura
====================

A separação modular permite que determinados módulos possam futuramente ser
extraídos para serviços independentes caso exista necessidade.

Entretanto, a arquitetura atual não introduz essa complexidade inicialmente,
mantendo o equilíbrio entre organização e simplicidade.


====================
Resumo
====================

A organização modular definida neste documento estabelece uma estrutura baseada
nos domínios do sistema.

Essa abordagem garante maior clareza arquitetural, facilita o desenvolvimento
paralelo da equipe e cria uma base sólida para manutenção e evolução futura do
Sistema de Gestão de Atividades Complementares.
