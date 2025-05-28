package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.UserRepository;
import org.example.restaurantms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name="User")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Retrieve all Users", description = "Returns a list of all Users")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new User", description = "Allows to create a new User")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping("/create")
    public ResponseEntity<User> createUser(
            @RequestBody @Parameter(description = "User object that needs to be created") User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


}
