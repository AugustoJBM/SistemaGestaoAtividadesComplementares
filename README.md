[![CI](https://github.com/GestaoAtividadeComplementarOrg/GestaoAtividadesComplementares/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/GestaoAtividadeComplementarOrg/GestaoAtividadesComplementares/actions/workflows/ci.yml)
[![DEPLOY](https://github.com/GestaoAtividadeComplementarOrg/GestaoAtividadesComplementares/actions/workflows/deploy.yml/badge.svg)](https://github.com/GestaoAtividadeComplementarOrg/GestaoAtividadesComplementares/actions/workflows/deploy.yml)

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=arayumi3_GestaoAtividadesComplementares&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=arayumi3_GestaoAtividadesComplementares)

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=arayumi3_GestaoAtividadesComplementares)

## Deploy em Produção (Render)

- **Frontend (SPA):** [https://sgac-frontend.onrender.com](https://gestaoatividadescomplementares.onrender.com)
- **Backend (API REST):** [https://sgac-backend-3rub.onrender.com](https://gestaoatividadescomplementares-backend.onrender.com)
- **SonarCloud:** [https://sonarcloud.io/project/overview?id=arayumi3_GestaoAtividadesComplementares](https://sonarcloud.io/project/overview?id=arayumi3_GestaoAtividadesComplementares)
- **Banco de Dados:** PostgreSQL (Render)

### SENHA DE ACESSO PADRÃO
- **LOGIN:** test@test.test
- **PASSWORD:** 12345678

# Sistema de Gestão de Atividades Complementares

## :octocat: Integrantes

- Thayson Guedes de Medeiros
- Tiago Jose Santos da Cunha
- Ayumi Rayani da Silva Lima
- Augusto Jorge Brandão Mendonça
- João Vitor da Silva Moura

---

## :page_with_curl: Sobre o Projeto

Projeto desenvolvido para a disciplina de Engenharia de Software, ministrada pela Professora Dr. Thaís Alves Burity Rocha, da Universidade Federal do Agreste de Pernambuco (UFAPE).

O Sistema de Gestão de Atividades Complementares tem como objetivo auxiliar os estudantes da UFAPE no gerenciamento das Atividades Complementares Curriculares (ACC) e das Atividades Curriculares de Extensão (ACEX).

A plataforma busca automatizar e simplificar o processo de acompanhamento das atividades complementares, permitindo o armazenamento de certificados, controle da carga horária por categoria, acompanhamento do percentual concluído em cada natureza de atividade, emissão de relatórios para formalização do processo institucional e envio de notificações ao estudante.

---

## :round_pushpin: Objetivos

O sistema deverá permitir que o estudante:

- Realize seu cadastro e autenticação na plataforma;
- Faça upload e gerenciamento de certificados das atividades realizadas;
- Acompanhe a carga horária acumulada por categoria (ACC e ACEX);
- Visualize o percentual concluído em cada natureza de atividade;
- Emita relatórios e documentos necessários para solicitação de validação das atividades;
- Receba notificações sobre pendências, prazos e atualizações relacionadas ao processo de validação.

---

## 🛠️ Tecnologias Utilizadas

### Front-end
- Angular
- TypeScript
- Tailwind CSS

### Back-end
- Java 21
- Spring Boot
- Spring Security
- JWT

### Banco de Dados
- H2 em memória para desenvolvimento
- PostgreSQL previsto para produção

### Ferramentas
- Git & GitHub
- GitHub Actions
- Docker (apoio de ambiente, não obrigatório para a execução local padrão)

---

## 💻 Como executar o projeto

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

- Java 21+
- Node.js 22+
- npm
- Git

### 1) Executar o backend

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

O backend inicia em: `http://localhost:8080`

### 2) Executar o frontend

```bash
cd Front
npm ci
npm start
```

O frontend inicia em: `http://localhost:4200`

### 3) Validar testes e build

```bash
cd backend && ./mvnw test
cd ../Front && npm ci && npm test && npm run build
```

---

## 🔐 Variáveis de ambiente

O backend usa algumas variáveis de ambiente para configuração local e de produção:

- **`JWT_SECRET`**: segredo usado para assinar os tokens JWT.
  Valor padrão em desenvolvimento: `SenhaDeTeste==123==EssaSenhaSoServeParaTestes`
- **`DB_PASSWORD`**: senha do PostgreSQL quando o perfil `prod` está ativo.
- **`SPRING_PROFILES_ACTIVE`**: controla o perfil ativo do Spring.
  - `dev` → usa H2 em memória
  - `prod` → usa PostgreSQL

Exemplo de uso em desenvolvimento:

```bash
export JWT_SECRET="SenhaDeTeste==123==EssaSenhaSoServeParaTestes"
```

---

## 🗄️ Situação do banco de dados

O projeto está configurado para dois cenários:

**Desenvolvimento: H2 em memória**
- Arquivo de configuração: `application-dev.properties`
- URL: `jdbc:h2:mem:testdb`
- Ideal para execução local e testes rápidos

**Produção: PostgreSQL**
- Arquivo de configuração: `application-prod.properties`
- URL: `jdbc:postgresql://localhost:5432/gestao_atividades_complementares`
- Requer a variável `DB_PASSWORD` configurada

O arquivo `application.properties` usa o perfil `dev` por padrão, então a execução local padrão do projeto continua em H2.

---

## 📌 Observação final

Este README foi ajustado para refletir exatamente o estado real do projeto: execução local com H2 em desenvolvimento, uso de PostgreSQL em produção e comandos reais de teste e build do backend e frontend.
