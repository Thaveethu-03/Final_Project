package com.example.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;	
    private LocalDateTime updatedAt;
}
