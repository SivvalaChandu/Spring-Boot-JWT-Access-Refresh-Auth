package com.Backend.JWTImp.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/auth/register")) {
            filterChain.doFilter(request, response); // Just pass the request through
            return;
        }

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String username = null;

            // First try to validate access token
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                System.out.println("Found Authorization header");
                String token = authHeader.substring(BEARER_PREFIX.length()).trim();
                try {
                    username = jwtUtils.getSubject(token);
                    System.out.println("Extracted username from token: "+ username);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                            && jwtUtils.isValidToken(token, username)) {
                        System.out.println("Token is valid, authenticating user");
                        authenticateUser(username, response, null);
                    }
                } catch (ExpiredJwtException e) {
                    // Access token expired, try refresh token
                    System.out.println("Access token expired, attempting to use refresh token");
                    handleRefreshToken(request, response);
                }
            } else {
                // No access token, try refresh token
                System.out.println("No Authorization header found, checking for refresh token");
                handleRefreshToken(request, response);
            }
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
        }

        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Found cookies in request");
            Optional<Cookie> refreshTokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> REFRESH_TOKEN_COOKIE.equals(cookie.getName()))
                    .findFirst();

            if (refreshTokenCookie.isPresent()) {
                System.out.println("Found refresh token cookie");
                String refreshToken = refreshTokenCookie.get().getValue();
                try {
                    String username = jwtUtils.getSubject(refreshToken);
                    System.out.println("Extracted username from refresh token: "+ username);
                    if (username != null && jwtUtils.isValidToken(refreshToken, username)) {
                        System.out.println("Refresh token is valid, authenticating user");
                        authenticateUser(username, response, refreshToken);
                    }
                } catch (Exception e) {
                    logger.error("Refresh token validation failed: ", e);
                    // Clear invalid refresh token
                    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }else {
            logger.debug("No cookies found in request");
        }
    }

    private void authenticateUser(String username, HttpServletResponse response, String refreshToken) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Issue new access token if this was triggered by a refresh token
            if (refreshToken != null) {
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority())
                        .orElse("ROLE_USER");

                String newAccessToken = jwtUtils.issueAccessToken(username, role);
                response.setHeader(HttpHeaders.AUTHORIZATION, newAccessToken);

                // Also set it as a response header for the client to capture
                response.setHeader("Access-Control-Expose-Headers", "Authorization");
            }
        } catch (Exception e) {
            logger.error("Error during user authentication: ", e);
        }
    }
}