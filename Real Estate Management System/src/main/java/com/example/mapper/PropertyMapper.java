package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.dto.PropertyRequestDTO;
import com.example.dto.PropertyResponseDTO;
import com.example.entity.Property;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    // owner and status are set in the service after validation
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Property toEntity(PropertyRequestDTO dto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.name", target = "ownerName")
    PropertyResponseDTO toResponseDTO(Property property);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(PropertyRequestDTO dto, @MappingTarget Property property);
}