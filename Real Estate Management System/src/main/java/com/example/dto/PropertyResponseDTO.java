package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.enums.PropertyStatus;
import com.example.enums.PropertyType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyResponseDTO {

    private Long id;
    private Long ownerId;
    private String ownerName;
    private String title;
    private String description;
    private PropertyType propertyType;
    private PropertyStatus status;
    private String address;
    private String city;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double areaSqft;
    private BigDecimal monthlyRent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
