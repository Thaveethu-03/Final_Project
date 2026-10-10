package com.example.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.PageResponseDTO;
import com.example.dto.TenantRequestDTO;
import com.example.dto.TenantResponseDTO;
import com.example.entity.Tenant;
import com.example.exception.BusinessRuleException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.TenantMapper;
import com.example.repository.LeaseRepository;
import com.example.repository.MaintenanceRequestRepository;
import com.example.repository.TenantRepository;
import com.example.service.TenantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final TenantMapper tenantMapper;

    @Override
    @Transactional
    public TenantResponseDTO createTenant(TenantRequestDTO dto) {
        log.info("Creating tenant with email={}", dto.getEmail());

        if (tenantRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Tenant already exists with email: " + dto.getEmail());
        }
        if (tenantRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException("Tenant already exists with phone: " + dto.getPhone());
        }

        Tenant saved = tenantRepository.save(tenantMapper.toEntity(dto));
        log.info("Tenant created with id={}", saved.getId());
        return tenantMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponseDTO getTenantById(Long id) {
        return tenantMapper.toResponseDTO(findTenant(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<TenantResponseDTO> getAllTenants(String name, Pageable pageable) {
        Page<Tenant> page;
        if (name == null || name.isBlank()) {
            page = tenantRepository.findAll(pageable);
        } else {
            page = tenantRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }
        return PageResponseDTO.of(page.map(tenantMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public TenantResponseDTO updateTenant(Long id, TenantRequestDTO dto) {
        log.info("Updating tenant id={}", id);

        Tenant tenant = findTenant(id);

        if (tenantRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("Another tenant already uses email: " + dto.getEmail());
        }
        if (tenantRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new DuplicateResourceException("Another tenant already uses phone: " + dto.getPhone());
        }

        tenantMapper.updateEntity(dto, tenant);
        return tenantMapper.toResponseDTO(tenantRepository.save(tenant));
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = findTenant(id);

        if (leaseRepository.existsByTenantId(id) || maintenanceRequestRepository.existsByTenantId(id)) {
            throw new BusinessRuleException(
                    "Tenant " + id + " cannot be deleted because the tenant has lease or maintenance records");
        }

        tenantRepository.delete(tenant);
        log.info("Tenant deleted with id={}", id);
    }

    private Tenant findTenant(Long id) 
    {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }
}
