# Fraud Sentinel — Arquitetura

Sistema de detecção de fraudes financeiras em tempo real, orientado a eventos,
com análise por regras determinísticas e enriquecimento assíncrono por IA.

> **Princípio central:** a decisão em tempo real é feita pelo *rule engine* +
> Redis (rápido, determinístico, auditável). A IA **não decide** — ela enriquece
> a análise de forma assíncrona com um score consultivo e uma justificativa.
> Se a IA cair, o antifraude continua funcionando (degradação graciosa).

---

## 1. Arquitetura Geral

Camadas seguindo Clean Architecture + Ports & Adapters. O domínio não conhece
framework nem infraestrutura; toda dependência externa passa por interfaces.

```mermaid
graph TB
    U[Cliente Autenticado]

    subgraph PRESENTATION[Presentation]
        SEC[Security / JWT Filter]
        RL[Rate Limiting - Bucket4j]
        CTRL[REST Controllers]
    end

    subgraph APPLICATION[Application]
        UC[Use Cases]
        PORTS[Ports - Interfaces]
    end

    subgraph DOMAIN[Domain]
        RULES[Fraud Rules - Chain of Responsibility]
        SCORE[Risk Scoring]
    end

    subgraph INFRA[Infrastructure - Adapters]
        JPA[JPA Adapter]
        KP[Kafka Producer]
        KC[Kafka Consumer]
        RC[Redis Adapter]
        AIA[Spring AI Adapter]
        OBX[Outbox Relay]
    end

    subgraph EXTERNAL[Sistemas Externos]
        PG[(PostgreSQL)]
        RD[(Redis)]
        KAFKA[[Kafka / Redpanda]]
        GROQ{{Groq - LLaMA}}
    end

    U -->|HTTPS + JWT| SEC
    SEC --> RL
    RL --> CTRL
    CTRL --> UC
    UC --> PORTS
    UC --> RULES
    RULES --> SCORE
    PORTS -.-> JPA
    PORTS -.-> KP
    PORTS -.-> RC
    PORTS -.-> AIA
    JPA --> PG
    RC --> RD
    KP --> KAFKA
    KAFKA --> KC
    KC --> UC
    OBX --> KAFKA
    AIA --> GROQ
```

---

## 2. Fluxo de Autenticação

JWT com Access + Refresh Token e blacklist em Redis (logout/revogação por `jti`).

```mermaid
sequenceDiagram
    actor User
    participant API as Auth Controller
    participant SEC as Security / JWT
    participant RD as Redis
    participant DB as PostgreSQL

    User->>API: POST /auth/login (credenciais)
    API->>DB: busca usuário
    DB-->>API: usuário + hash
    API->>SEC: valida senha (BCrypt)
    SEC-->>API: ok
    API->>SEC: gera Access + Refresh Token
    API-->>User: 200 {accessToken, refreshToken}

    Note over User,DB: Requisição autenticada
    User->>API: GET /resource (Bearer accessToken)
    API->>SEC: valida assinatura + expiração
    SEC->>RD: jti está na blacklist?
    RD-->>SEC: não
    SEC-->>API: autenticado
    API-->>User: 200 recurso

    Note over User,RD: Logout / revogação
    User->>API: POST /auth/logout
    API->>RD: adiciona jti à blacklist (TTL = expiração)
    API-->>User: 204
```

---

## 3. Fluxo Kafka (ingestão + Outbox)

A transação é persistida e o evento é gravado na **mesma transação** (Outbox).
Um relay publica no Kafka depois — eliminando o problema de *dual-write*.

```mermaid
sequenceDiagram
    participant C as Transaction Controller
    participant UC as Submit Transaction UseCase
    participant DB as PostgreSQL
    participant OBX as Outbox Relay
    participant K as Kafka / Redpanda
    participant CONS as Fraud Consumer

    C->>UC: nova transação
    UC->>DB: persiste Transaction (PENDING) + Outbox event (mesma TX)
    DB-->>UC: commit
    UC-->>C: 202 Accepted (id)
    OBX->>DB: lê eventos não publicados
    OBX->>K: publica TransactionCreated
    OBX->>DB: marca como publicado
    K->>CONS: consome evento (idempotente)
    Note over CONS: processa análise de fraude
    CONS->>K: em caso de falha -> DLQ
```

