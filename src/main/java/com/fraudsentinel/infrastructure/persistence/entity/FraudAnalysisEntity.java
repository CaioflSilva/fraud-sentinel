package com.fraudsentinel.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FraudAnalysisEntity {

    @Id
    private UUID id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private UUID transactionId;

    @Column(name = "rule_score", nullable = false)
    private int ruleScore;

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "final_score", nullable = false)
    private int finalScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "triggered_rules", columnDefinition = "jsonb")
    private String triggeredRules;

    @Column(name = "ai_justification")
    private String aiJustification;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}