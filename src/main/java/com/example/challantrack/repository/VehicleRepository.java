package com.example.challantrack.repository;

import com.example.challantrack.entity.Vehicle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    boolean existsByVehicleNumber(String vehicleNumber);
}
