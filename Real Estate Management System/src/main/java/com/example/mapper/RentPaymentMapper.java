package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dto.RentPaymentRequestDTO;
import com.example.dto.RentPaymentResponseDTO;
import com.example.entity.RentPayment;

@Mapper(componentModel = "spring")
public interface RentPaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lease", ignore = true)
    @Mapping(target = "paidDate", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RentPayment toEntity(RentPaymentRequestDTO dto);

    @Mapping(source = "lease.id", target = "leaseId")
    @Mapping(source = "lease.property.title", target = "propertyTitle")
    @Mapping(source = "lease.tenant.name", target = "tenantName")
    RentPaymentResponseDTO toResponseDTO(RentPayment payment);
}
