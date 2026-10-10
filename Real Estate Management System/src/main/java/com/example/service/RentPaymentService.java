package com.example.service;

import org.springframework.data.domain.Pageable;

import com.example.dto.PageResponseDTO;
import com.example.dto.RentPaymentPayRequestDTO;
import com.example.dto.RentPaymentRequestDTO;
import com.example.dto.RentPaymentResponseDTO;
import com.example.dto.RentSummaryResponseDTO;
import com.example.enums.PaymentStatus;

public interface RentPaymentService {

    RentPaymentResponseDTO createPayment(RentPaymentRequestDTO dto);

    RentPaymentResponseDTO getPaymentById(Long id);

    /** All filters are optional. */
    PageResponseDTO<RentPaymentResponseDTO> getAllPayments(Long leaseId, PaymentStatus status, Pageable pageable);

    RentPaymentResponseDTO payRent(Long id, RentPaymentPayRequestDTO dto);

    void deletePayment(Long id);

    RentSummaryResponseDTO getRentSummary(Long leaseId);

    /** PENDING payments whose due date is over become OVERDUE. Returns how many were changed. */
    int markOverduePayments();
}
