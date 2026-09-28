package com.example.challantrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentRequestDTO {

    @NotNull(message = "Challan ID is required")
    private Long challanId;

    @NotBlank(message = "Payment reference cannot be blank")
    private String paymentReference;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Long challanId, String paymentReference, BigDecimal amount, String paymentMethod, LocalDate paymentDate) {
        this.challanId = challanId;
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public Long getChallanId() {
        return challanId;
    }

    public void setChallanId(Long challanId) {
        this.challanId = challanId;
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
}
