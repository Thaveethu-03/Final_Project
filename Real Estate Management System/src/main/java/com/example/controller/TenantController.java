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

import com.example.dto.PageResponseDTO;
import com.example.dto.TenantRequestDTO;
import com.example.dto.TenantResponseDTO;
import com.example.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant management APIs")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<TenantResponseDTO> createTenant(@Valid @RequestBody TenantRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.createTenant(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by id")
    public ResponseEntity<TenantResponseDTO> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenantById(id));
    }

    @GetMapping
    @Operation(summary = "List tenants with pagination, sorting and optional name search")
    public ResponseEntity<PageResponseDTO<TenantResponseDTO>> getAllTenants(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(tenantService.getAllTenants(name, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant")
    public ResponseEntity<TenantResponseDTO> updateTenant(@PathVariable Long id,
            @Valid @RequestBody TenantRequestDTO dto) {
        return ResponseEntity.ok(tenantService.updateTenant(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant (tenant with lease or maintenance records cannot be deleted)")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }
}
