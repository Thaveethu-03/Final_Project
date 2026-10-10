package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.entity.MaintenanceRequest;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long>, JpaSpecificationExecutor<MaintenanceRequest> 
{

    boolean existsByPropertyId(Long propertyId);

    boolean existsByTenantId(Long tenantId);
}
