package com.example.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.MaintenanceCreateRequestDTO;
import com.example.dto.MaintenanceResponseDTO;
import com.example.dto.MaintenanceStatusUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.entity.MaintenanceRequest;
import com.example.entity.Property;
import com.example.entity.Tenant;
import com.example.enums.LeaseStatus;
import com.example.enums.MaintenancePriority;
import com.example.enums.MaintenanceStatus;
import com.example.exception.BusinessRuleException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.MaintenanceRequestMapper;
import com.example.repository.LeaseRepository;
import com.example.repository.MaintenanceRequestRepository;
import com.example.repository.PropertyRepository;
import com.example.repository.TenantRepository;
import com.example.service.MaintenanceRequestService;
import com.example.specification.MaintenanceRequestSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService 
{

    private final MaintenanceRequestRepository requestRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LeaseRepository leaseRepository;
    private final MaintenanceRequestMapper requestMapper;

    @Override
    @Transactional
    public MaintenanceResponseDTO createRequest(MaintenanceCreateRequestDTO dto) 
    {
        log.info("Creating maintenance request '{}' for propertyId={}", 
        			dto.getTitle(), 
        			dto.getPropertyId());

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException
                		("Property not found with id: " + 
                				dto.getPropertyId()));

        Tenant tenant = null;
        if (dto.getTenantId() != null) {
            tenant = tenantRepository.findById(dto.getTenantId())
                    .orElseThrow(() -> new ResourceNotFoundException
                    		("Tenant not found with id: " + 
                    				dto.getTenantId()));

            boolean hasActiveLease = leaseRepository.existsByTenantIdAndPropertyIdAndStatus(
            		    tenant.getId(),
                    property.getId(), 
                    LeaseStatus.ACTIVE);
            
            if (!hasActiveLease) 
            {
                throw new BusinessRuleException("Tenant has no active lease on this property");
            }
        }

        MaintenanceRequest request = requestMapper.toEntity(dto);
        request.setProperty(property);
        request.setTenant(tenant);
        request.setStatus(MaintenanceStatus.OPEN);

        MaintenanceRequest saved = requestRepository.save(request);
        log.info("Maintenance request created with id={}", saved.getId());
        return requestMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponseDTO getRequestById(Long id) 
    {
        return requestMapper.toResponseDTO(findRequest(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MaintenanceResponseDTO> getAllRequests(
    			MaintenanceStatus status,
            MaintenancePriority priority, 
            Long propertyId, 
            Pageable pageable) 
    {
        Page<MaintenanceRequest> page = requestRepository
                .findAll(MaintenanceRequestSpecification
                	.withFilters(
                	status, 
                	priority, 
                	propertyId), 
                	pageable);
        
        return PageResponseDTO.of(page.map(requestMapper::toResponseDTO));
    }

    /**
     * Allowed status flow:
     * OPEN -> IN_PROGRESS or CANCELLED, IN_PROGRESS -> COMPLETED or CANCELLED.
     * COMPLETED and CANCELLED are final.
     */
    @Override
    @Transactional
    public MaintenanceResponseDTO updateStatus(Long id, MaintenanceStatusUpdateRequestDTO dto) 
    {
        log.info("Changing maintenance request id={} to {}", id, dto.getStatus());

        MaintenanceRequest request = findRequest(id);

        if (!isValidTransition(request.getStatus(), dto.getStatus())) 
        {
            		 throw new BusinessRuleException(
                 "Cannot change status from " + 
            		 request.getStatus() + " to " + 
                 dto.getStatus());
        }

        request.setStatus(dto.getStatus());
        if (dto.getCost() != null)
        {
            request.setCost(dto.getCost());
        }
        if (dto.getStatus() == MaintenanceStatus.COMPLETED) 
        {
            request.setCompletedAt(LocalDateTime.now());
        }

        return requestMapper.toResponseDTO(requestRepository.save(request));
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) 
    {
        MaintenanceRequest request = findRequest(id);

        if (request.getStatus() != MaintenanceStatus.OPEN) 
        {
            throw new BusinessRuleException("Only OPEN request can be deleted. Current status: " + request.getStatus());
        }

        requestRepository.delete(request);
        log.info("Maintenance request deleted with id={}", id);
    }

    // ---------- private helpers ----------

    private boolean isValidTransition(MaintenanceStatus from, MaintenanceStatus to) 
    {
        return switch (from) 
        		{
            case OPEN -> to == MaintenanceStatus.IN_PROGRESS || to == MaintenanceStatus.CANCELLED;
            case IN_PROGRESS -> to == MaintenanceStatus.COMPLETED || to == MaintenanceStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private MaintenanceRequest findRequest(Long id) 
    {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
    }
}
