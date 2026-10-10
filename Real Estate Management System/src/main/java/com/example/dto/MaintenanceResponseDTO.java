package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.enums.MaintenancePriority;
import com.example.enums.MaintenanceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponseDTO 
{

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private Long tenantId;
    private String tenantName;
    private String title;
    private String description;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private BigDecimal cost;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
