package com.example.dto;

/** Login result. Send the token in every request: Authorization: Bearer <token> */
public record AuthResponseDTO(String token, String tokenType, String username, String role, long expiresInMs) 
{
    
}
