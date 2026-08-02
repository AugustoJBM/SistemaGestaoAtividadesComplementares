=========================================
Decisões de Arquitetura
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento registra as principais decisões arquiteturais adotadas durante o
planejamento do Sistema de Gestão de Atividades Complementares.

Cada decisão foi tomada considerando os objetivos do projeto, o contexto
acadêmico, a facilidade de manutenção, a escalabilidade da aplicação e a
produtividade da equipe de desenvolvimento.

O registro dessas decisões tem como objetivo preservar o conhecimento
arquitetural do projeto, evitando perda de contexto ao longo do seu ciclo de
vida.

====================
Objetivos
====================

* Registrar as principais decisões arquiteturais.
* Justificar a adoção das tecnologias utilizadas.
* Facilitar a compreensão da arquitetura pelos desenvolvedores.
* Reduzir retrabalho decorrente da rediscussão de decisões já tomadas.
* Servir como referência para futuras evoluções do sistema.

====================
DA-01 — Arquitetura Monolítica Modular
====================

Contexto
---------

O sistema será desenvolvido por uma equipe de cinco desenvolvedores e possui um
domínio de negócio bem definido, com módulos que compartilham informações entre
si.

Decisão
--------

Será adotada uma arquitetura de **Monólito Modular**.

Justificativa
-------------

Essa abordagem oferece uma separação clara entre os domínios do sistema sem
introduzir a complexidade operacional de uma arquitetura baseada em
microserviços.

Cada módulo poderá evoluir de forma relativamente independente, mantendo uma
base de código única, implantação simplificada e menor custo de manutenção.

Consequências
-------------

* Organização por domínio.
* Baixo acoplamento entre módulos.
* Implantação simplificada.
* Facilidade de desenvolvimento para equipes pequenas.
* Possibilidade de evolução futura para microserviços, caso necessário.

====================
DA-02 — Backend em Java com Spring Boot
====================

Contexto
---------

O backend será responsável pela implementação das regras de negócio, segurança,
persistência e exposição da API REST.

Decisão
--------

Será utilizado Java com Spring Boot.

Justificativa
-------------

O Spring Boot fornece um ecossistema maduro para aplicações corporativas,
oferecendo suporte nativo a injeção de dependências, validação, persistência,
segurança, testes e integração com diversos serviços.

Além disso, a equipe possui familiaridade com a tecnologia e ela é amplamente
utilizada na indústria.

Consequências
-------------

* Desenvolvimento acelerado.
* Grande disponibilidade de documentação.
* Facilidade de manutenção.
* Ecossistema consolidado.

====================
DA-03 — Frontend em Angular
====================

Contexto
---------

A aplicação deverá possuir uma interface moderna, organizada e de fácil
manutenção.

Decisão
--------

Será utilizado Angular com TypeScript.

Justificativa
-------------

Angular possui arquitetura baseada em componentes e módulos, tipagem estática,
injeção de dependências e ferramentas integradas que favorecem aplicações de
grande porte.

Sua estrutura incentiva organização e padronização do código.

Consequências
-------------

* Componentização.
* Facilidade de reutilização.
* Organização por funcionalidades.
* Melhor escalabilidade.

====================
DA-04 — Utilização de Tailwind CSS
====================

Contexto
---------

Será necessário desenvolver interfaces modernas com rapidez e consistência.

Decisão
--------

Será utilizado Tailwind CSS.

Justificativa
-------------

Tailwind fornece uma abordagem baseada em classes utilitárias, reduzindo a
necessidade de folhas de estilo extensas e favorecendo a padronização visual.

Consequências
-------------

* Desenvolvimento mais rápido.
* Interface consistente.
* Redução de CSS personalizado.

====================
DA-05 — API REST
====================

Contexto
---------

O frontend e o backend deverão comunicar-se através de uma interface bem
definida.

Decisão
--------

A comunicação será realizada por meio de uma API REST utilizando HTTP e JSON.

Justificativa
-------------

REST é amplamente adotado na indústria, possui baixo acoplamento e integra-se
facilmente com aplicações web e móveis.

Consequências
-------------

* Separação entre cliente e servidor.
* Facilidade de integração.
* API facilmente documentável.

====================
DA-06 — PostgreSQL
====================

Contexto
---------

O sistema necessita armazenar dados estruturados e garantir integridade das
informações.

Decisão
--------

Será utilizado PostgreSQL.

Justificativa
-------------

PostgreSQL oferece excelente suporte a transações, integridade referencial,
consultas complexas e escalabilidade para aplicações corporativas.

Consequências
-------------

* Banco de dados relacional robusto.
* Alto nível de confiabilidade.
* Excelente integração com Spring Boot.

====================
DA-07 — Autenticação Baseada em JWT
====================

Contexto
---------

O sistema necessita controlar o acesso de diferentes perfis de usuários.

Decisão
--------

Será utilizada autenticação baseada em JSON Web Token (JWT).

Justificativa
-------------

JWT permite autenticação stateless, reduzindo a necessidade de armazenamento de
sessões no servidor e facilitando integrações futuras.

Consequências
-------------

* Escalabilidade.
* Menor acoplamento entre cliente e servidor.
* Facilidade de integração com APIs.

====================
DA-08 — Organização por Domínio
====================

Contexto
---------

O sistema será dividido em módulos independentes de negócio.

Decisão
--------

A estrutura do código será organizada por funcionalidades e não por camadas
globais.

Exemplo:

::

    atividades/
        controller/
        service/
        repository/
        dto/

    certificados/
        controller/
        service/
        repository/

Justificativa
-------------

Essa abordagem favorece alta coesão, reduz dependências e facilita a localização
do código relacionado a uma funcionalidade.

Consequências
-------------

* Melhor organização.
* Evolução modular.
* Facilidade para novos desenvolvedores.

====================
DA-09 — Desenvolvimento Baseado em GitHub Flow
====================

Contexto
---------

O desenvolvimento será realizado por cinco integrantes utilizando GitHub.

Decisão
--------

Será adotado o GitHub Flow.

Justificativa
-------------

O fluxo é simples, adequado para equipes pequenas e favorece integração
contínua através de Pull Requests.

Consequências
-------------

* Desenvolvimento paralelo.
* Revisão obrigatória de código.
* Histórico limpo.
* Facilidade de colaboração.

====================
DA-10 — Documentação Versionada
====================

Contexto
---------

Toda a documentação deverá evoluir juntamente com o código-fonte.

Decisão
--------

A documentação será mantida no próprio repositório, utilizando arquivos
ReStructuredText.

Justificativa
-------------

Essa abordagem mantém documentação e código sincronizados, facilita revisões e
permite versionamento completo através do Git.

Consequências
-------------

* Documentação sempre atualizada.
* Histórico de alterações.
* Facilidade de colaboração.
* Redução de documentação desatualizada.

====================
Resumo das Decisões
====================

As decisões registradas neste documento definem os principais pilares da
arquitetura do Sistema de Gestão de Atividades Complementares.

Qualquer alteração significativa na arquitetura deverá ser discutida pela equipe
e, quando aprovada, registrada neste documento para manter a rastreabilidade e o
histórico das escolhas arquiteturais.
