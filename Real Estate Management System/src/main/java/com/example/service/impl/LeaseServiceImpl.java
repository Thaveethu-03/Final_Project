package com.example.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.LeaseRequestDTO;
import com.example.dto.LeaseResponseDTO;
import com.example.dto.LeaseUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.entity.Lease;
import com.example.entity.Property;
import com.example.entity.Tenant;
import com.example.enums.LeaseStatus;
import com.example.enums.PropertyStatus;
import com.example.exception.BusinessRuleException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.LeaseMapper;
import com.example.repository.LeaseRepository;
import com.example.repository.PropertyRepository;
import com.example.repository.TenantRepository;
import com.example.service.LeaseService;
import com.example.service.PropertyService;
import com.example.specification.LeaseSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {

    // used when we are creating a new lease (no existing lease id to exclude)
    private static final Long NO_LEASE = 0L;

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final PropertyService propertyService;
    private final LeaseMapper leaseMapper;

    /**
     * One transaction: lease is saved AND property becomes RENTED.
     * If anything fails, both are rolled back.
     */
    @Override
    @Transactional
    public LeaseResponseDTO createLease(LeaseRequestDTO dto) {
        log.info("Creating lease for propertyId={}, tenantId={}", dto.getPropertyId(), dto.getTenantId());

        Property property = findProperty(dto.getPropertyId());
        Tenant tenant = findTenant(dto.getTenantId());

        validateDates(dto.getStartDate(), dto.getEndDate());

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BusinessRuleException("Property is not available. Current status: " + property.getStatus());
        }

        long overlaps = leaseRepository.countOverlappingLeases(property.getId(), LeaseStatus.ACTIVE,
                dto.getStartDate(), dto.getEndDate(), NO_LEASE);
        if (overlaps > 0) {
            throw new BusinessRuleException("Property already has an active lease in the given period");
        }

        Lease lease = leaseMapper.toEntity(dto);
        lease.setProperty(property);
        lease.setTenant(tenant);
        lease.setStatus(LeaseStatus.ACTIVE);
        if (lease.getMonthlyRent() == null) {
            lease.setMonthlyRent(property.getMonthlyRent());
        }

        Lease saved = leaseRepository.save(lease);

        // goes through PropertyService so the property cache is cleared
        propertyService.updatePropertyStatus(property.getId(), PropertyStatus.RENTED);

        log.info("Lease created with id={}", saved.getId());
        return leaseMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaseResponseDTO getLeaseById(Long id) {
        return leaseMapper.toResponseDTO(findLease(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<LeaseResponseDTO> getAllLeases(LeaseStatus status, Long propertyId, Long tenantId,
            Pageable pageable) {
        Page<Lease> page = leaseRepository.findAll(LeaseSpecification.withFilters(status, propertyId, tenantId),
                pageable);
        return PageResponseDTO.of(page.map(leaseMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public LeaseResponseDTO updateLease(Long id, LeaseUpdateRequestDTO dto) {
        log.info("Updating lease id={}", id);

        Lease lease = findLease(id);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE lease can be updated. Current status: " + lease.getStatus());
        }
        validateDates(lease.getStartDate(), dto.getEndDate());

        long overlaps = leaseRepository.countOverlappingLeases(lease.getProperty().getId(), LeaseStatus.ACTIVE,
                lease.getStartDate(), dto.getEndDate(), lease.getId());
        if (overlaps > 0) {
            throw new BusinessRuleException("New end date overlaps with another active lease of this property");
        }

        lease.setEndDate(dto.getEndDate());
        lease.setMonthlyRent(dto.getMonthlyRent());
        lease.setSecurityDeposit(dto.getSecurityDeposit());

        return leaseMapper.toResponseDTO(leaseRepository.save(lease));
    }

    @Override
    @Transactional
    public LeaseResponseDTO terminateLease(Long id) {
        log.info("Terminating lease id={}", id);

        Lease lease = findLease(id);

        if (lease.getStatus() != LeaseStatus.ACTIVE) {
            throw new BusinessRuleException("Only ACTIVE lease can be terminated. Current status: " + lease.getStatus());
        }

        lease.setStatus(LeaseStatus.TERMINATED);
        Lease saved = leaseRepository.save(lease);

        propertyService.updatePropertyStatus(lease.getProperty().getId(), PropertyStatus.AVAILABLE);

        return leaseMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaseResponseDTO> getExpiringLeases(int days) {
        if (days < 1 || days > 730) {
            throw new BusinessRuleException("days must be between 1 and 730");
        }
        LocalDate today = LocalDate.now();
        return leaseRepository.findByStatusAndEndDateBetween(LeaseStatus.ACTIVE, today, today.plusDays(days))
                .stream()
                .map(leaseMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public int expireOverdueLeases() {
        List<Lease> leases = leaseRepository.findByStatusAndEndDateBefore(LeaseStatus.ACTIVE, LocalDate.now());

        for (Lease lease : leases) {
            lease.setStatus(LeaseStatus.EXPIRED);
            leaseRepository.save(lease);
            propertyService.updatePropertyStatus(lease.getProperty().getId(), PropertyStatus.AVAILABLE);
        }

        log.info("{} lease(s) expired", leases.size());
        return leases.size();
    }

    // ---------- private helpers ----------

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new BusinessRuleException("End date must be after start date");
        }
    }

    private Lease findLease(Long id) {
        return leaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found with id: " + id));
    }

    private Property findProperty(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    private Tenant findTenant(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }
}
