package com.Backend.JWTImp.controller;

import com.Backend.JWTImp.Dto.LoginDto;
import com.Backend.JWTImp.model.User;
import com.Backend.JWTImp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        System.out.println("Login entered");
        String jwtToken = authService.login(loginDto);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtToken).build();
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        System.out.println("register entered");
        String jwtToken = authService.register(user);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, jwtToken).build();
    }


}
