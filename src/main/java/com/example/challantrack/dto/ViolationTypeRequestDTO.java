package com.example.challantrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ViolationTypeRequestDTO {

    @NotBlank(message = "Violation code cannot be blank")
    private String violationCode;

    @NotBlank(message = "Violation name cannot be blank")
    private String violationName;

    private String description;

    @NotNull(message = "Base fine is required")
    @Positive(message = "Base fine must be greater than zero")
    private BigDecimal baseFine;

    public ViolationTypeRequestDTO() {
    }

    public ViolationTypeRequestDTO(String violationCode, String violationName, String description, BigDecimal baseFine) {
        this.violationCode = violationCode;
        this.violationName = violationName;
        this.description = description;
        this.baseFine = baseFine;
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
}
