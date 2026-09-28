package com.example.challantrack.dto;

import java.time.LocalDateTime;

public class VehicleResponseDTO {

    private Long id;
    private String vehicleNumber;
    private String ownerName;
    private String vehicleType;
    private String ownerPhone;
    private LocalDateTime createdAt;

    public VehicleResponseDTO() {
    }

    public VehicleResponseDTO(Long id, String vehicleNumber, String ownerName, String vehicleType, String ownerPhone, LocalDateTime createdAt) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.ownerPhone = ownerPhone;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
