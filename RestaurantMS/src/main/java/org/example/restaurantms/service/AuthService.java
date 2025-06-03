package org.example.restaurantms.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantms.DTO.UserRequest;
import org.example.restaurantms.DTO.UserResponse;
import org.example.restaurantms.entity.RoleType;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email " + request.email() + " is already registered");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(RoleType.ROLE_USER);
        user.setFirst_name(request.first_name());
        user.setLast_name(request.last_name());
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());



        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }
}
