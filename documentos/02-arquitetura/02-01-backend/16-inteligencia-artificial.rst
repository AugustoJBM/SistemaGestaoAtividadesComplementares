=========================================
Arquitetura do Módulo de IA
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

Este documento define a arquitetura técnica, os padrões de integração e o fluxo de dados do módulo de Inteligência Artificial no backend do Sistema de Gestão de Atividades Complementares.

O módulo combina processamento de documentos, geração de embeddings semânticos e modelos de linguagem (LLM) integrados via arquitetura RAG (*Retrieval-Augmented Generation*).

====================
Visão Geral da Arquitetura
====================

A arquitetura do módulo de IA é estruturada em três pipelines principais:

::

    1. Pipeline de Ingestão Normativa (PPC/Regulamentos)
       Documento PDF/TXT -> Extrator de Texto -> Segmentação -> Embedding Service -> Banco Vetorial

    2. Pipeline de Extração de Certificados
       Comprovante (PDF/Imagem) -> PDFBox/Visão Multimodal -> LLM -> DTO Estruturado

    3. Pipeline de Auditoria Regulatória (True RAG)
       Atividade -> Embedding de Consulta -> Busca por Similaridade -> LLM com Contexto -> Parecer

====================
Componentes e Integrações
====================

Hugging Face Embeddings
-----------------------

Responsável pela geração de representações vetoriais densas de 384 dimensões a partir de textos normativos e consultas, utilizando modelos da família *sentence-transformers* (ex: ``all-MiniLM-L6-v2``).

Groq LLM Service
----------------

Responsável pela inferência de alta velocidade utilizando modelos de linguagem (ex: ``qwen/qwen3.6-27b``) para:

* estruturação de dados não estruturados de certificados;
* análise de conformidade e geração da justificativa técnica fundamentada nas normas recuperadas.

Apache PDFBox
-------------

Utilizado na extração de texto vetorial de documentos PDF e na renderização sob demanda de páginas para processamento visual multimodal quando o documento for escaneado.

====================
Fluxo de Auditoria Regulatória (RAG)
====================

O fluxo de geração de parecer ocorre nas seguintes etapas:

::

    [AtividadeComplementar]
               │
               ▼
    [Geração de Embedding da Atividade]
               │
               ▼
    [Busca por Similaridade de Cosseno no Banco]
               │
               ▼
    [Recuperação dos Chunks Normativos Mais Relevantes]
               │
               ▼
    [Montagem do Prompt com Contexto Regulatório Dinâmico]
               │
               ▼
    [Chamada à API Groq (LLM)]
               │
               ▼
    [Parsing Resiliente JSON/Regex]
               │
               ▼
    [Persistência em ParecerConformidade]

====================
Resiliência e Tolerância a Falhas
====================

O módulo adota estratégias de contingência para garantir a continuidade operacional:

Fallback Determinístico na Ingestão
-----------------------------------

Caso a API de LLM esteja indisponível durante a ingestão de um regulamento, o sistema aciona um extrator heurístico baseado em expressões regulares capaz de identificar capítulos, artigos e quadros de carga horária diretamente da estrutura do texto.

Parsing Resiliente de Respostas
-------------------------------

As respostas dos modelos passam por sanitização prévia (remoção de tags de raciocínio, formatações markdown e blocos de código). Caso a serialização JSON falhe, um mecanismo baseado em regex recupera os campos essenciais do parecer.

Degradação Graciosa
-------------------

Em caso de indisponibilidade total dos serviços externos de IA, o sistema retorna pareceres com status informativo (`AMBIGUO`) sem interromper as demais operações do sistema.

====================
Diretrizes de Comunicação Modular
====================

Para manter a conformidade com as regras de fronteiras modulares do projeto:

* o módulo de IA deve expor suas operações para outros módulos exclusivamente através de fachadas (`IaCertificadoFacade`, `RegulamentoFacade`) ou contratos de integração;
* classes de outros domínios (como `AtividadeComplementarService`) não devem acessar diretamente repositórios internos do módulo de IA (`RegulamentoChunkRepository`).

====================
Resumo
====================

A arquitetura do módulo de IA combina recuperação semântica e modelos de linguagem para oferecer auditoria regulatória e extração documental de alta precisão, preservando a resiliência e a modularidade da aplicação.