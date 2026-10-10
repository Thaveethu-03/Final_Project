package com.example.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.CityStatsDTO;
import com.example.dto.PageResponseDTO;
import com.example.dto.PropertyRequestDTO;
import com.example.dto.PropertyResponseDTO;
import com.example.entity.Owner;
import com.example.entity.Property;
import com.example.enums.PropertyStatus;
import com.example.enums.PropertyType;
import com.example.exception.BusinessRuleException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.PropertyMapper;
import com.example.repository.LeaseRepository;
import com.example.repository.MaintenanceRequestRepository;
import com.example.repository.OwnerRepository;
import com.example.repository.PropertyRepository;
import com.example.service.PropertyService;
import com.example.specification.PropertySpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService 
{

    private final PropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;
    private final LeaseRepository leaseRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final PropertyMapper propertyMapper;

    @Override
    @Transactional
    @CacheEvict(value = "cityStats", allEntries = true)
    public PropertyResponseDTO createProperty(PropertyRequestDTO dto) 
    {
        log.info("Creating property '{}' for ownerId={}", 
        				dto.getTitle(), 
        				dto.getOwnerId());

        Owner owner = findOwner(dto.getOwnerId());

        if (propertyRepository.existsByOwnerIdAndTitle(
        					dto.getOwnerId(), 
        					dto.getTitle())) 
        {
            throw new DuplicateResourceException("Owner already has a property with title: " + dto.getTitle());
        }

        Property property = propertyMapper.toEntity(dto);
        property.setOwner(owner);
        property.setStatus(PropertyStatus.AVAILABLE);

        Property saved = propertyRepository.save(property);
        log.info("Property created with id={}", saved.getId());
        return propertyMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "properties", key = "#id")
    public PropertyResponseDTO getPropertyById(Long id) 
    {
        log.debug("Loading property id={} from database", id);
        return propertyMapper
        			.toResponseDTO(findProperty(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PropertyResponseDTO> searchProperties(
    							String city, 
    							PropertyType propertyType,
    							PropertyStatus status, 
    							BigDecimal minRent, 
    							BigDecimal maxRent, 
    							Integer bedrooms, 
    							Pageable pageable) 
    {

        if (minRent != null && 
        		maxRent != null && 
        		minRent.compareTo(maxRent) > 0) 
        {
            throw new BusinessRuleException("minRent cannot be greater than maxRent");
        }

        Specification<Property> spec = PropertySpecification
        									.withFilters(
        									city, 
        									propertyType, 
        									status, minRent,
        									maxRent, 
        									bedrooms);
        
        Page<Property> page = propertyRepository
        							.findAll(spec, pageable);
        return PageResponseDTO.of(page.map(propertyMapper::toResponseDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PropertyResponseDTO> getPropertiesByOwner(Long ownerId, Pageable pageable) 
    {
        if (!ownerRepository.existsById(ownerId)) 
        {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        Page<Property> page = propertyRepository.findByOwnerId(ownerId, pageable);
        return PageResponseDTO.of(page.map(propertyMapper::toResponseDTO));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "properties", key = "#id"),
            @CacheEvict(value = "cityStats", allEntries = true) })
    public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO dto) 
    {
        log.info("Updating property id={}", id);

        Property property = findProperty(id);
        Owner owner = findOwner(dto.getOwnerId());

        if (propertyRepository.existsByOwnerIdAndTitleAndIdNot(dto.getOwnerId(), dto.getTitle(), id)) 
        {
            throw new DuplicateResourceException("Owner already has another property with title: " + dto.getTitle());
        }

        propertyMapper.updateEntity(dto, property);
        property.setOwner(owner);
        return propertyMapper.toResponseDTO(propertyRepository.save(property));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "properties", key = "#id"),
            @CacheEvict(value = "cityStats", allEntries = true) })
    public PropertyResponseDTO updatePropertyStatus(Long id, PropertyStatus status) 
    {
        log.info("Changing status of property id={} to {}", id, status);

        Property property = findProperty(id);
        property.setStatus(status);
        return propertyMapper.toResponseDTO(propertyRepository.save(property));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "properties", key = "#id"),
            @CacheEvict(value = "cityStats", allEntries = true) })
    public void deleteProperty(Long id) 
    {
        Property property = findProperty(id);

        if (property.getStatus() == PropertyStatus.RENTED) 
        {
            throw new BusinessRuleException("Rented property cannot be deleted. Change its status first.");
        }

        if (leaseRepository.existsByPropertyId(id) || maintenanceRequestRepository.existsByPropertyId(id)) {
            			throw new BusinessRuleException(
            					"Property " + id + " cannot be deleted because it has lease or maintenance records");
        }

        propertyRepository.delete(property);
        log.info("Property deleted with id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("cityStats")
    public List<CityStatsDTO> getCityStats() 
    {
        log.debug("Running city stats JPQL query");
        return propertyRepository.getCityStats();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStatusCounts() 
    {
        Map<String, Long> result = new LinkedHashMap<>();
        
        propertyRepository
        .countByStatusNative()
        .forEach(row -> result
        	.put(row.getStatus(), row.getTotal()));
        
        return result;
    }

    // ---------- private helpers ----------

    private Property findProperty(Long id) 
    {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    private Owner findOwner(Long ownerId) 
    {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
    }
}
