package org.example.restaurantms.Service.UnitTests;

import org.example.restaurantms.entity.RoleType;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.UserRepository;
import org.example.restaurantms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("Should return all users")
    public void testGetAllUsers() {
        User user1 = new User();
        user1.setUsername("User1");

        User user2 = new User();
        user2.setUsername("User2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("TestUser", result.get().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create a new user")
    public void testCreateUser() {
        User newUser = new User();
        newUser.setUsername("NewUser");

        when(userRepository.save(newUser)).thenReturn(newUser);

        User createdUser = userService.createUser(newUser);

        assertNotNull(createdUser);
        assertEquals("NewUser", createdUser.getUsername());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    @DisplayName("Should delete an existing user by ID")
    public void testDeleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should partially update a user")
    public void testPartiallyUpdateUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("OldName");
        existingUser.setEmail("old@example.com");

        User updates = new User();
        updates.setUsername("NewName");
        updates.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.partiallyUpdateUser(userId, updates);

        assertNotNull(updatedUser);
        assertEquals("NewName", updatedUser.getUsername());
        assertEquals("new@example.com", updatedUser.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should return null when trying to update non-existing user")
    public void testPartiallyUpdateNonExistingUser() {
        Long userId = 99L;
        User updates = new User();
        updates.setUsername("DoesNotMatter");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.partiallyUpdateUser(userId, updates);

        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

}
