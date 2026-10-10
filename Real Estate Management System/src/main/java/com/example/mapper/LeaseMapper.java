package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.LeaseRequestDTO;
import com.example.dto.LeaseResponseDTO;
import com.example.entity.Lease;

@Mapper(componentModel = "spring")
public interface LeaseMapper {

    // property, tenant and status are set in the service after validation
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "property", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lease toEntity(LeaseRequestDTO dto);

    @Mapping(source = "property.id", target = "propertyId")
    @Mapping(source = "property.title", target = "propertyTitle")
    @Mapping(source = "tenant.id", target = "tenantId")
    @Mapping(source = "tenant.name", target = "tenantName")
    LeaseResponseDTO toResponseDTO(Lease lease);
}
