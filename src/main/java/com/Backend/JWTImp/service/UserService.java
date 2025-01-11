package com.Backend.JWTImp.service;

import com.Backend.JWTImp.model.User;
import com.Backend.JWTImp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userrepository;

    public UserService(UserRepository userrepository, PasswordEncoder passwordEncoder) {
        this.userrepository = userrepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user) {
         userrepository.save(user);
    }

    public List<User> getAllUser() {
        List<User> users = (List<User>) userrepository.findAll();
        return users;
    }

    public User getUserById(String id) {
        Optional<User> User =  userrepository.findById(id);
        if(User.isPresent()){
            return User.get();
        }else {
            return null;
        }
    }

    public User updateUser(User user, String id) {
        Optional<User> existingUser = userrepository.findById(id);
        if(existingUser.isPresent()){
            existingUser.get().setUsername(user.getUsername()!=null?user.getUsername():existingUser.get().getUsername());
            existingUser.get().setEmail(user.getEmail()!=null?user.getEmail():existingUser.get().getEmail());
            existingUser.get().setPassword(user.getPassword()!=null?passwordEncoder.encode(user.getPassword()):existingUser.get().getPassword());
            // save
            userrepository.save(existingUser.get());
            return existingUser.orElse(null);
        }
        return null;
    }

public void deleteUser(String id) {
    Optional<User> User =  userrepository.findById(id);
    if(User.isPresent()){
        userrepository.deleteById(id);
    }else {
        throw new RuntimeException("User not found");
    }
}

}

