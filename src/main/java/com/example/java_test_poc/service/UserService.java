package com.example.java_test_poc.service;
import com.example.java_test_poc.dto.UserRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String createUser(UserRequest userRequest) {
        return "User " + userRequest.getName() + " with email " + userRequest.getEmail() + " created successfully!";
    }
}
