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


}
