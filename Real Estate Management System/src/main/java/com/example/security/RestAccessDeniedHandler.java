package com.example.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** 403 JSON when the user is logged in but does not have the role (example: USER tries DELETE). */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler 
{

    @Override
    public void handle(HttpServletRequest request, 
    					   HttpServletResponse response,
    					   AccessDeniedException accessDeniedException)
    					   throws IOException 
    {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"timestamp\":\"" 
						        			+ LocalDateTime.now()
						                + "\",\"status\":403,\"error\":\"Forbidden\","
						                + "\"message\":\"You do not have permission to perform this action.\","
						                + "\"path\":\"" + request.getRequestURI().replace("\"", "") + "\"}");
    }
}
