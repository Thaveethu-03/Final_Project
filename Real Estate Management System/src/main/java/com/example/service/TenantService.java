package com.example.service;

import org.springframework.data.domain.Pageable;

import com.example.dto.PageResponseDTO;
import com.example.dto.TenantRequestDTO;
import com.example.dto.TenantResponseDTO;

public interface TenantService {

    TenantResponseDTO createTenant(TenantRequestDTO dto);

    TenantResponseDTO getTenantById(Long id);

    /** name is optional. If given, only tenants whose name contains it are returned. */
    PageResponseDTO<TenantResponseDTO> getAllTenants(String name, Pageable pageable);

    TenantResponseDTO updateTenant(Long id, TenantRequestDTO dto);

    void deleteTenant(Long id);
}
