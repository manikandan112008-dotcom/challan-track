package com.example.challantrack.repository;

import com.example.challantrack.entity.Challan;
import com.example.challantrack.entity.ChallanStatus;
import com.example.challantrack.entity.Vehicle;
import com.example.challantrack.entity.ViolationType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallanRepository extends JpaRepository<Challan, Long> {

    List<Challan> findByVehicleOrderByViolationDateDesc(Vehicle vehicle);

    List<Challan> findByVehicleAndViolationTypeOrderByViolationDateDesc(Vehicle vehicle, ViolationType violationType);

    List<Challan> findByStatus(ChallanStatus status);

    boolean existsByChallanNumber(String challanNumber);

    @Query("SELECT c FROM Challan c WHERE c.vehicle = :vehicle AND c.violationType = :violationType AND c.violationDate >= :fromDate ORDER BY c.violationDate DESC")
    List<Challan> findRecentByVehicleAndViolationType(@Param("vehicle") Vehicle vehicle,
                                                     @Param("violationType") ViolationType violationType,
                                                     @Param("fromDate") LocalDate fromDate);

    @Query("SELECT c FROM Challan c WHERE c.vehicle = :vehicle AND c.status = :status AND c.violationDate < :thresholdDate ORDER BY c.violationDate ASC")
    List<Challan> findUnpaidOlderThan(@Param("vehicle") Vehicle vehicle,
                                      @Param("status") ChallanStatus status,
                                      @Param("thresholdDate") LocalDate thresholdDate);

    @Query("SELECT c FROM Challan c WHERE c.status = :status AND c.violationDate < :thresholdDate ORDER BY c.violationDate ASC")
    List<Challan> findAllUnpaidOlderThan(@Param("status") ChallanStatus status,
                                         @Param("thresholdDate") LocalDate thresholdDate);
}
