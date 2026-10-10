package com.example.dto;

import java.math.BigDecimal;

import com.example.enums.PropertyType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PropertyRequestDTO {

    @NotNull(message = "Owner id is required")
    private Long ownerId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotNull(message = "Bedrooms is required")
    @Min(value = 0, message = "Bedrooms cannot be negative")
    private Integer bedrooms;

    @NotNull(message = "Bathrooms is required")
    @Min(value = 0, message = "Bathrooms cannot be negative")
    private Integer bathrooms;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be greater than 0")
    private Double areaSqft;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Monthly rent can have max 10 digits and 2 decimals")
    private BigDecimal monthlyRent;
}
