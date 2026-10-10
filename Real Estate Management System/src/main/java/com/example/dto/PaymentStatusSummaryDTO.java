package com.example.dto;

import java.math.BigDecimal;

import com.example.enums.PaymentStatus;

/** Result of the JPQL group-by query: total amount and count per payment status. */
public record PaymentStatusSummaryDTO(PaymentStatus status, BigDecimal totalAmount, Long count) {
}
