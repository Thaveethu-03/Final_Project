package com.example.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter 
{

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) 
    {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
    								   HttpServletResponse response, 
    								   FilterChain chain)
            throws ServletException, IOException 
    {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")
                			   && SecurityContextHolder
                			   .getContext()
                			   .getAuthentication() == null) 
        {
            String token = header.substring(7).trim();
            try {
                if (jwtService.isTokenValid(token)) 
                {
                    String username = jwtService.extractUsername(token);
                    UserDetails user = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    							user, 
                    							null, 
                    							user.getAuthorities());
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource()
                    								.buildDetails(request));
                    
                    SecurityContextHolder
                    				.getContext()
                    				.setAuthentication(authentication);
                }
            } catch (Exception ex) {
                log.debug("JWT authentication failed: {}", ex.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
