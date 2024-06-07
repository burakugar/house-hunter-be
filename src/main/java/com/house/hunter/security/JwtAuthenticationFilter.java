package com.house.hunter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.house.hunter.exception.InvalidTokenException;
import com.house.hunter.model.dto.error.ErrorDto;
import com.house.hunter.util.BlacklistedTokenService;
import com.house.hunter.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;
    private UserDetailsService userDetailsServiceImpl;
    private BlacklistedTokenService blacklistedTokenService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        String username = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtUtil.getEmailFromToken(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                final boolean isValid = jwtUtil.validateTokenWithoutPrefix(token);
                final boolean isBlacklisted = blacklistedTokenService.isBlacklisted(token);
                if (isValid && !isBlacklisted) {
                    // if token is valid configure Spring Security to manually set authentication
                    final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, specify that the current user is authenticated
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Clear the security context if the token is invalid or blacklisted
                    SecurityContextHolder.clearContext();
                    throw new InvalidTokenException("Token is expired, invalid, or blacklisted");
                }
            }
        } catch (ExpiredJwtException ex) {
            // Handle the ExpiredJwtException
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "JWT token has expired", List.of(ex.getMessage()));
            objectMapper.writeValue(response.getWriter(), errorDto);
            return;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ErrorDto errorDto = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), List.of(e.getMessage()));
            objectMapper.writeValue(response.getWriter(), errorDto);
            return;
        }
        filterChain.doFilter(request, response);

    }

}