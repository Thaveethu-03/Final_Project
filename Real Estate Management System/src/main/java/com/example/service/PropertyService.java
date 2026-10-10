package com.example.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.example.dto.CityStatsDTO;
import com.example.dto.PageResponseDTO;
import com.example.dto.PropertyRequestDTO;
import com.example.dto.PropertyResponseDTO;
import com.example.enums.PropertyStatus;
import com.example.enums.PropertyType;

public interface PropertyService {

    PropertyResponseDTO createProperty(PropertyRequestDTO dto);

    PropertyResponseDTO getPropertyById(Long id);

    PageResponseDTO<PropertyResponseDTO> searchProperties(
    		String city, 
    		PropertyType propertyType,
        PropertyStatus status, 
        BigDecimal minRent, 
        BigDecimal maxRent, 
        Integer bedrooms, 
        Pageable pageable);

    PageResponseDTO<PropertyResponseDTO> getPropertiesByOwner(Long ownerId, Pageable pageable);

    PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO dto);

    PropertyResponseDTO updatePropertyStatus(Long id, PropertyStatus status);

    void deleteProperty(Long id);

    List<CityStatsDTO> getCityStats();

    Map<String, Long> getStatusCounts();
}
