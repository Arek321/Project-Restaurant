package org.example.restaurantms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String first_name;
    private String last_name;
    private String phoneNumber;
    private String address;
    @Enumerated(EnumType.STRING)
    private RoleType role;

}
