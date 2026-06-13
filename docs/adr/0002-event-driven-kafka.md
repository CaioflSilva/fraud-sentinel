# ADR-0002: Arquitetura Orientada a Eventos com Kafka

## Status
Aceito

## Contexto
Detecção de fraude precisa desacoplar a **ingestão** da transação da sua **análise**.
Análise síncrona dentro do request HTTP aumentaria a latência da API e impediria
escalar a parte de processamento de forma independente. Também queremos processar
em *near-real-time* e suportar picos de volume.

## Decisão
A ingestão apenas persiste a transação e publica um evento (`TransactionCreated`).
A análise de fraude roda em um **consumer assíncrono**. O Kafka é o backbone de
mensageria. Decisões de robustez:

- **Consumers idempotentes**: reprocessar um evento não duplica a análise (controle
  por chave de idempotência / status persistido).
- **Dead Letter Queue (DLQ)**: eventos que falham repetidamente vão para uma fila
  morta, sem travar o fluxo principal.
- **Correlation ID** propagado do request HTTP até o consumer (via MDC).

## Consequências

### Positivas
- Escalabilidade: consumers escalam separadamente da API.
- Resiliência: pico de carga vira backlog na fila, não timeout no cliente.
- Desacoplamento entre ingestão e processamento.

### Trade-offs
- Complexidade operacional (broker a mais para rodar e monitorar).
- **Consistência eventual**: a transação responde `202 Accepted` antes da análise.
- Exige disciplina de idempotência e tratamento de DLQ.