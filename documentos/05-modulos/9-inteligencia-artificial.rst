=========================================
Módulo de Inteligência Artificial
=========================================

.. contents::
   :local:
   :depth: 2

====================
Introdução
====================

O módulo de Inteligência Artificial é responsável por fornecer recursos de automação cognitiva, leitura inteligente de documentos e auditoria de conformidade regulatória para o Sistema de Gestão de Atividades Complementares.

Este módulo atua de forma transversal aos fluxos de cadastro, validação e gestão normativa, reduzindo a carga manual de preenchimento e auxiliando avaliadores na tomada de decisão baseada em evidências normativas.

====================
Objetivos
====================

O módulo de Inteligência Artificial busca:

* automatizar a extração de metadados a partir de certificados digitais (PDF e imagens);
* estruturar e vetorizar os regulamentos institucionais e PPCs da UFAPE;
* auditar atividades complementares comparando seus dados contra os artigos normativos vigentes (RAG);
* fornecer métricas empíricas de concordância entre as análises da IA e as decisões dos avaliadores humanos.

====================
Usuários Envolvidos
====================

Estudante
=========

Beneficia-se da extração automática de dados dos comprovantes durante o cadastro e da transparência proporcionada pelo parecer técnico preliminar.

Avaliador
=========

Utiliza o parecer de conformidade e a citação direta dos artigos do PPC como subsídio técnico para deferir ou indeferir solicitações.

Administrador
=============

Responsável pelo upload e atualização da base normativa de conhecimento e pelo acompanhamento das métricas de acurácia da IA.

====================
Responsabilidades
====================

Extração de Metadados de Certificados
-------------------------------------

Analisar arquivos enviados nos formatos PDF, PNG e JPEG, identificando automaticamente título da atividade, instituição emissora, data de realização, carga horária, natureza e categoria sugerida.

Ingestão e Vetorização Normativa
--------------------------------

Processar documentos normativos (PPCs, Resoluções de ACC/ACEX), segmentar regras em fragmentos coesos (chunks) e gerar representações vetoriais densas (embeddings) para indexação semântica.

Auditoria Regulatória (RAG)
---------------------------

Executar recuperação por similaridade semântica dos artigos aplicáveis a cada atividade e gerar parecer técnico contendo decisão sugerida, teto de horas aproveitável, artigo citado e justificativa formal.

Métricas de Desempenho e Concordância
-------------------------------------

Consolidar dados estatísticos sobre a quantidade de atividades auditadas, taxa de concordância com avaliadores humanos e tempo médio de inferência.

====================
Conceitos do Domínio
====================

Regulamento Chunk
=================

Representa um fragmento normativo (artigo, parágrafo ou quadro de horas) persistido com seu texto original e seu vetor semântico associado.

Parecer de Conformidade
=======================

Representa a avaliação técnica emitida pela IA sobre uma atividade complementar, contendo a decisão sugerida, justificativa e artigo normativo de embasamento.

Decisão da IA
=============

Resultado da auditoria automática:

* DEFERIDO: atividade atende aos requisitos e limites regulamentares;
* INDEFERIDO: atividade contraria as regras ou tetos do regulamento;
* AMBIGUO: informações insuficientes ou divergentes que exigem decisão humana exclusiva.

Embedding Vetorial
==================

Representação numérica multidimensional utilizada para calcular a proximidade semântica entre uma atividade e os artigos normativos da instituição.

====================
Regras de Negócio
====================

RN-IA-01
--------

A extração inteligente deve ser tolerante a falhas: eventuais indisponibilidades de modelos externos não devem bloquear o anexo do arquivo nem impedir o preenchimento manual pelo usuário.

RN-IA-02
--------

O parecer de conformidade emitido pela IA possui caráter consultivo e de apoio à decisão, cabendo ao avaliador humano a decisão final homologatória.

RN-IA-03
--------

A alteração dos dados ou do certificado de uma atividade deve invalidar automaticamente o parecer anterior, exigindo nova auditoria regulatória.

RN-IA-04
--------

A ingestão de novos documentos normativos e a consulta de métricas globais de pesquisa devem ser restritas aos perfis administrativos e avaliadores.

RN-IA-05
--------

O teto de carga horária aproveitável sugerido pela IA nunca poderá ultrapassar o valor estabelecido na regra normativa recuperada para a categoria correspondente.

====================
Endpoints do Módulo
====================

Extração de Certificados
------------------------

::

    POST /api/v1/atividades/extrair-certificado

    Consome: multipart/form-data
    Retorna: Metadados extraídos (título, instituição, data, carga horária, natureza, categoria)

Parecer de Conformidade
-----------------------

::

    GET /api/v1/atividades/{id}/parecer

    Retorna: Parecer de conformidade gerado/persistido para a atividade

Ingestão de Regulamentos
------------------------

::

    POST /api/v1/regulamentos/ingerir
    GET  /api/v1/regulamentos

    Permite upload, vetorização e consulta dos fragmentos normativos ativos

Métricas de Pesquisa
--------------------

::

    GET /api/v1/metricas-pesquisa/concordancia-kappa

    Retorna: Amostras avaliadas, taxa de concordância e tempo médio de inferência

====================
Resumo
====================

O módulo de Inteligência Artificial agrega capacidade preditiva e automação documental ao SGAC, 
transformando regulamentos estáticos em uma base de conhecimento ativa e proporcionando maior 
celeridade e conformidade jurídica ao processo de validação de atividades complementares.