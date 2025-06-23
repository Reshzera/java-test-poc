package com.example.java_test_poc.controller;

import com.example.java_test_poc.dto.UserRequest;
import com.example.java_test_poc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public String createUser(@Valid @RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }
}
