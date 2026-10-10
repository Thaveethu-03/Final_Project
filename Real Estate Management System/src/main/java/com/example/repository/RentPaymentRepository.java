package com.example.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dto.PaymentStatusSummaryDTO;
import com.example.entity.RentPayment;
import com.example.enums.PaymentStatus;

@Repository
public interface RentPaymentRepository extends JpaRepository<RentPayment, Long>, 
		JpaSpecificationExecutor<RentPayment> 
{

    boolean existsByLeaseIdAndDueDate(Long leaseId, LocalDate dueDate);

    // JPQL: total amount and count per status for one lease (rent summary report)
    @Query("""
            SELECT new com.example.dto.PaymentStatusSummaryDTO(p.status, SUM(p.amount), COUNT(p))
            FROM RentPayment p
            WHERE p.lease.id = :leaseId
            GROUP BY p.status
            """)
    List<PaymentStatusSummaryDTO> getSummaryByLease(@Param("leaseId") Long leaseId);

    // JPQL bulk update: PENDING payments whose due date is over become OVERDUE
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE RentPayment p
            SET p.status = :newStatus, p.updatedAt = :now
            WHERE p.status = :oldStatus AND p.dueDate < :today
            """)
    
    int markOverdue(@Param("oldStatus") PaymentStatus oldStatus,
            @Param("newStatus") PaymentStatus newStatus,
            @Param("today") LocalDate today,
            @Param("now") LocalDateTime now);
}
