package com.example.challantrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VehicleRequestDTO {

    @NotBlank(message = "Vehicle number cannot be blank")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", message = "Vehicle number format is invalid")
    private String vehicleNumber;

    @NotBlank(message = "Owner name cannot be blank")
    private String ownerName;

    @NotBlank(message = "Vehicle type cannot be blank")
    private String vehicleType;

    @NotBlank(message = "Owner phone cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Owner phone must be a valid 10-digit number")
    private String ownerPhone;

    public VehicleRequestDTO() {
    }

    public VehicleRequestDTO(String vehicleNumber, String ownerName, String vehicleType, String ownerPhone) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.ownerPhone = ownerPhone;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }
}
