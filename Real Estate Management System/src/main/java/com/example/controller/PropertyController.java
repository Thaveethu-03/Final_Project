package com.example.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.CityStatsDTO;
import com.example.dto.PageResponseDTO;
import com.example.dto.PropertyRequestDTO;
import com.example.dto.PropertyResponseDTO;
import com.example.enums.PropertyStatus;
import com.example.enums.PropertyType;
import com.example.service.PropertyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Property", description = "Property management APIs")
public class PropertyController 
{

    private final PropertyService propertyService;

    @PostMapping
    @Operation(summary = "Create a new property (status starts as AVAILABLE)")
    public ResponseEntity<PropertyResponseDTO> createProperty(@Valid @RequestBody PropertyRequestDTO dto) 
    {
        return ResponseEntity
        		.status(HttpStatus.CREATED)
        		.body(propertyService
        		.createProperty(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get property by id (cached)")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(@PathVariable Long id) 
    {
        return ResponseEntity.ok(
        		propertyService
        		.getPropertyById(id));
    }

    @GetMapping
    @Operation(summary = "Search properties with optional filters, pagination and sorting")
    public ResponseEntity<PageResponseDTO<PropertyResponseDTO>> searchProperties(
    		
            @RequestParam(required = false) String city,

            @RequestParam(required = false) PropertyType propertyType,

            @RequestParam(required = false) PropertyStatus status,

            @RequestParam(required = false) BigDecimal minRent,

            @RequestParam(required = false) BigDecimal maxRent,

            @RequestParam(required = false) Integer bedrooms,
            
            @PageableDefault(size = 10, sort = "id") 
            Pageable pageable) 
    {
        return ResponseEntity.ok(propertyService	.searchProperties(
        		city, 
        		propertyType, 
        		status, 
        		minRent, 
        		maxRent,
            bedrooms, 
            pageable));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "List properties of one owner")
    public ResponseEntity<PageResponseDTO<PropertyResponseDTO>> getPropertiesByOwner(@PathVariable Long ownerId,
            @PageableDefault(size = 10, sort = "id") 
    			Pageable pageable) 
    {
        return ResponseEntity
        		.ok(propertyService
        		.getPropertiesByOwner(ownerId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update property")
    public ResponseEntity<PropertyResponseDTO> updateProperty(@PathVariable Long id,
            @Valid @RequestBody PropertyRequestDTO dto) 
    {
        return ResponseEntity
        		.ok(propertyService
        		.updateProperty(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change property status (AVAILABLE, RENTED, UNDER_MAINTENANCE)")
    public ResponseEntity<PropertyResponseDTO> updatePropertyStatus(@PathVariable Long id,
            @RequestParam PropertyStatus status) 
    {	
        return ResponseEntity
        		.ok(propertyService
        		.updatePropertyStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property (rented property cannot be deleted)")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) 
    {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/city-stats")
    @Operation(summary = "Report (JPQL): property count and average rent per city (cached)")
    public ResponseEntity<List<CityStatsDTO>> getCityStats() 
    {
        return ResponseEntity
        		.ok(propertyService
        		.getCityStats());
    }

    @GetMapping("/reports/status-count")
    @Operation(summary = "Report (native SQL): property count per status")
    public ResponseEntity<Map<String, Long>> getStatusCounts() 
    {
        return ResponseEntity
        		.ok(propertyService
        		.getStatusCounts());
    }
}
