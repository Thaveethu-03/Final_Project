package com.example.service;

import com.example.dto.AuthResponseDTO;
import com.example.dto.LoginRequestDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.dto.UserResponseDTO;

public interface AuthService {

    /** Creates a normal USER. ADMIN users cannot be created from the API. */
    UserResponseDTO register(RegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO dto);
}
