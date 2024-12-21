package com.CRUD.CRUD_Mysql.service;

import com.CRUD.CRUD_Mysql.model.User;
import com.CRUD.CRUD_Mysql.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userrepository;

    public UserService(UserRepository userrepository) {
        this.userrepository = userrepository;
    }

    public User saveUser(User user) {
        return userrepository.save(user);
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

        existingUser.get().setFirstName(user.getFirstName());
        existingUser.get().setLastName(user.getLastName());
        existingUser.get().setEmail(user.getEmail());
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
    }
}

}

