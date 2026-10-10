package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.dto.OwnerRequestDTO;
import com.example.dto.OwnerResponseDTO;
import com.example.entity.Owner;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner toEntity(OwnerRequestDTO dto);
    OwnerResponseDTO toResponseDTO(Owner owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(OwnerRequestDTO dto, @MappingTarget Owner owner);
}
