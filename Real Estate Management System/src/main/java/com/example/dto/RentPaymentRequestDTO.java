package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentPaymentRequestDTO 
{

    @NotNull(message = "Lease id is required")
    private Long leaseId;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    // optional: if not given, the lease's monthly rent is used
    @Positive(message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount can have max 10 digits and 2 decimals")
    private BigDecimal amount;
}
