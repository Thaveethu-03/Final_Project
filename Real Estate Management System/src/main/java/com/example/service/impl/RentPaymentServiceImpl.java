package com.example.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.PageResponseDTO;
import com.example.dto.PaymentStatusSummaryDTO;
import com.example.dto.RentPaymentPayRequestDTO;
import com.example.dto.RentPaymentRequestDTO;
import com.example.dto.RentPaymentResponseDTO;
import com.example.dto.RentSummaryResponseDTO;
import com.example.entity.Lease;
import com.example.entity.RentPayment;
import com.example.enums.LeaseStatus;
import com.example.enums.PaymentStatus;
import com.example.exception.BusinessRuleException;
import com.example.exception.DuplicateResourceException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.RentPaymentMapper;
import com.example.repository.LeaseRepository;
import com.example.repository.RentPaymentRepository;
import com.example.service.RentPaymentService;
import com.example.specification.RentPaymentSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentPaymentServiceImpl implements RentPaymentService 
{

    private final RentPaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final RentPaymentMapper paymentMapper;

    @Override
    @Transactional
    public RentPaymentResponseDTO createPayment(RentPaymentRequestDTO dto) 
    {
        log.info("Creating rent payment for leaseId={}, dueDate={}", 
        			dto.getLeaseId(), 
        			dto.getDueDate());

        Lease lease = findLease(dto.getLeaseId());

        if (lease.getStatus() != LeaseStatus.ACTIVE) 
        {
            throw new BusinessRuleException("Payments can be created only for ACTIVE lease");
        }
        
        if (dto.getDueDate().isBefore(lease.getStartDate()) || dto.getDueDate().isAfter(lease.getEndDate())) 
        {
            throw new BusinessRuleException(
            			"Due date must be within the lease period ("
                    + lease.getStartDate() + 
                    " to " + lease.getEndDate() + ")");
        }
        
        if (paymentRepository.existsByLeaseIdAndDueDate(
        			lease.getId(), 
        			dto.getDueDate())) 
        {
            throw new DuplicateResourceException("A payment with due date " + 
            			dto.getDueDate()
                    + " already exists for this lease");
        }

        RentPayment payment = paymentMapper.toEntity(dto);
        payment.setLease(lease);
        payment.setStatus(PaymentStatus.PENDING);
        
        if (payment.getAmount() == null)
        {
            payment.setAmount(lease.getMonthlyRent());
        }

        RentPayment saved = paymentRepository.save(payment);
        log.info("Rent payment created with id={}", saved.getId());
        return paymentMapper.toResponseDTO(saved);
        
    }

    @Override
    @Transactional(readOnly = true)
    public RentPaymentResponseDTO getPaymentById(Long id) 
    {
        return paymentMapper.toResponseDTO(findPayment(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<RentPaymentResponseDTO> getAllPayments(
	    			Long leaseId, 
	    			PaymentStatus status,
	            Pageable pageable) 
    {
        Page<RentPayment> page = paymentRepository
        			.findAll(RentPaymentSpecification
        			.withFilters(
        			leaseId, 
        			status),
                pageable);
        
        return PageResponseDTO.of(page.map(paymentMapper::toResponseDTO));
    }

    @Override
    @Transactional
    public RentPaymentResponseDTO payRent(Long id, RentPaymentPayRequestDTO dto) 
    
    {
        log.info("Paying rent payment id={}", id);

        RentPayment payment = findPayment(id);

        if (payment.getStatus() == PaymentStatus.PAID) 
        {
            throw new BusinessRuleException("This payment is already PAID");
        }

        LocalDate paidDate = dto.getPaidDate() != null ? dto.getPaidDate() : LocalDate.now();
        
        if (paidDate.isAfter(LocalDate.now())) 
        {
            throw new BusinessRuleException("Paid date cannot be in the future");
        }

        payment.setPaidDate(paidDate);
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.PAID);

        return paymentMapper
        				.toResponseDTO(
        				paymentRepository
        				.save(payment));
    }

    @Override
    @Transactional
    public void deletePayment(Long id) 
    {
        RentPayment payment = findPayment(id);

        if (payment.getStatus() == PaymentStatus.PAID) 
        {
            throw new BusinessRuleException("PAID payment cannot be deleted");
        }

        paymentRepository.delete(payment);
        log.info("Rent payment deleted with id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public RentSummaryResponseDTO getRentSummary(Long leaseId) 
    {
        findLease(leaseId);

        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalPending = BigDecimal.ZERO;
        BigDecimal totalOverdue = BigDecimal.ZERO;
        long paidCount = 0;
        long pendingCount = 0;
        long overdueCount = 0;

        List<PaymentStatusSummaryDTO> rows = paymentRepository.getSummaryByLease(leaseId);
        for (PaymentStatusSummaryDTO row : rows) 
        {
            switch (row.status()) 
            {
                case PAID -> 
                {
                    totalPaid = row.totalAmount();
                    paidCount = row.count();
                }
                case PENDING -> 
                {
                    totalPending = row.totalAmount();
                    pendingCount = row.count();
                }
                case OVERDUE -> 
                {
                    totalOverdue = row.totalAmount();
                    overdueCount = row.count();
                }
            }
        }

        return new RentSummaryResponseDTO(
        			leaseId, 
        			totalPaid, 
        			totalPending, 
        			totalOverdue, 
        			paidCount, 
        			pendingCount,
                overdueCount);
    }

    @Override
    @Transactional
    public int markOverduePayments() 
    {
        int updated = paymentRepository
        			.markOverdue(
        			PaymentStatus.PENDING, 
        			PaymentStatus.OVERDUE, 
        			LocalDate.now(),
                LocalDateTime.now());
        
        log.info("{} payment(s) marked as OVERDUE", updated);
        return updated;
    }

    // ---------- private helpers ----------

    private RentPayment findPayment(Long id) 
    {
        return paymentRepository.findById(id)
                .orElseThrow(() -> 
                new ResourceNotFoundException
                ("Rent payment not found with id: " + id));
    }

    private Lease findLease(Long id) 
    {
        return leaseRepository.findById(id)
                .orElseThrow(() -> 
                new ResourceNotFoundException
                ("Lease not found with id: " + id));
    }
}
