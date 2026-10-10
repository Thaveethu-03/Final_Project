package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.dto.TenantRequestDTO;
import com.example.dto.TenantResponseDTO;
import com.example.entity.Tenant;

@Mapper(componentModel = "spring")
public interface TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tenant toEntity(TenantRequestDTO dto);

    TenantResponseDTO toResponseDTO(Tenant tenant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TenantRequestDTO dto, @MappingTarget Tenant tenant);
}
