package com.example.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.RentPayment;
import com.example.enums.PaymentStatus;

import jakarta.persistence.criteria.Predicate;

public final class RentPaymentSpecification {

    private RentPaymentSpecification() {
    }

    /** Every filter is optional. */
    public static Specification<RentPayment> withFilters(Long leaseId, PaymentStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (leaseId != null) {
                predicates.add(cb.equal(root.get("lease").get("id"), leaseId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
