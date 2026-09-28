package com.example.challantrack.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long id;
    private Long challanId;
    private String challanNumber;
    private String paymentReference;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(Long id, Long challanId, String challanNumber, String paymentReference,
                            BigDecimal amount, String paymentMethod, LocalDate paymentDate, LocalDateTime createdAt) {
        this.id = id;
        this.challanId = challanId;
        this.challanNumber = challanNumber;
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallanId() {
        return challanId;
    }

    public void setChallanId(Long challanId) {
        this.challanId = challanId;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
