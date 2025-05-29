package org.example.restaurantms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @GetMapping("/get")
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

    @Operation(summary = "Delete a User", description = "Deletes a User by ID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "The ID of the User to delete", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a User by ID", description = "Returns a single User by their ID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/get/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "The ID of the User to retrieve", required = true)
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(
            summary = "Partially update a User",
            description = "Updates selected fields of an existing User.\nNOTE! Only change those values that you want to change!"
    )
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<User> partiallyUpdateUser(
            @Parameter(description = "ID of a User to patch")
            @PathVariable Long id,
            @RequestBody @Parameter(description = "User object with fields to update") User updates) {

        User updatedUser = userService.partiallyUpdateUser(id, updates);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
