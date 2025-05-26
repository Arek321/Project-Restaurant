package org.example.restaurantms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "restaurant_table")
public class R_Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int tableNumber;
    private int seatsNumber;

    @OneToMany(mappedBy = "r_table")
    private List<Reservation> reservations;
}
