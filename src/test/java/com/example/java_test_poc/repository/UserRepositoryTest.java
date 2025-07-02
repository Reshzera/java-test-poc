package com.example.java_test_poc.repository;

import com.example.java_test_poc.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("should save and retrieve user by email")
    void shouldFindByEmail() {
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("should check if user exists by email")
    void shouldCheckExistsByEmail() {
        UserEntity user = new UserEntity();
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("jane@example.com");
        boolean notExists = userRepository.existsByEmail("nope@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
