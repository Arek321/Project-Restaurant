package org.example.restaurantms.service;

import lombok.RequiredArgsConstructor;
import org.example.restaurantms.entity.R_Table;
import org.example.restaurantms.entity.Reservation;
import org.example.restaurantms.entity.User;
import org.example.restaurantms.repository.R_TableRepository;
import org.example.restaurantms.repository.ReservationRepository;
import org.example.restaurantms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final R_TableRepository tableRepository;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, R_TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation createReservation(Long userId, Long tableId, LocalDateTime startTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        R_Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setR_table(table);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(2));

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with id: " + id));
        reservationRepository.delete(reservation);
    }
}
