package com.Backend.JWTImp.service;

import com.Backend.JWTImp.Dto.LoginDto;
import com.Backend.JWTImp.jwt.JwtUtils;
import com.Backend.JWTImp.model.Role;
import com.Backend.JWTImp.model.User;
import com.Backend.JWTImp.repository.RoleRepository;
import com.Backend.JWTImp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> generateTokens(String username, String role) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", jwtUtils.issueAccessToken(username, role));
        tokens.put("refresh_token", jwtUtils.issueRefreshToken(username));
        return tokens;
    }

    public Map<String, String> login(LoginDto loginDto){
        User user = userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String role = user.getRoles().stream()
                    .findFirst()
                    .map(Role::getName)
                    .orElse("ROLE_USER");

            return generateTokens(loginDto.getUsername(), role);
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public Map<String, String> register(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setId(newUser.getId());
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRoles(Set.of(role));

        userRepository.save(user);
        return generateTokens(user.getUsername(), role.getName());
    }
}
