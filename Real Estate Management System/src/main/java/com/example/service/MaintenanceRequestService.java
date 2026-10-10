package com.example.service;

import org.springframework.data.domain.Pageable;

import com.example.dto.MaintenanceCreateRequestDTO;
import com.example.dto.MaintenanceResponseDTO;
import com.example.dto.MaintenanceStatusUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.enums.MaintenancePriority;
import com.example.enums.MaintenanceStatus;

public interface MaintenanceRequestService 
{

    MaintenanceResponseDTO createRequest(MaintenanceCreateRequestDTO dto);

    MaintenanceResponseDTO getRequestById(Long id);

    PageResponseDTO<MaintenanceResponseDTO> getAllRequests(
    			MaintenanceStatus status, 
    			MaintenancePriority priority,
            Long propertyId, 
            Pageable pageable);

    MaintenanceResponseDTO updateStatus(Long id, MaintenanceStatusUpdateRequestDTO dto);

    void deleteRequest(Long id);
}
