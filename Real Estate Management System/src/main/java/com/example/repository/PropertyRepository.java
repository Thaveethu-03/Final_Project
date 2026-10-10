package com.example.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dto.CityStatsDTO;
import com.example.dto.StatusCountProjection;
import com.example.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    Page<Property> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerId(Long ownerId);

    boolean existsByOwnerIdAndTitle(Long ownerId, String title);

    boolean existsByOwnerIdAndTitleAndIdNot(Long ownerId, String title, Long id);

    // JPQL: properties count and average rent per city
    @Query("""
            SELECT new com.example.dto.CityStatsDTO(p.city, COUNT(p), AVG(p.monthlyRent))
            FROM Property p
            GROUP BY p.city
            ORDER BY COUNT(p) DESC
            """)
    List<CityStatsDTO> getCityStats();

    // Native SQL: properties count per status
    @Query(value = "SELECT status AS status, COUNT(*) AS total FROM properties GROUP BY status", nativeQuery = true)
    List<StatusCountProjection> countByStatusNative();
}
