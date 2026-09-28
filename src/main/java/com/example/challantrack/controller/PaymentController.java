package com.example.challantrack.controller;

import com.example.challantrack.dto.PaymentRequestDTO;
import com.example.challantrack.dto.PaymentResponseDTO;
import com.example.challantrack.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Payment API", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create a payment against a challan")
    @PostMapping("/payments")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO request) {
        return new ResponseEntity<>(paymentService.createPayment(request), HttpStatus.CREATED);
    }
}
