Limpo, sem sujeira. architecture.md ✅
Arquivo 2 de 7 — cria PACKAGE_STRUCTURE.md na pasta docs (mesmo lugar do architecture, não dentro de adr).
IntelliJ: botão direito na pasta docs → New → File → PACKAGE_STRUCTURE.md
Cola isso:
markdown# Fraud Sentinel — Estrutura de Pacotes

Pacote base: `com.fraudsentinel`

A organização segue Clean Architecture. A **regra de dependência** é a coluna
vertebral do projeto:

> As dependências apontam **sempre para dentro**. `presentation` e `infrastructure`
> dependem de `application`, que depende de `domain`. **`domain` não depende de
> ninguém** — nem de Spring, nem de JPA, nem de Kafka.
com.fraudsentinel

│

├── domain                          # núcleo de negócio — ZERO dependência de framework

│   ├── transaction

│   │   ├── Transaction.java        # entidade de domínio (NÃO é @Entity JPA)

│   │   ├── TransactionStatus.java  # enum: PENDING, ANALYZING, APPROVED, FLAGGED...

│   │   └── Money.java              # value object (amount + currency)

│   ├── fraud

│   │   ├── FraudRule.java          # interface — elo da Chain of Responsibility

│   │   ├── RiskScore.java          # value object (0-100)

│   │   ├── RiskLevel.java          # enum: LOW, MEDIUM, HIGH, CRITICAL

│   │   ├── FraudAnalysis.java      # resultado da análise

│   │   └── rules                   # implementações das regras de fraude

│   │       ├── SuspiciousAmountRule.java

│   │       ├── AbnormalFrequencyRule.java

│   │       ├── UnusualLocationRule.java

│   │       ├── UnknownDeviceRule.java

│   │       └── SuspiciousTimeRule.java

│   ├── user

│   │   ├── User.java

│   │   └── Role.java               # ADMIN, ANALYST...

│   └── exception                   # exceções de domínio (puras)

│

├── application                     # orquestra o domínio — define os contratos

│   ├── usecase

│   │   ├── auth                    # login, refresh, logout

│   │   ├── transaction             # submeter / consultar transação

│   │   └── fraud                   # processar análise, enriquecer com IA

│   └── port                        # interfaces (hexagonal)

│       ├── in                      # portas de entrada (contratos dos use cases)

│       └── out                     # portas de saída (o que o domínio precisa do mundo)

│           ├── TransactionRepository.java

│           ├── FraudAnalysisRepository.java

│           ├── EventPublisher.java

│           ├── BlacklistCache.java

│           └── AiAnalysisPort.java

│

├── infrastructure                  # ADAPTERS — implementam os ports de saída

│   ├── persistence

│   │   ├── entity                  # @Entity JPA (SEPARADO do domínio)

│   │   ├── repository              # Spring Data repositories + adapters

│   │   └── mapper                  # MapStruct: domínio <-> entity

│   ├── messaging

│   │   ├── producer                # publica eventos no Kafka

│   │   ├── consumer                # FraudConsumer, AiEnrichmentConsumer

│   │   └── outbox                  # tabela + relay (Transactional Outbox)

│   ├── cache                       # adapters Redis (blacklist, histórico, cache de regras)

│   ├── ai                          # Spring AI / Groq adapter (implementa AiAnalysisPort)

│   └── security                    # JWT, filtros, BCrypt, Bucket4j

│

├── presentation                    # camada web (HTTP)

│   ├── controller                  # REST controllers

│   ├── dto                         # request/response (NUNCA expõe entidade JPA)

│   ├── mapper                      # DTO <-> domínio

│   └── exception                   # @RestControllerAdvice (tratamento global)

│

├── configuration                   # @Configuration: beans, Kafka, Redis, Security, OpenAPI

│

├── shared                          # utilitários transversais

│   ├── correlation                 # Correlation ID / MDC

│   └── logging                     # structured logging

│

└── FraudSentinelApplication.java   # @SpringBootApplication

## Por que separar entidade de domínio de entidade JPA?

É a pergunta clássica de entrevista. A `Transaction` do `domain` modela a **regra de
negócio**; a `TransactionEntity` da `infrastructure` modela a **tabela** (anotações
`@Entity`, `@Column`, relacionamentos JPA). Mantê-las separadas evita que decisões de
persistência (lazy loading, anotações, herança JPA) vazem para o núcleo. O MapStruct
faz a tradução nas bordas.

Para um CRUD pequeno isso é overkill — e tudo bem assumir esse trade-off aqui, porque
o objetivo é demonstrar domínio de arquitetura corporativa (ver ADR-0001).