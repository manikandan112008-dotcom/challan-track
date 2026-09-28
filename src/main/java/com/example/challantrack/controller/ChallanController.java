package com.example.challantrack.controller;

import com.example.challantrack.dto.ChallanRequestDTO;
import com.example.challantrack.dto.ChallanResponseDTO;
import com.example.challantrack.service.ChallanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Challan API", description = "Challan issue and tracking endpoints")
public class ChallanController {

    private final ChallanService challanService;

    public ChallanController(ChallanService challanService) {
        this.challanService = challanService;
    }

    @Operation(summary = "Issue a new challan")
    @PostMapping("/challans")
    public ResponseEntity<ChallanResponseDTO> issueChallan(@Valid @RequestBody ChallanRequestDTO request) {
        return new ResponseEntity<>(challanService.issueChallan(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all challans for a vehicle")
    @GetMapping("/vehicles/{vehicleId}/challans")
    public ResponseEntity<List<ChallanResponseDTO>> getVehicleChallanHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(challanService.getVehicleChallanHistory(vehicleId));
    }

    @Operation(summary = "Get repeat violations for a vehicle")
    @GetMapping("/vehicles/{vehicleId}/repeat-violations")
    public ResponseEntity<List<ChallanService.RepeatViolationDTO>> getRepeatViolations(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(challanService.getRepeatViolations(vehicleId));
    }

    @Operation(summary = "Get unpaid challans older than 30 days")
    @GetMapping("/challans/unpaid/older-than-30-days")
    public ResponseEntity<List<ChallanService.UnpaidChallanDTO>> getOldUnpaidChallans() {
        return ResponseEntity.ok(challanService.getOldUnpaidChallans());
    }
}
