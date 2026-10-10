package com.example.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.OwnerRequestDTO;
import com.example.dto.OwnerResponseDTO;
import com.example.dto.PageResponseDTO;
import com.example.service.OwnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Owner", description = "Property owner management APIs")
public class OwnerController 
{

    private final OwnerService ownerService;

    @PostMapping
    @Operation(summary = "Create a new owner")
    public ResponseEntity<OwnerResponseDTO> createOwner(@Valid @RequestBody OwnerRequestDTO request) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.createOwner(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by id")
    public ResponseEntity<OwnerResponseDTO> getOwnerById(@PathVariable Long id) 
    {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @GetMapping
    @Operation(summary = "List owners with pagination, sorting and optional name search")
    public ResponseEntity<PageResponseDTO<OwnerResponseDTO>> getAllOwners(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) 
    {
        return ResponseEntity.ok(ownerService.getAllOwners(name, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update owner")
    public ResponseEntity<OwnerResponseDTO> updateOwner(@PathVariable Long id,
            @Valid @RequestBody OwnerRequestDTO request) 
    {
        return ResponseEntity.ok(ownerService.updateOwner(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete owner")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) 
    {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
