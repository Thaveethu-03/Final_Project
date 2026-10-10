package com.example.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.Property;
import com.example.enums.PropertyStatus;
import com.example.enums.PropertyType;

import jakarta.persistence.criteria.Predicate;

public final class PropertySpecification {

    private PropertySpecification() {
    }

    /** Every filter is optional. Only the filters that are given are applied. */
    public static Specification<Property> withFilters(String city, PropertyType propertyType,
            PropertyStatus status, BigDecimal minRent, BigDecimal maxRent, Integer bedrooms) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.<String>get("city")), city.trim().toLowerCase()));
            }
            if (propertyType != null) {
                predicates.add(cb.equal(root.get("propertyType"), propertyType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (minRent != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("monthlyRent"), minRent));
            }
            if (maxRent != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("monthlyRent"), maxRent));
            }
            if (bedrooms != null) {
                predicates.add(cb.equal(root.get("bedrooms"), bedrooms));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
