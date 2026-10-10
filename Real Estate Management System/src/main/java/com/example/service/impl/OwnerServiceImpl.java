package com.example.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.OwnerRequestDTO;
import com.example.dto.OwnerResponseDTO;
import com.example.dto.PageResponseDTO;
import com.example.entity.Owner;
import com.example.exception.BusinessRuleException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.OwnerMapper;
import com.example.repository.OwnerRepository;
import com.example.repository.PropertyRepository;
import com.example.service.OwnerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService 
{

    private final OwnerRepository ownerRepository;
    private final PropertyRepository propertyRepository;
    private final OwnerMapper ownerMapper;

    @Override
    @Transactional
    public OwnerResponseDTO createOwner(OwnerRequestDTO request) 
    {
        log.info("Creating owner with email={}", request.getEmail());

        if (ownerRepository.existsByEmail(request.getEmail())) 
        {
            throw new DuplicateResourceException("Owner already exists with email: " + request.getEmail());
        }
        if (ownerRepository.existsByPhone(request.getPhone())) 
        {
            throw new DuplicateResourceException("Owner already exists with phone: " + request.getPhone());
        }

        Owner saved = ownerRepository.save(ownerMapper.toEntity(request));
        log.info("Owner created with id={}", saved.getId());
        return ownerMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerResponseDTO getOwnerById(Long id) 
    {
        return ownerMapper.toResponseDTO(findOwner(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OwnerResponseDTO> getAllOwners(String name, Pageable pageable) 
    {
        Page<Owner> page;
        if (name == null || name.isBlank()) 
        {
            page = ownerRepository.findAll(pageable);
        } else 
        {
            page = ownerRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }
        return PageResponseDTO.of(page.map(ownerMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public OwnerResponseDTO updateOwner(Long id, OwnerRequestDTO request) 
    {
        log.info("Updating owner id={}", id);

        Owner owner = findOwner(id);

        if (ownerRepository.existsByEmailAndIdNot(request.getEmail(), id)) 
        {
            throw new DuplicateResourceException("Another owner already uses email: " + request.getEmail());
        }
        if (ownerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) 
        {
            throw new DuplicateResourceException("Another owner already uses phone: " + request.getPhone());
        }

        ownerMapper.updateEntity(request, owner);
        return ownerMapper.toResponseDTO(ownerRepository.save(owner));
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) 
    {
        Owner owner = findOwner(id);
        if (propertyRepository.existsByOwnerId(id)) 
        {
            throw new BusinessRuleException("Owner " + id + " cannot be deleted because the owner has properties");
        }
        ownerRepository.delete(owner);
        log.info("Owner deleted with id={}", id);
    }

    private Owner findOwner(Long id) 
    
    {
        return ownerRepository.findById(id)
                .orElseThrow(() -> 
                new ResourceNotFoundException("Owner not found with id: " + id));
    }
}
