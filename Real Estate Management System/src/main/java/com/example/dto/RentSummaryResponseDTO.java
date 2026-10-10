package com.example.dto;

import java.math.BigDecimal;

/** Rent summary report for one lease. */
public record RentSummaryResponseDTO(
        Long leaseId,
        BigDecimal totalPaid,
        BigDecimal totalPending,
        BigDecimal totalOverdue,
        long paidCount,
        long pendingCount,
        long overdueCount) 
{
	
}
