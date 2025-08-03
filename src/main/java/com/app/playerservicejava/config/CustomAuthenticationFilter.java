package com.app.playerservicejava.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
public class CustomAuthenticationFilter implements Filter {

    private static final Map<String, String> VALID_USERS = new HashMap<>();
    
    static {
        // admin:admin123 encoded in Base64
        VALID_USERS.put("YWRtaW46YWRtaW4xMjM=", "ADMIN");
        // user:user123 encoded in Base64  
        VALID_USERS.put("dXNlcjp1c2VyMTIz", "USER");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Allow H2 console access without authentication
        if (httpRequest.getRequestURI().startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        // Check for Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Player Service\"");
            httpResponse.getWriter().write("Authentication required");
            return;
        }

        // Extract and validate credentials
        String credentials = authHeader.substring(6); // Remove "Basic " prefix
        
        if (!VALID_USERS.containsKey(credentials)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Player Service\"");
            httpResponse.getWriter().write("Invalid credentials");
            return;
        }

        // Authentication successful, continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
} 