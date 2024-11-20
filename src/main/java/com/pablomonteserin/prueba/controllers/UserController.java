package com.pablomonteserin.prueba.controllers;

import com.pablomonteserin.prueba.persistence.entities.User;
import com.pablomonteserin.prueba.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    @Autowired
private UserRepository userRepository;
    @GetMapping("/")
    public List<User> getUsers() {
List<User> users = userRepository.findAll();
return users;

    }
}
