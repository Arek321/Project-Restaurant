package org.example.restaurantms.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantms.entity.RoleType;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


    public String registerUser(String username, String password, String email, String firstName, String lastName,
                               String phoneNumber, String address) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Użytkownik o podanej nazwie już istnieje.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFirst_name(firstName);
        user.setLast_name(lastName);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setRole(RoleType.ROLE_USER);

        userRepository.save(user);
        return "Użytkownik zarejestrowany pomyślnie.";
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User partiallyUpdateUser(Long id, User updates) {
        return userRepository.findById(id).map(user -> {
            if (updates.getUsername() != null) user.setUsername(updates.getUsername());
            if (updates.getPassword() != null) user.setPassword(updates.getPassword());
            if (updates.getEmail() != null) user.setEmail(updates.getEmail());
            if (updates.getFirst_name() != null) user.setFirst_name(updates.getFirst_name());
            if (updates.getLast_name() != null) user.setLast_name(updates.getLast_name());
            if (updates.getPhoneNumber() != null) user.setPhoneNumber(updates.getPhoneNumber());
            if (updates.getAddress() != null) user.setAddress(updates.getAddress());
            if (updates.getRole() != null) user.setRole(updates.getRole());

            return userRepository.save(user);
        }).orElse(null);
    }
}
