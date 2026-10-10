package com.example.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.entity.MaintenanceRequest;
import com.example.enums.MaintenancePriority;
import com.example.enums.MaintenanceStatus;

import jakarta.persistence.criteria.Predicate;

public final class MaintenanceRequestSpecification 
{

    private MaintenanceRequestSpecification() 
    {
    	
    }

    public static Specification<MaintenanceRequest> 
    			withFilters(
    		    MaintenanceStatus status,
            MaintenancePriority priority, 
            Long propertyId) 
    {
        return (root, query, cb) -> 
        {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) 
            {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) 
            {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (propertyId != null) 
            {
                predicates.add(cb.equal(root.get("property").get("id"), propertyId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
