package com.Backend.JWTImp.controller;

import com.Backend.JWTImp.Dto.LoginDto;
import com.Backend.JWTImp.model.User;
import jakarta.servlet.http.Cookie;
import com.Backend.JWTImp.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;


    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(8 * 60);  // 7 days expiry for refresh token
        // Add SameSite attribute for better security
//        refreshTokenCookie.setAttribute("SameSite", "Strict");
        return refreshTokenCookie;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        System.out.println("Login entered");
        Map<String, String> tokens = authService.login(loginDto);

        Cookie refreshTokenCookie = createRefreshTokenCookie(tokens.get("refresh_token"));
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, tokens.get("access_token"))
                .build();
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpServletResponse response) {
            System.out.println("register entered");
            Map<String, String> tokens = authService.register(user);

            Cookie refreshTokenCookie = createRefreshTokenCookie(tokens.get("refresh_token"));
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, tokens.get("access_token"))
                    .build();
        }
}