---

## 4. Fluxo de Análise de Fraude (Rule Engine)

As regras são elos de uma *Chain of Responsibility*. Cada uma soma ao score.
Tudo aqui é determinístico e rápido — sem chamada de IA no caminho.

```mermaid
flowchart TD
    START([Evento TransactionCreated]) --> IDEM{Já processado?}
    IDEM -->|Sim| SKIP[Ignora - idempotência]
    IDEM -->|Não| CTX[Carrega contexto]
    CTX --> RD1[Redis: blacklist]
    CTX --> RD2[Redis: histórico do usuário]
    CTX --> CHAIN[Chain of Responsibility]

    subgraph REGRAS[Regras de Fraude]
        R1[Valor suspeito] --> R2[Frequência anormal]
        R2 --> R3[Localização incomum]
        R3 --> R4[Dispositivo desconhecido]
        R4 --> R5[Horário suspeito]
    end

    CHAIN --> R1
    R5 --> RSCORE[Rule Score 0-100]
    RSCORE --> SAVE[Persiste resultado parcial]
    SAVE --> EMIT[Publica FraudAnalyzed]
    EMIT --> AIQ[/Fila p/ enriquecimento IA/]
```

---

## 5. Fluxo da IA (enriquecimento assíncrono)

A IA consome um segundo evento. Note o caminho de falha: se o Groq estiver
indisponível ou estourar timeout, o sistema mantém o rule score e segue.

```mermaid
sequenceDiagram
    participant K as Kafka (FraudAnalyzed)
    participant AIC as AI Enrichment Consumer
    participant SAI as Spring AI Adapter
    participant GROQ as Groq / LLaMA
    participant DB as PostgreSQL

    K->>AIC: consome FraudAnalyzed (assíncrono)
    AIC->>SAI: monta prompt (contexto + rule score)
    SAI->>GROQ: chama modelo (API OpenAI-compatible)
    alt IA disponível
        GROQ-->>SAI: {aiScore, justificativa, indicadores}
        SAI-->>AIC: resultado IA
        AIC->>AIC: finalScore = rule*0.70 + ai*0.30
        AIC->>DB: atualiza análise (ENRICHED)
    else IA indisponível / timeout
        GROQ--xSAI: erro
        AIC->>DB: mantém rule score (degradação graciosa)
        Note over AIC,DB: antifraude continua funcionando sem IA
    end
```

---

## 6. Fluxo de Persistência

### Ciclo de vida da transação

```mermaid
stateDiagram-v2
    [*] --> PENDING: transação recebida
    PENDING --> ANALYZING: consumer pega o evento
    ANALYZING --> RULES_DONE: rule engine concluído
    RULES_DONE --> ENRICHED: IA concluída ou timeout
    ENRICHED --> APPROVED: score LOW / MEDIUM
    ENRICHED --> FLAGGED: score HIGH / CRITICAL
    APPROVED --> [*]
    FLAGGED --> [*]
```

### Modelo de dados (núcleo)

```mermaid
erDiagram
    USER ||--o{ TRANSACTION : submits
    TRANSACTION ||--|| FRAUD_ANALYSIS : has
    TRANSACTION ||--o{ OUTBOX_EVENT : generates

    USER {
        uuid id PK
        string email
        string passwordHash
        string role
    }
    TRANSACTION {
        uuid id PK
        uuid userId FK
        decimal amount
        string currency
        string status
        timestamp createdAt
    }
    FRAUD_ANALYSIS {
        uuid id PK
        uuid transactionId FK
        int ruleScore
        int aiScore
        int finalScore
        string riskLevel
        text aiJustification
    }
    OUTBOX_EVENT {
        uuid id PK
        string aggregateType
        string eventType
        json payload
        boolean published
    }
```

---

## Faixas de Score de Risco

| Nível | Faixa |
|-------|-------|
| LOW | 0–25 |
| MEDIUM | 26–50 |
| HIGH | 51–75 |
| CRITICAL | 76–100 |

`finalScore = (ruleScore × 0.70) + (aiScore × 0.30)` — pesos configuráveis via `application.yml`.