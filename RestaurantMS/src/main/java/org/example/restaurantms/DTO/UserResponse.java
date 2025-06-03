package org.example.restaurantms.DTO;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role
) {}
