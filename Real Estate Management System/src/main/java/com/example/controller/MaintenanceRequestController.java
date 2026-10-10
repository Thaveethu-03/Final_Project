package com.example.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.MaintenanceCreateRequestDTO;
import com.example.dto.MaintenanceResponseDTO;
import com.example.dto.MaintenanceStatusUpdateRequestDTO;
import com.example.dto.PageResponseDTO;
import com.example.enums.MaintenancePriority;
import com.example.enums.MaintenanceStatus;
import com.example.service.MaintenanceRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
@Tag(name = "Maintenance Request", description = "Maintenance request APIs")
public class MaintenanceRequestController 
{

    private final MaintenanceRequestService requestService;

    @PostMapping
    @Operation(summary = "Create a maintenance request (status OPEN)")
    public ResponseEntity<MaintenanceResponseDTO> createRequest(
            @Valid @RequestBody MaintenanceCreateRequestDTO dto) 
    {
        return ResponseEntity
        		.status(HttpStatus.CREATED)
        		.body(requestService
        		.createRequest(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance request by id")
    public ResponseEntity<MaintenanceResponseDTO> getRequestById(@PathVariable Long id) 
    {
        return ResponseEntity
        		.ok(requestService
        		.getRequestById(id));
    }

    @GetMapping
    @Operation(summary = "List maintenance requests with optional filters, pagination and sorting")
    public ResponseEntity<PageResponseDTO<MaintenanceResponseDTO>> getAllRequests(
            @RequestParam(required = false) MaintenanceStatus status,
            @RequestParam(required = false) MaintenancePriority priority,
            @RequestParam(required = false) Long propertyId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) 
    {
        return ResponseEntity.ok(requestService.getAllRequests(status, priority, propertyId, pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change status: OPEN > IN_PROGRESS > COMPLETED (or CANCELLED)")
    public ResponseEntity<MaintenanceResponseDTO> updateStatus(@PathVariable Long id,
            @Valid @RequestBody MaintenanceStatusUpdateRequestDTO dto) 
    {
        return ResponseEntity
        		.ok(requestService
        		.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a maintenance request (only OPEN)")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) 
    {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}
