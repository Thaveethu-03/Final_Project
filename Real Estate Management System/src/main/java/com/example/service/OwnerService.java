package com.example.service;

import org.springframework.data.domain.Pageable;

import com.example.dto.OwnerRequestDTO;
import com.example.dto.OwnerResponseDTO;
import com.example.dto.PageResponseDTO;

public interface OwnerService {

    OwnerResponseDTO createOwner(OwnerRequestDTO request);

    OwnerResponseDTO getOwnerById(Long id);

    /** name is optional. If given, only owners whose name contains it are returned. */
    PageResponseDTO<OwnerResponseDTO> getAllOwners(String name, Pageable pageable);

    OwnerResponseDTO updateOwner(Long id, OwnerRequestDTO request);

    void deleteOwner(Long id);
}
