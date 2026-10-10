package com.example.controller;

import java.util.Map;

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

import com.example.dto.PageResponseDTO;
import com.example.dto.RentPaymentPayRequestDTO;
import com.example.dto.RentPaymentRequestDTO;
import com.example.dto.RentPaymentResponseDTO;
import com.example.dto.RentSummaryResponseDTO;
import com.example.enums.PaymentStatus;
import com.example.service.RentPaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Rent Payment", description = "Rent payment APIs")
public class RentPaymentController 
{

    private final RentPaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a rent payment due record (status PENDING)")
    public ResponseEntity<RentPaymentResponseDTO> createPayment(
    		@Valid @RequestBody RentPaymentRequestDTO dto) 
    {
        return ResponseEntity
        			.status(HttpStatus.CREATED)
        			.body(paymentService.createPayment(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by id")
    public ResponseEntity<RentPaymentResponseDTO> getPaymentById(@PathVariable Long id) 
    {
        return ResponseEntity
        				.ok(paymentService
        				.getPaymentById(id));
    }

    
    @GetMapping
    @Operation(summary = "List payments with optional filters, pagination and sorting")
    public ResponseEntity<PageResponseDTO<RentPaymentResponseDTO>> getAllPayments(
            @RequestParam(required = false) Long leaseId,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) 
    {
        return ResponseEntity
        			.ok(paymentService
        			.getAllPayments(
        			leaseId,
        			status, 
        			pageable));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Mark a payment as PAID")
    public ResponseEntity<RentPaymentResponseDTO> payRent(@PathVariable Long id,
            @Valid @RequestBody RentPaymentPayRequestDTO dto) 
    {
        return ResponseEntity
        					.ok(paymentService
        					.payRent(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a payment (PAID payment cannot be deleted)")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) 
    {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @Operation(summary = "Rent summary report of one lease (JPQL group by)")
    public ResponseEntity<RentSummaryResponseDTO> getRentSummary(@RequestParam Long leaseId) 
    {
        return ResponseEntity
        				.ok(paymentService
        				.getRentSummary(leaseId));
    }

    @PostMapping("/mark-overdue")
    @Operation(summary = "Mark PENDING payments whose due date is over as OVERDUE")
    public ResponseEntity<Map<String, Integer>> markOverduePayments() 
    {
        return ResponseEntity
        				.ok(Map.of("markedOverdue", 
        				paymentService
        				.markOverduePayments()));
    }
}
