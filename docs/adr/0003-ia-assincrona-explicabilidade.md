# ADR-0003: IA como Camada Assíncrona de Explicabilidade

## Status
Aceito

## Contexto
A proposta inicial colocava o modelo LLaMA (via Groq) no **caminho síncrono de decisão**
da fraude. Isso tem três problemas sérios para um sistema financeiro:

1. **Latência**: uma chamada a LLM custa de centenas de ms a segundos — inaceitável
   no caminho crítico de uma decisão antifraude.
2. **Não-determinismo**: o mesmo input pode gerar outputs diferentes; auditar e
   reproduzir uma decisão de bloqueio fica inviável.
3. **Disponibilidade**: depender de um provider externo (Groq) no caminho crítico
   significa que, se ele cair, o antifraude para.

## Decisão
Separar **decisão** de **explicação**:

- A decisão em tempo real é feita **exclusivamente** pelo rule engine determinístico
    + Redis. Rápido, reproduzível, auditável.
- A IA consome um **segundo evento** (`FraudAnalyzed`) e enriquece a análise de forma
  **assíncrona**: produz um score consultivo, uma justificativa em linguagem natural
  e indicadores de risco.
- O score final combina os dois, com pesos configuráveis:
  `finalScore = (ruleScore × 0.70) + (aiScore × 0.30)`.
- **Degradação graciosa**: se a IA falhar ou estourar timeout, a análise mantém o
  rule score e o sistema segue funcionando.

## Consequências

### Positivas
- Resiliência: o antifraude funciona mesmo sem IA.
- SLA previsível: a IA não impacta a latência do caminho de decisão.
- Explicabilidade sem acoplar a decisão a um modelo não-determinístico.
- Decisão arquitetural forte e defensável em entrevista técnica.

### Trade-offs
- O score final é **eventualmente consistente** (enriquecimento chega depois).
- Uma transação pode ser inicialmente classificada só pelo rule score e ter a
  justificativa da IA preenchida segundos depois.