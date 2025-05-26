package org.example.restaurantms.repository;

import org.example.restaurantms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
