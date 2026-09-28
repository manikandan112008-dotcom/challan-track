package com.example.challantrack.service;

import com.example.challantrack.dto.PaymentRequestDTO;
import com.example.challantrack.dto.PaymentResponseDTO;
import com.example.challantrack.entity.Challan;
import com.example.challantrack.entity.ChallanStatus;
import com.example.challantrack.entity.PaymentRecord;
import com.example.challantrack.exception.DuplicateResourceException;
import com.example.challantrack.exception.InvalidPaymentException;
import com.example.challantrack.exception.ResourceNotFoundException;
import com.example.challantrack.repository.ChallanRepository;
import com.example.challantrack.repository.PaymentRecordRepository;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final ChallanRepository challanRepository;

    public PaymentService(PaymentRecordRepository paymentRecordRepository, ChallanRepository challanRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.challanRepository = challanRepository;
    }

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        Challan challan = challanRepository.findById(request.getChallanId())
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + request.getChallanId()));

        validatePayment(challan, request);

        if (paymentRecordRepository.existsByPaymentReference(request.getPaymentReference())) {
            throw new DuplicateResourceException("Payment reference already exists");
        }

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setChallan(challan);
        paymentRecord.setPaymentReference(request.getPaymentReference());
        paymentRecord.setAmount(request.getAmount());
        paymentRecord.setPaymentMethod(request.getPaymentMethod());
        paymentRecord.setPaymentDate(request.getPaymentDate());

        PaymentRecord saved = paymentRecordRepository.save(paymentRecord);
        updateChallanStatus(challan.getId(), ChallanStatus.PAID);

        return new PaymentResponseDTO(
                saved.getId(),
                challan.getId(),
                challan.getChallanNumber(),
                saved.getPaymentReference(),
                saved.getAmount(),
                saved.getPaymentMethod(),
                saved.getPaymentDate(),
                saved.getCreatedAt()
        );
    }

    public void validatePayment(Challan challan, PaymentRequestDTO request) {
        if (challan.getStatus() == ChallanStatus.PAID) {
            throw new InvalidPaymentException("Challan is already paid");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(challan.getApplicableFine()) != 0) {
            throw new InvalidPaymentException("Payment amount must match the challan applicable fine");
        }

        if (request.getPaymentReference() == null || request.getPaymentReference().isBlank()) {
            throw new InvalidPaymentException("Payment reference cannot be blank");
        }

        if (request.getPaymentDate() == null) {
            throw new InvalidPaymentException("Payment date is required");
        }

        if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
            throw new InvalidPaymentException("Payment method cannot be blank");
        }

        if (paymentRecordRepository.existsByPaymentReference(request.getPaymentReference())) {
            throw new DuplicateResourceException("Payment reference already exists");
        }
    }

    @Transactional
    public void updateChallanStatus(Long challanId, ChallanStatus status) {
        Challan challan = challanRepository.findById(challanId)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + challanId));
        challan.setStatus(status);
        challanRepository.save(challan);
    }
}
