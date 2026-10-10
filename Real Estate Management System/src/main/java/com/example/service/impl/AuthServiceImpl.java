package com.example.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dto.AuthResponseDTO;
import com.example.dto.LoginRequestDTO;
import com.example.dto.RegisterRequestDTO;
import com.example.dto.UserResponseDTO;
import com.example.entity.AppUser;
import com.example.enums.Role;
import com.example.exception.DuplicateResourceException;
import com.example.exception.InvalidCredentialsException;
import com.example.repository.AppUserRepository;
import com.example.security.JwtService;
import com.example.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService 
{

    private static final String BAD_CREDENTIALS = "Invalid username or password";

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponseDTO register(RegisterRequestDTO dto) 
    {
        String username = dto.getUsername().trim();
        log.info("Registering user '{}'", username);

        if (userRepository.existsByUsername(username)) 
        {
            throw new DuplicateResourceException("Username already taken: " + username);
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        AppUser saved = userRepository.save(user);
        return new UserResponseDTO(
        			saved.getId(), 
        			saved.getUsername(), 
        			saved.getRole().name());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) 
    {
    	
        AppUser user = userRepository.findByUsername(
        					dto.getUsername().trim())
                			.orElseThrow(() -> new InvalidCredentialsException(BAD_CREDENTIALS));

        if (!passwordEncoder.matches(
        					dto.getPassword(), 
        					user.getPassword())) {
            				throw new InvalidCredentialsException(BAD_CREDENTIALS);
        }

        String token = jwtService.generateToken(
        					user.getUsername(), 
        					user.getRole());
        
        log.info("User '{}' logged in", user.getUsername());
        return new AuthResponseDTO(
        			token, "Bearer", 
        			user.getUsername(), 
        			user.getRole().name(),
                jwtService.getExpirationMs());
    }
}
