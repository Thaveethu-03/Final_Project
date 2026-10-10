package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.AuthResponseDTO;
import com.example.dto.LoginRequestDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login (open to everyone)")
@SecurityRequirements
public class AuthController 
{

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new USER")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) 
    {
        return ResponseEntity
        			.status(HttpStatus.CREATED)
        			.body(authService.register(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get a JWT token")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) 
    {
        return ResponseEntity.ok(authService.login(dto));
    }
}
