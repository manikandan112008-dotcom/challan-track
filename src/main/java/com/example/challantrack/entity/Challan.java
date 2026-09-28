package com.example.challantrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challan", uniqueConstraints = @UniqueConstraint(columnNames = "challan_number"))
public class Challan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Challan number cannot be blank")
    @Column(name = "challan_number", nullable = false, unique = true, length = 50)
    private String challanNumber;

    @NotNull(message = "Vehicle is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "Violation type is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "violation_type_id", nullable = false)
    private ViolationType violationType;

    @NotBlank(message = "Officer name cannot be blank")
    @Column(name = "officer_name", nullable = false)
    private String officerName;

    @NotNull(message = "Violation date is required")
    @Column(name = "violation_date", nullable = false)
    private LocalDate violationDate;

    @NotNull(message = "Original fine is required")
    @Positive(message = "Original fine must be greater than zero")
    @Column(name = "original_fine", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalFine;

    @NotNull(message = "Applicable fine is required")
    @Positive(message = "Applicable fine must be greater than zero")
    @Column(name = "applicable_fine", nullable = false, precision = 10, scale = 2)
    private BigDecimal applicableFine;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ChallanStatus status = ChallanStatus.UNPAID;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = ChallanStatus.UNPAID;
        }
        this.createdAt = LocalDateTime.now();
    }

    public Challan() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ViolationType getViolationType() {
        return violationType;
    }

    public void setViolationType(ViolationType violationType) {
        this.violationType = violationType;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public LocalDate getViolationDate() {
        return violationDate;
    }

    public void setViolationDate(LocalDate violationDate) {
        this.violationDate = violationDate;
    }

    public BigDecimal getOriginalFine() {
        return originalFine;
    }

    public void setOriginalFine(BigDecimal originalFine) {
        this.originalFine = originalFine;
    }

    public BigDecimal getApplicableFine() {
        return applicableFine;
    }

    public void setApplicableFine(BigDecimal applicableFine) {
        this.applicableFine = applicableFine;
    }

    public ChallanStatus getStatus() {
        return status;
    }

    public void setStatus(ChallanStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
