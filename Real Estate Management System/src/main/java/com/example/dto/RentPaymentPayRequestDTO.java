package com.example.dto;

import java.time.LocalDate;

import com.example.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentPaymentPayRequestDTO 
{

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // optional: if not given, today is used
    private LocalDate paidDate;
}
