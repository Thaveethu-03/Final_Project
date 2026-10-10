package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaseRequestDTO {

    @NotNull(message = "Property id is required")
    private Long propertyId;

    @NotNull(message = "Tenant id is required")
    private Long tenantId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    // optional: if not given, the property's monthly rent is used
    @Positive(message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Monthly rent can have max 10 digits and 2 decimals")
    private BigDecimal monthlyRent;

    @NotNull(message = "Security deposit is required")
    @PositiveOrZero(message = "Security deposit cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Security deposit can have max 10 digits and 2 decimals")
    private BigDecimal securityDeposit;
}
