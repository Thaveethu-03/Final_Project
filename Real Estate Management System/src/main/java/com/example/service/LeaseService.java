package com.example.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.example.dto.LeaseRequestDTO;
import com.example.dto.LeaseResponseDTO;
import com.example.dto.LeaseUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.enums.LeaseStatus;

public interface LeaseService {

    LeaseResponseDTO createLease(LeaseRequestDTO dto);

    LeaseResponseDTO getLeaseById(Long id);

    /** All filters are optional. */
    PageResponseDTO<LeaseResponseDTO> getAllLeases(LeaseStatus status, Long propertyId, Long tenantId,
            Pageable pageable);

    LeaseResponseDTO updateLease(Long id, LeaseUpdateRequestDTO dto);

    LeaseResponseDTO terminateLease(Long id);

    /** ACTIVE leases that end within the next given days. */
    List<LeaseResponseDTO> getExpiringLeases(int days);

    /** ACTIVE leases whose end date is over become EXPIRED. Returns how many were expired. */
    int expireOverdueLeases();
}
