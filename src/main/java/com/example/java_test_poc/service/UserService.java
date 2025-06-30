package com.example.java_test_poc.service;

import com.example.java_test_poc.dto.UserRequest;
import com.example.java_test_poc.dto.UserResponse;
import com.example.java_test_poc.entity.UserEntity;
import com.example.java_test_poc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = new UserEntity(userRequest.getName(), userRequest.getEmail());
        UserEntity savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }

    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }

    public Optional<UserResponse> updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    if (!user.getEmail().equals(userRequest.getEmail()) &&
                            userRepository.existsByEmail(userRequest.getEmail())) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                    user.setName(userRequest.getName());
                    user.setEmail(userRequest.getEmail());
                    UserEntity updatedUser = userRepository.save(user);
                    return new UserResponse(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
                });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}