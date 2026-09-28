package com.example.challantrack.service;

import com.example.challantrack.dto.ChallanRequestDTO;
import com.example.challantrack.dto.ChallanResponseDTO;
import com.example.challantrack.entity.Challan;
import com.example.challantrack.entity.ChallanStatus;
import com.example.challantrack.entity.Vehicle;
import com.example.challantrack.entity.ViolationType;
import com.example.challantrack.exception.InvalidChallanException;
import com.example.challantrack.exception.ResourceNotFoundException;
import com.example.challantrack.repository.ChallanRepository;
import com.example.challantrack.repository.PaymentRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallanService {

    private final ChallanRepository challanRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final VehicleService vehicleService;
    private final ViolationTypeService violationTypeService;

    public ChallanService(ChallanRepository challanRepository,
                         PaymentRecordRepository paymentRecordRepository,
                         VehicleService vehicleService,
                         ViolationTypeService violationTypeService) {
        this.challanRepository = challanRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.vehicleService = vehicleService;
        this.violationTypeService = violationTypeService;
    }

    @Transactional
    public ChallanResponseDTO issueChallan(ChallanRequestDTO request) {
        Vehicle vehicle = vehicleService.findVehicleEntityById(request.getVehicleId());
        ViolationType violationType = violationTypeService.findViolationTypeEntityById(request.getViolationTypeId());

        int previousOccurrences = calculatePreviousOccurrences(vehicle, violationType, request.getViolationDate());
        BigDecimal applicableFine = calculateApplicableFine(violationType.getBaseFine(), previousOccurrences);
        BigDecimal originalFine = violationType.getBaseFine();

        Challan challan = new Challan();
        challan.setVehicle(vehicle);
        challan.setViolationType(violationType);
        challan.setOfficerName(request.getOfficerName());
        challan.setViolationDate(request.getViolationDate());
        challan.setOriginalFine(originalFine);
        challan.setApplicableFine(applicableFine);
        challan.setStatus(ChallanStatus.UNPAID);
        challan.setChallanNumber(generateChallanNumber());

        Challan saved = challanRepository.save(challan);
        return mapToResponse(saved, false);
    }

    public BigDecimal calculateApplicableFine(BigDecimal baseFine, int previousOccurrences) {
        if (baseFine == null || baseFine.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidChallanException("Base fine must be greater than zero");
        }
        return baseFine.multiply(BigDecimal.valueOf(previousOccurrences + 1L));
    }

    public List<ChallanResponseDTO> getVehicleChallanHistory(Long vehicleId) {
        Vehicle vehicle = vehicleService.findVehicleEntityById(vehicleId);
        return challanRepository.findByVehicleOrderByViolationDateDesc(vehicle).stream()
                .map(challan -> mapToResponse(challan, paymentRecordRepository.findByChallan(challan).isPresent()))
                .collect(Collectors.toList());
    }

    public List<RepeatViolationDTO> getRepeatViolations(Long vehicleId) {
        Vehicle vehicle = vehicleService.findVehicleEntityById(vehicleId);
        List<Challan> challans = challanRepository.findByVehicleOrderByViolationDateDesc(vehicle);

        List<RepeatViolationDTO> result = new ArrayList<>();
        for (Challan challan : challans) {
            List<Challan> recentSameType = challanRepository.findRecentByVehicleAndViolationType(
                    vehicle,
                    challan.getViolationType(),
                    challan.getViolationDate().minusYears(1)
            );

            long occurrences = recentSameType.size();
            if (occurrences > 0) {
                result.add(new RepeatViolationDTO(
                        vehicle.getVehicleNumber(),
                        challan.getViolationType().getViolationName(),
                        Math.toIntExact(occurrences),
                        challan.getApplicableFine()
                ));
            }
        }

        result.sort(Comparator.comparing(RepeatViolationDTO::getViolationType));
        return result.stream().distinct().collect(Collectors.toList());
    }

    public List<UnpaidChallanDTO> getOldUnpaidChallans() {
        LocalDate threshold = LocalDate.now().minusDays(30);
        List<Challan> challans = challanRepository.findAllUnpaidOlderThan(ChallanStatus.UNPAID, threshold);
        return challans.stream()
                .map(challan -> {
                    long unpaidDays = Period.between(challan.getViolationDate(), LocalDate.now()).getDays();
                    return new UnpaidChallanDTO(
                            challan.getChallanNumber(),
                            challan.getVehicle().getVehicleNumber(),
                            challan.getVehicle().getOwnerName(),
                            challan.getViolationType().getViolationName(),
                            challan.getApplicableFine(),
                            challan.getViolationDate(),
                            unpaidDays
                    );
                })
                .collect(Collectors.toList());
    }

    private int calculatePreviousOccurrences(Vehicle vehicle, ViolationType violationType, LocalDate currentDate) {
        LocalDate oneYearAgo = currentDate.minusYears(1);
        List<Challan> sameVehicleTypeChallans = challanRepository.findRecentByVehicleAndViolationType(
                vehicle,
                violationType,
                oneYearAgo
        );
        return sameVehicleTypeChallans.size();
    }

    private String generateChallanNumber() {
        String prefix = "CH-" + LocalDate.now();
        long count = challanRepository.count() + 1;
        return prefix.replace("-", "") + "-" + String.format("%04d", count);
    }

    private ChallanResponseDTO mapToResponse(Challan challan, boolean paid) {
        return new ChallanResponseDTO(
                challan.getId(),
                challan.getChallanNumber(),
                challan.getVehicle().getVehicleNumber(),
                challan.getViolationType().getViolationName(),
                challan.getOfficerName(),
                challan.getViolationDate(),
                challan.getOriginalFine(),
                challan.getApplicableFine(),
                challan.getStatus(),
                paid ? "PAID" : "UNPAID"
        );
    }

    public static class RepeatViolationDTO {
        private final String vehicleNumber;
        private final String violationType;
        private final int occurrences;
        private final BigDecimal latestFine;

        public RepeatViolationDTO(String vehicleNumber, String violationType, int occurrences, BigDecimal latestFine) {
            this.vehicleNumber = vehicleNumber;
            this.violationType = violationType;
            this.occurrences = occurrences;
            this.latestFine = latestFine;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public String getViolationType() {
            return violationType;
        }

        public int getOccurrences() {
            return occurrences;
        }

        public BigDecimal getLatestFine() {
            return latestFine;
        }
    }

    public static class UnpaidChallanDTO {
        private final String challanNumber;
        private final String vehicleNumber;
        private final String ownerName;
        private final String violationType;
        private final BigDecimal applicableFine;
        private final LocalDate violationDate;
        private final long unpaidDays;

        public UnpaidChallanDTO(String challanNumber, String vehicleNumber, String ownerName,
                               String violationType, BigDecimal applicableFine,
                               LocalDate violationDate, long unpaidDays) {
            this.challanNumber = challanNumber;
            this.vehicleNumber = vehicleNumber;
            this.ownerName = ownerName;
            this.violationType = violationType;
            this.applicableFine = applicableFine;
            this.violationDate = violationDate;
            this.unpaidDays = unpaidDays;
        }

        public String getChallanNumber() {
            return challanNumber;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getViolationType() {
            return violationType;
        }

        public BigDecimal getApplicableFine() {
            return applicableFine;
        }

        public LocalDate getViolationDate() {
            return violationDate;
        }

        public long getUnpaidDays() {
            return unpaidDays;
        }
    }
}
