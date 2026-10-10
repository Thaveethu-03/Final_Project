package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Lease;
import com.example.enums.LeaseStatus;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long>, JpaSpecificationExecutor<Lease> {

    boolean existsByPropertyId(Long propertyId);

    boolean existsByTenantId(Long tenantId);

    boolean existsByTenantIdAndPropertyIdAndStatus(Long tenantId, Long propertyId, LeaseStatus status);

    List<Lease> findByStatusAndEndDateBetween(LeaseStatus status, LocalDate from, LocalDate to);

    List<Lease> findByStatusAndEndDateBefore(LeaseStatus status, LocalDate date);

    // JPQL: how many active leases of this property overlap with the given dates
    // (two date ranges overlap when start1 <= end2 AND end1 >= start2)
    @Query("""
            SELECT COUNT(l) FROM Lease l
            WHERE l.property.id = :propertyId
              AND l.status = :status
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
              AND l.id <> :excludeLeaseId
            """)
    long countOverlappingLeases(@Param("propertyId") Long propertyId,
            @Param("status") LeaseStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeLeaseId") Long excludeLeaseId);
}
