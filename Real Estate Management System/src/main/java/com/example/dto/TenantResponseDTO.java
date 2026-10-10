package com.example.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor	
public class TenantResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String occupation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
