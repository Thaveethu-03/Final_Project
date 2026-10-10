package com.example.dto;

import java.math.BigDecimal;

import com.example.enums.MaintenanceStatus;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceStatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private MaintenanceStatus status;

    @PositiveOrZero(message = "Cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Cost can have max 10 digits and 2 decimals")
    private BigDecimal cost;
}
