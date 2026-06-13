# ADR-0005: Transactional Outbox como Evolução Deliberada

## Status
Aceito

## Contexto
No fluxo de ingestão, persistimos a transação (status `PENDING`) e publicamos um
evento no Kafka. Isso é uma **escrita dupla (dual-write)** em dois sistemas sem uma
transação distribuída. Se o publish falhar após o commit no banco (ou vice-versa),
ficamos com inconsistência: transação sem evento, ou evento sem transação.

A solução correta é o **Transactional Outbox Pattern**: o evento é gravado em uma
tabela (`OUTBOX_EVENT`) na **mesma transação** da transação de negócio; um relay lê
essa tabela e publica no Kafka de forma confiável, marcando como publicado.

## Decisão
Adotar uma evolução em duas etapas, de forma **deliberada e didática**:

1. **Fase 3 (inicial)**: publicar direto no Kafka após o commit. Simples, rápido de
   implementar, e expõe o problema do dual-write na prática.
2. **Refatoração (logo após)**: introduzir a tabela `OUTBOX_EVENT` e o relay,
   eliminando a janela de inconsistência.

Esta não é uma decisão de "começar errado". É uma escolha pedagógica para evidenciar
o problema, entender o *porquê* do pattern e registrar a evolução.

## Consequências

### Positivas
- Entendimento real do trade-off (não decoreba do pattern).
- Narrativa técnica forte: "identifiquei o dual-write e refatorei para Outbox".
- Histórico de commits que conta a evolução da arquitetura.

### Trade-offs
- Retrabalho intencional entre a Fase 3 e a refatoração.
- Janela temporária com risco de inconsistência antes da refatoração (aceitável em
  ambiente de desenvolvimento, nunca promovido a produção sem o Outbox).