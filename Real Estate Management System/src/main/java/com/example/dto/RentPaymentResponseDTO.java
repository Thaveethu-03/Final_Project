package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.enums.PaymentMethod;
import com.example.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentPaymentResponseDTO {

    private Long id;
    private Long leaseId;
    private String propertyTitle;
    private String tenantName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
