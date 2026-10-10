package com.example.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.LeaseRequestDTO;
import com.example.dto.LeaseResponseDTO;
import com.example.dto.LeaseUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.enums.LeaseStatus;
import com.example.service.LeaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
@Tag(name = "Lease", description = "Lease management APIs")
public class LeaseController {

    private final LeaseService leaseService;

    @PostMapping
    @Operation(summary = "Create a lease (property becomes RENTED)")
    public ResponseEntity<LeaseResponseDTO> createLease(@Valid @RequestBody LeaseRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaseService.createLease(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lease by id")
    public ResponseEntity<LeaseResponseDTO> getLeaseById(@PathVariable Long id) {
        return ResponseEntity.ok(leaseService.getLeaseById(id));
    }

    @GetMapping
    @Operation(summary = "List leases with optional filters, pagination and sorting")
    public ResponseEntity<PageResponseDTO<LeaseResponseDTO>> getAllLeases(
            @RequestParam(required = false) LeaseStatus status,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(required = false) Long tenantId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(leaseService.getAllLeases(status, propertyId, tenantId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an ACTIVE lease (end date, rent, deposit)")
    public ResponseEntity<LeaseResponseDTO> updateLease(@PathVariable Long id,
            @Valid @RequestBody LeaseUpdateRequestDTO dto) {
        return ResponseEntity.ok(leaseService.updateLease(id, dto));
    }

    @PatchMapping("/{id}/terminate")
    @Operation(summary = "Terminate an ACTIVE lease (property becomes AVAILABLE)")
    public ResponseEntity<LeaseResponseDTO> terminateLease(@PathVariable Long id) {
        return ResponseEntity.ok(leaseService.terminateLease(id));
    }

    @GetMapping("/expiring")
    @Operation(summary = "ACTIVE leases ending within the next N days")
    public ResponseEntity<List<LeaseResponseDTO>> getExpiringLeases(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(leaseService.getExpiringLeases(days));
    }

    @PostMapping("/expire-overdue")
    @Operation(summary = "Mark ACTIVE leases whose end date is over as EXPIRED")
    public ResponseEntity<Map<String, Integer>> expireOverdueLeases() {
        return ResponseEntity.ok(Map.of("expiredLeases", leaseService.expireOverdueLeases()));
    }
}
