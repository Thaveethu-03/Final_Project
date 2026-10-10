package com.example.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.Lease;
import com.example.enums.LeaseStatus;

import jakarta.persistence.criteria.Predicate;

public final class LeaseSpecification {

    private LeaseSpecification() {
    }

    public static Specification<Lease> withFilters(LeaseStatus status, Long propertyId, Long tenantId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (propertyId != null) {
                predicates.add(cb.equal(root.get("property").get("id"), propertyId));
            }
            if (tenantId != null) {
                predicates.add(cb.equal(root.get("tenant").get("id"), tenantId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
