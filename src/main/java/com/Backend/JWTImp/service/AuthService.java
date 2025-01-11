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

    public String login(LoginDto loginDto){
        Optional<User> user = userRepository.findByUsername(loginDto.getUsername());
        if(user.isEmpty()){
            throw new RuntimeException("Invalid username");
        }

//        if(!user.get().getPassword().equals(loginDto.getPassword())){
//            throw new RuntimeException("Invalid password");
//        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.issueToken(loginDto.getUsername(),"ROLE_USER");
    }

    public String register(User newUser){
        Optional<User> user_check = userRepository.findByUsername(newUser.getUsername());
        if(user_check.isPresent()){
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setId(newUser.getId());
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(role));
        System.out.println(role);
        userRepository.save(user);
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                user.getUsername(),
//                user.getPassword()
//        ));
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.issueToken(newUser.getUsername(), role.getName());
    }
}
