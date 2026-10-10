package com.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.enums.LeaseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaseResponseDTO {

	    private Long id;
	    private Long propertyId;
	    private String propertyTitle;
	    private Long tenantId;
	    private String tenantName;
	    private LocalDate startDate;
	    private LocalDate endDate;
	    private BigDecimal monthlyRent;
	    private BigDecimal securityDeposit;
	    private LeaseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
