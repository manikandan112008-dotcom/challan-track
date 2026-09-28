package com.example.challantrack.service;

import com.example.challantrack.dto.VehicleRequestDTO;
import com.example.challantrack.dto.VehicleResponseDTO;
import com.example.challantrack.entity.Vehicle;
import com.example.challantrack.exception.DuplicateResourceException;
import com.example.challantrack.exception.ResourceNotFoundException;
import com.example.challantrack.repository.VehicleRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponseDTO createVehicle(VehicleRequestDTO request) {
        if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new DuplicateResourceException("Vehicle number already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setOwnerPhone(request.getOwnerPhone());

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    public List<VehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponseDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return mapToResponse(vehicle);
    }

    @Transactional
    public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (!vehicle.getVehicleNumber().equalsIgnoreCase(request.getVehicleNumber())
                && vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new DuplicateResourceException("Vehicle number already exists");
        }

        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setOwnerName(request.getOwnerName());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setOwnerPhone(request.getOwnerPhone());

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        vehicleRepository.delete(vehicle);
    }

    public Vehicle findVehicleEntityById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
    }

    private VehicleResponseDTO mapToResponse(Vehicle vehicle) {
        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getVehicleNumber(),
                vehicle.getOwnerName(),
                vehicle.getVehicleType(),
                vehicle.getOwnerPhone(),
                vehicle.getCreatedAt()
        );
    }
}
