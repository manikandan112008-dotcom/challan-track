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
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle", uniqueConstraints = @UniqueConstraint(columnNames = "vehicle_number"))
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vehicle number cannot be blank")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$", message = "Vehicle number format is invalid")
    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @NotBlank(message = "Owner name cannot be blank")
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @NotBlank(message = "Vehicle type cannot be blank")
    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType;

    @NotBlank(message = "Owner phone cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Owner phone must be a valid 10-digit number")
    @Column(name = "owner_phone", nullable = false, length = 15)
    private String ownerPhone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Vehicle() {
    }

    public Vehicle(String vehicleNumber, String ownerName, String vehicleType, String ownerPhone) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
        this.ownerPhone = ownerPhone;
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
