package com.example.challantrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "violation_type", uniqueConstraints = @UniqueConstraint(columnNames = "violation_code"))
public class ViolationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Violation code cannot be blank")
    @Column(name = "violation_code", nullable = false, unique = true, length = 20)
    private String violationCode;

    @NotBlank(message = "Violation name cannot be blank")
    @Column(name = "violation_name", nullable = false)
    private String violationName;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Base fine is required")
    @Positive(message = "Base fine must be greater than zero")
    @Column(name = "base_fine", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFine;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public ViolationType() {
    }

    public ViolationType(String violationCode, String violationName, String description, BigDecimal baseFine) {
        this.violationCode = violationCode;
        this.violationName = violationName;
        this.description = description;
        this.baseFine = baseFine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViolationCode() {
        return violationCode;
    }

    public void setViolationCode(String violationCode) {
        this.violationCode = violationCode;
    }

    public String getViolationName() {
        return violationName;
    }

    public void setViolationName(String violationName) {
        this.violationName = violationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseFine() {
        return baseFine;
    }

    public void setBaseFine(BigDecimal baseFine) {
        this.baseFine = baseFine;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
