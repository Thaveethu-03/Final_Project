package com.example.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 401 JSON when no token or a wrong token is sent. */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint 
{

    @Override
    public void commence(HttpServletRequest request, 
    						HttpServletResponse response,
    						AuthenticationException authException) 
    						throws IOException 
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"timestamp\":\"" 
        			+ LocalDateTime.now()
                + "\",\"status\":401,\"error\":\"Unauthorized\","
                + "\"message\":\"Authentication required. Login and send a valid Bearer token.\","
                + "\"path\":\"" 
                + request.getRequestURI().replace("\"", "") + "\"}");
    }
}
