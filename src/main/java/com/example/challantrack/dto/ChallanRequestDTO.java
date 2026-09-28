package com.example.challantrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ChallanRequestDTO {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Violation type ID is required")
    private Long violationTypeId;

    @NotBlank(message = "Officer name cannot be blank")
    private String officerName;

    @NotNull(message = "Violation date is required")
    private LocalDate violationDate;

    public ChallanRequestDTO() {
    }

    public ChallanRequestDTO(Long vehicleId, Long violationTypeId, String officerName, LocalDate violationDate) {
        this.vehicleId = vehicleId;
        this.violationTypeId = violationTypeId;
        this.officerName = officerName;
        this.violationDate = violationDate;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getViolationTypeId() {
        return violationTypeId;
    }

    public void setViolationTypeId(Long violationTypeId) {
        this.violationTypeId = violationTypeId;
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
}
