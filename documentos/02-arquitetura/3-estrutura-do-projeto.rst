=========================================
Estrutura do Projeto
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento define a organização física do repositório do Sistema de Gestão
de Atividades Complementares.

O objetivo é padronizar a disposição dos arquivos e diretórios do projeto,
facilitando a navegação, manutenção e evolução da aplicação.

Toda a equipe deverá seguir esta estrutura durante o desenvolvimento.

====================
Objetivos
====================

A organização do projeto busca atingir os seguintes objetivos.

* Facilitar a localização dos arquivos.
* Reduzir ambiguidades na estrutura do código.
* Favorecer o desenvolvimento paralelo.
* Padronizar a organização entre frontend e backend.
* Facilitar a manutenção do sistema.
* Preparar o projeto para crescimento futuro.

====================
Organização Geral
====================

O repositório será organizado da seguinte maneira.

::

    gestaoAtividadeComplementar/

    ├── backend/
    ├── frontend/
    ├── documentos/
    ├── scripts/
    ├── .github/
    ├── docker/
    ├── LICENSE
    ├── README.md
    └── .gitignore

Cada diretório possui responsabilidades específicas descritas nas seções
seguintes.

====================
Diretório Backend
====================

Contém toda a implementação da API REST desenvolvida utilizando Java e Spring
Boot.

Responsabilidades.

* regras de negócio;
* autenticação;
* persistência;
* API REST;
* integração com banco de dados.

====================
Diretório Frontend
====================

Contém toda a aplicação Angular.

Responsabilidades.

* interface do usuário;
* navegação;
* autenticação;
* comunicação com a API;
* gerenciamento do estado da interface.

====================
Diretório Documentos
====================

Contém toda a documentação oficial do projeto.

Nenhuma documentação externa deverá substituir os documentos presentes neste
diretório.

====================
Diretório Scripts
====================

Contém scripts auxiliares utilizados durante o desenvolvimento.

Exemplos.

* inicialização do ambiente;
* geração de dados;
* limpeza;
* automações.

====================
Diretório Docker
====================

Contém arquivos necessários para execução da aplicação utilizando Docker.

Exemplos.

* Dockerfile
* docker-compose.yml

====================
Diretório GitHub
====================

Contém configurações relacionadas ao GitHub.

Exemplos.

* Workflows.
* Templates de Issues.
* Templates de Pull Requests.
* Configurações de automações.

====================
Organização do Backend
====================

O backend será organizado seguindo uma arquitetura modular.

Cada módulo possuirá sua própria estrutura interna.

Exemplo.

::

    backend/

        autenticacao/

        usuarios/

        atividades/

        certificados/

        avaliacoes/

        relatorios/

        notificacoes/

        administracao/

A organização detalhada será apresentada no documento
``backend.rst``.

====================
Organização do Frontend
====================

O frontend também será organizado por funcionalidades.

Cada módulo da aplicação possuirá seus próprios componentes, serviços,
rotas e modelos.

Exemplo.

::

    frontend/

        autenticacao/

        atividades/

        certificados/

        avaliacoes/

        relatorios/

A organização detalhada encontra-se no documento ``frontend.rst``.

====================
Convenções Gerais
====================

Durante todo o projeto deverão ser observadas as seguintes convenções.

* nomes de diretórios em letras minúsculas;
* utilização de nomes descritivos;
* separação por domínio de negócio;
* evitar diretórios genéricos;
* evitar concentração excessiva de arquivos em um único diretório.

====================
Responsabilidades
====================

A organização do projeto deverá refletir a arquitetura definida para o sistema.

Novos diretórios somente deverão ser adicionados quando representarem um novo
domínio ou uma nova responsabilidade claramente identificada.

====================
Resumo
====================

A estrutura definida neste documento estabelece uma organização consistente
para todo o repositório.

Essa organização servirá como base para os documentos específicos do backend,
frontend e demais componentes da arquitetura.
