package com.example.challantrack.controller;

import com.example.challantrack.dto.ViolationTypeRequestDTO;
import com.example.challantrack.dto.ViolationTypeResponseDTO;
import com.example.challantrack.service.ViolationTypeService;
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
@Tag(name = "Violation Type API", description = "Violation type management endpoints")
public class ViolationTypeController {

    private final ViolationTypeService violationTypeService;

    public ViolationTypeController(ViolationTypeService violationTypeService) {
        this.violationTypeService = violationTypeService;
    }

    @Operation(summary = "Create a violation type")
    @PostMapping("/violation-types")
    public ResponseEntity<ViolationTypeResponseDTO> createViolationType(@Valid @RequestBody ViolationTypeRequestDTO request) {
        return new ResponseEntity<>(violationTypeService.createViolationType(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all violation types")
    @GetMapping("/violation-types")
    public ResponseEntity<List<ViolationTypeResponseDTO>> getAllViolationTypes() {
        return ResponseEntity.ok(violationTypeService.getAllViolationTypes());
    }

    @Operation(summary = "Get a violation type by id")
    @GetMapping("/violation-types/{id}")
    public ResponseEntity<ViolationTypeResponseDTO> getViolationTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(violationTypeService.getViolationTypeById(id));
    }

    @Operation(summary = "Update a violation type")
    @PutMapping("/violation-types/{id}")
    public ResponseEntity<ViolationTypeResponseDTO> updateViolationType(@PathVariable Long id, @Valid @RequestBody ViolationTypeRequestDTO request) {
        return ResponseEntity.ok(violationTypeService.updateViolationType(id, request));
    }

    @Operation(summary = "Delete a violation type")
    @DeleteMapping("/violation-types/{id}")
    public ResponseEntity<Void> deleteViolationType(@PathVariable Long id) {
        violationTypeService.deleteViolationType(id);
        return ResponseEntity.noContent().build();
    }
}
