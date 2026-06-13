Arquivo 6 de 7 — cria 0004-redpanda-local-kafka-ci.md dentro de docs\adr.
Botão direito na pasta adr → New → File → 0004-redpanda-local-kafka-ci.md
Cola isso:
markdown# ADR-0004: Redpanda no Local, Apache Kafka no CI

## Status
Aceito

## Contexto
O ambiente de desenvolvimento tem **8 GB de RAM**. A stack completa (app Spring +
PostgreSQL + Redis + broker + observabilidade) é pesada. Um broker Kafka roda sobre
a JVM e consome facilmente 1 GB+, competindo com a própria aplicação (que também é
JVM). Precisamos de leveza local **sem** perder fidelidade ao Kafka.

## Decisão
- **Local (docker-compose)**: usar **Redpanda** — broker compatível com a API do
  Kafka, escrito em C++, sem JVM, significativamente mais leve.
- **CI (GitHub Actions)**: rodar os testes de integração com **Apache Kafka real**
  via Testcontainers, nos runners do GitHub (não na máquina local).
- O código da aplicação usa **Spring Kafka** (cliente padrão, *vendor-neutral*).
  Nenhuma linha muda entre os dois ambientes.

## Consequências

### Positivas
- Desenvolvimento local leve, viável em 8 GB de RAM.
- Testes de integração rodam contra Apache Kafka de verdade.
- Zero mudança de código entre local e CI.
- Para fins de portfólio, o sistema usa o protocolo Kafka e é testado contra
  Apache Kafka — afirmação honesta e verificável.

### Trade-offs
- Dois ambientes ligeiramente diferentes (Redpanda vs Kafka), mitigado pelos
  testes de integração no CI.

## Observação
O Kafka moderno roda em modo **KRaft** (sem Zookeeper), então mesmo o Kafka real
no CI é um único processo — sem container extra de coordenação.