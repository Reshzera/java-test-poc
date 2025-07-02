package com.example.java_test_poc.service;

import com.example.java_test_poc.dto.UserRequest;
import com.example.java_test_poc.dto.UserResponse;
import com.example.java_test_poc.entity.UserEntity;
import com.example.java_test_poc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private UserEntity userEntity;
    private UserResponse expectedResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("João Silva", "joao@email.com");
        userEntity = new UserEntity("João Silva", "joao@email.com");
        userEntity.setId(1L);
        expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com");
    }

    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        UserResponse result = userService.createUser(userRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getName(), result.getName());
        assertEquals(expectedResponse.getEmail(), result.getEmail());

        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getAllUsers_Success() {
        // Given
        UserEntity user2 = new UserEntity("Maria Santos", "maria@email.com");
        user2.setId(2L);
        List<UserEntity> users = Arrays.asList(userEntity, user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("João Silva", result.get(0).getName());
        assertEquals("Maria Santos", result.get(1).getName());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Found() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // When
        Optional<UserResponse> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedResponse.getId(), result.get().getId());
        assertEquals(expectedResponse.getName(), result.get().getName());
        assertEquals(expectedResponse.getEmail(), result.get().getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserResponse> result = userService.getUserById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByEmail_Found() {
        // Given
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(userEntity));

        // When
        Optional<UserResponse> result = userService.getUserByEmail("joao@email.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedResponse.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail("joao@email.com");
    }

    @Test
    void getUserByEmail_NotFound() {
        // Given
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // When
        Optional<UserResponse> result = userService.getUserByEmail("inexistente@email.com");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail("inexistente@email.com");
    }

    @Test
    void updateUser_Success() {
        // Given
        UserRequest updateRequest = new UserRequest("João Silva Atualizado", "joao.novo@email.com");
        UserEntity updatedEntity = new UserEntity("João Silva Atualizado", "joao.novo@email.com");
        updatedEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        // When
        Optional<UserResponse> result = userService.updateUser(1L, updateRequest);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva Atualizado", result.get().getName());
        assertEquals("joao.novo@email.com", result.get().getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("joao.novo@email.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUser_UserNotFound() {
        // Given
        UserRequest updateRequest = new UserRequest("João Silva", "joao@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserResponse> result = userService.updateUser(1L, updateRequest);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateUser_EmailAlreadyExists_ThrowsException() {
        // Given
        UserRequest updateRequest = new UserRequest("João Silva", "email.existente@email.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("email.existente@email.com")).thenReturn(true);

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, updateRequest)
        );

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("email.existente@email.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateUser_SameEmail_Success() {
        // Given
        UserRequest updateRequest = new UserRequest("João Silva Atualizado", "joao@email.com");
        UserEntity updatedEntity = new UserEntity("João Silva Atualizado", "joao@email.com");
        updatedEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        // When
        Optional<UserResponse> result = userService.updateUser(1L, updateRequest);

        // Then
        assertTrue(result.isPresent());
        assertEquals("João Silva Atualizado", result.get().getName());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_UserNotFound() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertFalse(result);
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}