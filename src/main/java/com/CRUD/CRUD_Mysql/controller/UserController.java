package com.CRUD.CRUD_Mysql.controller;


import com.CRUD.CRUD_Mysql.model.User;
import com.CRUD.CRUD_Mysql.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userservice;
    public UserController(UserService userservice) {
        this.userservice = userservice;
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user){
        return new ResponseEntity<User>(userservice.saveUser(user), HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getAllUser(){
        return userservice.getAllUser();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") String UserID) {
        User user = userservice.getUserById(UserID);

        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            String errorMessage = "User not found";
            return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") String id,
                                             @RequestBody User user) {
        User updatedUser = userservice.updateUser(user, id);

        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            String errorMessage = "User update failed: User not found or invalid data";
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id){
        userservice.deleteUser(id);
        return new ResponseEntity<String>("User deleted Successfully.",HttpStatus.OK);
    }

}
