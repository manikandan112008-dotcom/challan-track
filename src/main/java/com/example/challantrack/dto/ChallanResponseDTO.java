package com.example.challantrack.dto;

import com.example.challantrack.entity.ChallanStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ChallanResponseDTO {

    private Long id;
    private String challanNumber;
    private String vehicleNumber;
    private String violationType;
    private String officerName;
    private LocalDate violationDate;
    private BigDecimal originalFine;
    private BigDecimal applicableFine;
    private ChallanStatus status;
    private String paymentStatus;

    public ChallanResponseDTO() {
    }

    public ChallanResponseDTO(Long id, String challanNumber, String vehicleNumber, String violationType,
                            String officerName, LocalDate violationDate, BigDecimal originalFine,
                            BigDecimal applicableFine, ChallanStatus status, String paymentStatus) {
        this.id = id;
        this.challanNumber = challanNumber;
        this.vehicleNumber = vehicleNumber;
        this.violationType = violationType;
        this.officerName = officerName;
        this.violationDate = violationDate;
        this.originalFine = originalFine;
        this.applicableFine = applicableFine;
        this.status = status;
        this.paymentStatus = paymentStatus;
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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
