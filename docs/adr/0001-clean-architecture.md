# ADR-0001: Clean Architecture com Ports & Adapters

## Status
Aceito

## Contexto
O Fraud Sentinel será apresentado como case técnico e, na narrativa, auditado por
times de arquitetura, segurança e compliance. Precisamos de um sistema testável,
manutenível e com regras de negócio que não dependam de framework. Acoplar a lógica
de fraude ao Spring ou ao JPA tornaria o núcleo frágil e difícil de testar.

## Decisão
Adotar Clean Architecture com princípios hexagonais:

- **domain**: entidades e regras de negócio puras, **zero dependência** de Spring/JPA.
- **application**: casos de uso + *ports* (interfaces de entrada e saída).
- **infrastructure**: *adapters* que implementam os ports (JPA, Kafka, Redis, IA).
- **presentation**: controllers e DTOs.

Entidades JPA (`@Entity`) ficam em `infrastructure` e são **separadas** das entidades
de domínio. A conversão entre elas é feita com **MapStruct**. Nenhuma entidade JPA
é exposta diretamente em controllers.

## Consequências

### Positivas
- Alta testabilidade do domínio (testes sem subir contexto Spring).
- Baixo acoplamento; trocar PostgreSQL, Kafka ou Redis não toca o domínio.
- Separação clara de responsabilidades, fácil de auditar.

### Trade-offs
- Mais boilerplate (mappers, interfaces, classes duplicadas domínio/entity).
- Curva de aprendizado maior do que um CRUD em camadas tradicional.