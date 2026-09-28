package com.example.challantrack.service;

import com.example.challantrack.dto.ViolationTypeRequestDTO;
import com.example.challantrack.dto.ViolationTypeResponseDTO;
import com.example.challantrack.entity.ViolationType;
import com.example.challantrack.exception.DuplicateResourceException;
import com.example.challantrack.exception.ResourceNotFoundException;
import com.example.challantrack.repository.ViolationTypeRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViolationTypeService {

    private final ViolationTypeRepository violationTypeRepository;

    public ViolationTypeService(ViolationTypeRepository violationTypeRepository) {
        this.violationTypeRepository = violationTypeRepository;
    }

    @Transactional
    public ViolationTypeResponseDTO createViolationType(ViolationTypeRequestDTO request) {
        if (violationTypeRepository.existsByViolationCode(request.getViolationCode())) {
            throw new DuplicateResourceException("Violation code already exists");
        }

        ViolationType violationType = new ViolationType();
        violationType.setViolationCode(request.getViolationCode());
        violationType.setViolationName(request.getViolationName());
        violationType.setDescription(request.getDescription());
        violationType.setBaseFine(request.getBaseFine());

        ViolationType saved = violationTypeRepository.save(violationType);
        return mapToResponse(saved);
    }

    public List<ViolationTypeResponseDTO> getAllViolationTypes() {
        return violationTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ViolationTypeResponseDTO getViolationTypeById(Long id) {
        ViolationType violationType = violationTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation type not found with id: " + id));
        return mapToResponse(violationType);
    }

    @Transactional
    public ViolationTypeResponseDTO updateViolationType(Long id, ViolationTypeRequestDTO request) {
        ViolationType violationType = violationTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation type not found with id: " + id));

        if (!violationType.getViolationCode().equalsIgnoreCase(request.getViolationCode())
                && violationTypeRepository.existsByViolationCode(request.getViolationCode())) {
            throw new DuplicateResourceException("Violation code already exists");
        }

        violationType.setViolationCode(request.getViolationCode());
        violationType.setViolationName(request.getViolationName());
        violationType.setDescription(request.getDescription());
        violationType.setBaseFine(request.getBaseFine());

        return mapToResponse(violationTypeRepository.save(violationType));
    }

    @Transactional
    public void deleteViolationType(Long id) {
        ViolationType violationType = violationTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Violation type not found with id: " + id));
        violationTypeRepository.delete(violationType);
    }

    public ViolationType findViolationTypeEntityById(Long violationTypeId) {
        return violationTypeRepository.findById(violationTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Violation type not found with id: " + violationTypeId));
    }

    private ViolationTypeResponseDTO mapToResponse(ViolationType violationType) {
        return new ViolationTypeResponseDTO(
                violationType.getId(),
                violationType.getViolationCode(),
                violationType.getViolationName(),
                violationType.getDescription(),
                violationType.getBaseFine(),
                violationType.getCreatedAt()
        );
    }
}
