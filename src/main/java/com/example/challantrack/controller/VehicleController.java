package com.example.challantrack.controller;

import com.example.challantrack.dto.VehicleRequestDTO;
import com.example.challantrack.dto.VehicleResponseDTO;
import com.example.challantrack.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Vehicle API", description = "Vehicle management endpoints")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(summary = "Create a vehicle")
    @PostMapping("/vehicles")
    public ResponseEntity<VehicleResponseDTO> createVehicle(@Valid @RequestBody VehicleRequestDTO request) {
        return new ResponseEntity<>(vehicleService.createVehicle(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all vehicles")
    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @Operation(summary = "Get a vehicle by id")
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @Operation(summary = "Update a vehicle")
    @PutMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDTO request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, request));
    }

    @Operation(summary = "Delete a vehicle")
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
