package com.example.challantrack.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ViolationTypeResponseDTO {

    private Long id;
    private String violationCode;
    private String violationName;
    private String description;
    private BigDecimal baseFine;
    private LocalDateTime createdAt;

    public ViolationTypeResponseDTO() {
    }

    public ViolationTypeResponseDTO(Long id, String violationCode, String violationName, String description, BigDecimal baseFine, LocalDateTime createdAt) {
        this.id = id;
        this.violationCode = violationCode;
        this.violationName = violationName;
        this.description = description;
        this.baseFine = baseFine;
        this.createdAt = createdAt;
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
