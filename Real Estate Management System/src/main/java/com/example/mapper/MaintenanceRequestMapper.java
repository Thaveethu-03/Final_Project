package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.MaintenanceCreateRequestDTO;
import com.example.dto.MaintenanceResponseDTO;
import com.example.entity.MaintenanceRequest;

@Mapper(componentModel = "spring")
public interface MaintenanceRequestMapper 
{

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "cost", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MaintenanceRequest toEntity(MaintenanceCreateRequestDTO dto);

    @Mapping(source = "property.id", target = "propertyId")
    @Mapping(source = "property.title", target = "propertyTitle")
    @Mapping(source = "tenant.id", target = "tenantId")
    @Mapping(source = "tenant.name", target = "tenantName")
    MaintenanceResponseDTO toResponseDTO(MaintenanceRequest request);
}
